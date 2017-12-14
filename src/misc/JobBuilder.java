package misc;

import com.valkryst.VMVC.Settings;
import lombok.Data;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Data
public class JobBuilder {
    /** The program settings. */
    private Settings settings;

    /** The name of the Job. */
    private String name;
    /** The output directory. */
    private String outputDirectory;
    /** The file(s) belonging to the Job.*/
    private List<File> files;
    /** Whether the Job is an Encode Job or a Decode Job. */
    private boolean isEncodeJob = true;

    /**
     * Constructs a new JobBuilder.
     *
     * @param settings
     *          The program settings.
     */
    public JobBuilder(final Settings settings) {
        this.settings = settings;
        reset();
    }

    /**
     * Build a new job.
     *
     * @return
     *         The job.
     */
    public Job build() {
        checkState();
        return new Job(this);
    }

    /**
     * Checks the state of the builder.
     *
     * @throws java.lang.NullPointerException
     *          If the output directory or files list is null.
     */
    private void checkState() {
        Objects.requireNonNull(outputDirectory);
        Objects.requireNonNull(files);

        if (name == null || name.isEmpty()) {
            final UUID uuid = UUID.randomUUID();
            name = uuid.toString();
        }

        // If the output dir isn't specified, try setting it to the home dir.
        if (outputDirectory.isEmpty()) {
            setOutputToHomeDirectory();
        }

        // Ensure output directory has the correct trailing slash:
        if (!outputDirectory.endsWith("\\") && !outputDirectory.endsWith("/")) {
            outputDirectory += "/";
        }

        // Ensure the files are sorted from smallest to largest:
        this.files.sort(Comparator.comparing(File::length));
    }

    /** Resets the state of the builder. */
    public void reset() {
        name = null;
        setOutputToHomeDirectory();
        files = new ArrayList<>();
        isEncodeJob = true;
    }

    /** Sets the output directory to the home directory. */
    private void setOutputToHomeDirectory() {
        if (isEncodeJob) {
            final String defaultEncodeDir = settings.getStringSetting("Default Encoding Output Directory");

            if (defaultEncodeDir.isEmpty() == false) {
                final File file = new File(defaultEncodeDir);
                if (file.exists() && file.isDirectory()) {
                    outputDirectory = defaultEncodeDir;
                }
            }
        } else {
            final String defaultDecodeDir = settings.getStringSetting("Default Decoding Output Directory");

            if (defaultDecodeDir.isEmpty() == false) {
                final File file = new File(defaultDecodeDir);
                if (file.exists() && file.isDirectory()) {
                    outputDirectory = defaultDecodeDir;
                }
            }
        }

        try {
            final File home = FileSystemView.getFileSystemView().getHomeDirectory();
            outputDirectory = home.getCanonicalPath() + "/";
        } catch (final IOException e) {
            // todo Throw an error or something if this happens, not sure yet.
            e.printStackTrace();
        }
    }
}
