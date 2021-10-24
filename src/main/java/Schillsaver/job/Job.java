package Schillsaver.job;

import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class Job implements Serializable {
    private static final long serialVersionUID = 1;

    /** The name of the Job. */
    @Getter private final String name;
    /** The output directory. */
    @Getter private final String outputDirectory;
    /** The archiver used to archive the file(s). */
    @Getter private final ZipArchiver archiver;
    /** The file(s) to process.*/
    @Getter private final List<File> files;
    /** Whether the Job is an Encode Job or a Decode Job. */
    @Getter private final boolean isEncodeJob;

    /**
     * Constructs a new Job.
     *
     * @param builder
     *          The builder
     *
     * @throws NullPointerException
     *          If the builder is null.
     */
    Job(final @NonNull JobBuilder builder) {
        name = builder.getName();
        outputDirectory = builder.getOutputDirectory();
        archiver = new ZipArchiver();
        files = builder.getFiles();
        isEncodeJob = builder.isEncodeJob();
    }

    /**
     * Retrieves the summed pre-padding pre-zipped file size of the job's files.
     *
     * @return
     *          The pre-padding pre-zipped file size of the job.
     */
    public long getFileSize() {
        long fileSize = 0;

        for (final File file : files) {
            fileSize += file.length();
        }

        return fileSize;
    }
}
