package com.valkryst.Schillsaver.mvc.model;

import com.valkryst.Schillsaver.job.Job;
import com.valkryst.Schillsaver.job.JobBuilder;
import com.valkryst.Schillsaver.setting.Settings;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.util.List;

public class JobModel extends Model {
    /** The job. */
    @Getter private final Job job;

    /** Constructs a new JobModel. */
    public JobModel() {
        final JobBuilder builder = new JobBuilder();
        builder.setOutputDirectory(Settings.getInstance().getStringSetting("Default Encoding Output Directory"));
        builder.setEncodeJob(true);

        job = builder.build();
    }

    /**
     * Constructs a new JobModel.
     *
     * @param job
     *          The job represented by the model.
     */
    public JobModel(final @NonNull Job job) {
        this.job = job;
    }

    /**
     * Adds a file to the job's files.
     *
     * @param file
     *          The file.
     *
     * @throws NullPointerException
     *          If the file is null.
     */
    public void addFile(final @NonNull File file) {
        job.getFiles().add(file);
    }

    /**
     * Removes every file, from the job's files, whose file name matches the specified file name.
     *
     * @param fileName
     *          The file name to look for.
     *
     * @throws NullPointerException
     *          If the fileName is null.
     */
    public void removeFilesWithFilename(final @NonNull String fileName) {
        job.getFiles().removeIf(file -> file.getName().equals(fileName));
    }

    /**
     * Retrieves the job's files.
     *
     * @return
     *          An unmodifiable list of the job's files.
     */
    public List<File> getFiles() {
        return job.getFiles();
    }
}
