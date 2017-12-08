package model;

import configuration.Settings;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;
import misc.Job;
import misc.VideoEncoder;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.MainView;

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

    public List<Thread> prepareEncodingJobs(final Settings settings, final MainView view) {
        final List<Thread> encodingJobs = new ArrayList<>();

        for (final Job job : getEncodingJobs()) {
            final Tab tab = view.addOutputTab(job.getName());

            final Thread thread = new Thread(() -> {
                try {
                    tab.setClosable(false);

                    final VideoEncoder videoEncoder = new VideoEncoder(settings, (TextArea) tab.getContent());

                    final File inputFile = job.zipFiles();
                    final File outputFile = new File(job.getOutputDirectory() + FilenameUtils.removeExtension(inputFile.getName()) + ".mp4");

                    videoEncoder.encode(inputFile, outputFile);

                    inputFile.delete();
                    tab.setClosable(true);
                } catch (final IOException | InterruptedException e) {
                    // todo Couldn't zip the files, abort, log, tell user.
                    // todo Could also be an exception when opening muxer in the vid encoder class.
                    final Logger logger = LogManager.getLogger();
                    logger.error(e);

                    e.printStackTrace();
                }
            });

            encodingJobs.add(thread);
        }

        return encodingJobs;
    }

    public List<Thread> prepareDecodingJobs(final Settings settings, final MainView view) {
        final List<Thread> decodingJobs = new ArrayList<>();

        for (final Job job : getDecodingJobs()) {
            final Tab tab = view.addOutputTab(job.getName());

            final Thread thread = new Thread(() -> {
                tab.setClosable(false);

                // todo WRITE DECODE CODE
                System.err.println("DECODE STUFF NOT WRITTEN IN MAINMODEL");
                System.exit(1);

                tab.setClosable(true);
            });

            decodingJobs.add(thread);
        }

        return decodingJobs;
    }

    /**
     * Retrieves an unmodifiable list of encoding jobs.
     *
     * @return
     *         The list of encoding jobs.
     */
    private List<Job> getEncodingJobs() {
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
    private List<Job> getDecodingJobs() {
        final List<Job> decodingJobs = new ArrayList<>();

        for (final Job job : jobs.values()) {
            if (job.isEncodeJob() == false) {
                decodingJobs.add(job);
            }
        }

        return Collections.unmodifiableList(decodingJobs);
    }
}
