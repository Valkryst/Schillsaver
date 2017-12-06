package misc;

import lombok.Data;

import java.io.File;
import java.util.*;

@Data
public class JobBuilder {
    /** The name of the Job. */
    private String name;
    /** The output directory. */
    private String outputDirectory;
    /** The file(s) belonging to the Job.*/
    private List<File> files;
    /** Whether the Job is an Encode Job or a Decode Job. */
    private boolean isEncodeJob = true;
    /** Whether to pack all of the files into a single archive before encoding. */
    private boolean singleArchive = false;

    /** Constructs a new JobBuilder. */
    public JobBuilder() {
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
     *
     * @throws java.lang.IllegalArgumentException
     *          If the output directory is empty.
     */
    private void checkState() {
        Objects.requireNonNull(outputDirectory);
        Objects.requireNonNull(files);

        if (name == null || name.isEmpty()) {
            final UUID uuid = UUID.randomUUID();
            name = uuid.toString();
        }

        if (outputDirectory.isEmpty()) {
            throw new IllegalArgumentException("The output directory of a Job cannot be empty.");
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
        outputDirectory = null;
        files = new ArrayList<>();
        isEncodeJob = true;
        singleArchive = false;
    }
}
