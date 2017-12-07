package misc;

import lombok.Getter;

import java.io.File;
import java.io.Serializable;
import java.util.List;

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
}
