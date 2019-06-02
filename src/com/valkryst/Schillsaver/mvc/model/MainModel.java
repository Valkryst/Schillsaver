package com.valkryst.Schillsaver.mvc.model;

import com.valkryst.Schillsaver.FileManager;
import com.valkryst.Schillsaver.job.Job;
import com.valkryst.Schillsaver.log.LogLevel;
import com.valkryst.Schillsaver.setting.Settings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainModel extends Model {
    private final static String JOBS_FILE_PATH = System.getProperty("user.dir") + "/Jobs.ser";

    /** The jobs. */
    @Getter @Setter private Map<String, Job> jobs = new HashMap<>();

    /** Deserializes the jobs map, if the file exists. */
    public void loadJobs() {
        final File file = new File(JOBS_FILE_PATH);

        if (file.exists()) {
            try {
                final Object object = FileManager.deserializeObjectWithGZIP(JOBS_FILE_PATH);
                jobs = (Map<String, Job>) object;
            } catch (final IOException e) {
                Settings.getInstance().getLogger().log(e, LogLevel.ERROR);
            } catch (final ClassNotFoundException e) {
                // Delete the file:
                try {
                    Files.delete(Paths.get(file.getAbsolutePath()));
                } catch (final IOException ee) {
                    Settings.getInstance().getLogger().log(ee, LogLevel.ERROR);
                }
            }
        }
    }

    /** Serializes the jobs map to a file. */
    public void saveJobs() {
        if (jobs.size() == 0) {
            // Delete the file:
            final File file = new File(JOBS_FILE_PATH);

            if (file.exists()) {
                try {
                    Files.delete(Paths.get(file.getAbsolutePath()));
                } catch (final IOException e) {
                    Settings.getInstance().getLogger().log(e, LogLevel.ERROR);
                }
            }

            return;
        }

        try {
            FileManager.serializeObjectWithGZIP(JOBS_FILE_PATH, jobs);
        } catch (final IOException e) {
            Settings.getInstance().getLogger().log(e, LogLevel.ERROR);
        }
    }

    /**
     * Retrieves an unmodifiable list of encoding jobs.
     *
     * The jobs are sorted from smallest filesize to largest filesize before
     * being returned.
     *
     * @return
     *         The list of encoding jobs.
     */
    public List<Job> getEncodingJobs() {
        final List<Job> encodingJobs = new ArrayList<>();

        for (final Job job : jobs.values()) {
            if (job.isEncodeJob()) {
                encodingJobs.add(job);
            }
        }

        encodingJobs.sort(Comparator.comparingLong(Job::getFileSize));

        return Collections.unmodifiableList(encodingJobs);
    }

    /**
     * Retrieves an unmodifiable list of decoding jobs.
     *
     * The jobs are sorted from smallest filesize to largest filesize before
     * being returned.
     *
     * @return
     *         The list of decoding jobs.
     */
    public List<Job> getDecodingJobs() {
        final List<Job> decodingJobs = new ArrayList<>();

        for (final Job job : jobs.values()) {
            if (job.isEncodeJob() == false) {
                decodingJobs.add(job);
            }
        }

        decodingJobs.sort(Comparator.comparingLong(Job::getFileSize));

        return Collections.unmodifiableList(decodingJobs);
    }
}
