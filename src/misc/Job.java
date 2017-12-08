package misc;

import lombok.Getter;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Job implements Serializable {
    private static final long serialVersionUID = 0;

    /** The name of the Job. */
    @Getter private String name;
    /** The output directory. */
    @Getter private String outputDirectory;
    /** The file(s) belonging to the Job.*/
    @Getter private List<File> files;
    /** Whether the Job is an Encode Job or a Decode Job. */
    @Getter private boolean isEncodeJob = true;

    /**
     * Constructs a new Job.
     *
     * @param builder
     *          The builder
     */
    Job(final JobBuilder builder) {
        name = builder.getName();
        outputDirectory = builder.getOutputDirectory();
        files = builder.getFiles();
        isEncodeJob = builder.isEncodeJob();
    }

    /**
     * Zips all of the Job's files into a single archive.
     *
     * @return
     *         The zip file.
     *
     * @throws IOException
     *         If an I/O error occurs.
     */
    public File zipFiles() throws IOException {
        final File zipFile = new File(name + ".zip");

        final FileOutputStream fos = new FileOutputStream(zipFile);
        final ZipOutputStream zos = new ZipOutputStream(fos);

        byte[] buffer = new byte[32_768];

        for (final File file : files) {
            if (file.isDirectory() == false) {
                final ZipEntry entry = new ZipEntry(file.getName());
                final FileInputStream fis = new FileInputStream(file);

                zos.putNextEntry(entry);

                int read;
                while ((read = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, read);
                }

                zos.closeEntry();
                fis.close();
            }
        }

        zos.close();
        fos.close();

        return zipFile;
    }
}
