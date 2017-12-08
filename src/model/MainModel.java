package model;

import lombok.Getter;
import lombok.Setter;
import misc.Job;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class MainModel extends Model {
    /** The jobs. */
    @Getter @Setter private Map<String, Job> jobs = new HashMap<>();

    /** Deserializes the jobs map, if the file exists. */
    public void loadJobs() {
        final String filePath = System.getProperty("user.dir") + "/Jobs.ser";

        try (
            final FileInputStream fis = new FileInputStream(filePath);
            final ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            final Object object = ois.readObject();
            jobs = (Map<String, Job>) object;
        } catch (IOException | ClassNotFoundException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            // Delete the file:
            final File file = new File(filePath);

            if (file.exists()) {
                file.delete();
            }
        }
    }

    /** Serializes the jobs map to a file. */
    public void saveJobs() {
        final String filePath = System.getProperty("user.dir") + "/Jobs.ser";

        if (jobs.size() == 0) {
            // Delete the file:
            final File file = new File(filePath);

            if (file.exists()) {
                file.delete();
            }

            return;
        }

        try (
            final FileOutputStream fos = new FileOutputStream(filePath, false);
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(jobs);
        } catch (IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);
        }
    }

    /**
     * Retrieves an unmodifiable list of encoding jobs.
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

        return Collections.unmodifiableList(encodingJobs);
    }

    /**
     * Retrieves an unmodifiable list of decoding jobs.
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

        return Collections.unmodifiableList(decodingJobs);
    }
}
