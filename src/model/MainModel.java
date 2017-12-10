package model;

import configuration.Settings;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;
import misc.Job;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.MainView;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
                tab.setClosable(false);

                // Prepare Files:
                final File inputFile;
                final File outputFile;

                try {
                    inputFile = job.zipFiles();
                    outputFile = new File(job.getOutputDirectory() + FilenameUtils.removeExtension(inputFile.getName()) + ".mp4");
                } catch (final IOException e) {
                    final TextArea outputArea = ((TextArea) tab.getContent());
                    outputArea.appendText("Error:");
                    outputArea.appendText("\n\t" + e.getMessage());
                    outputArea.appendText("\n\tSee log file for more information.");

                    tab.setClosable(true);
                    return;
                }

                // Build FFMPEG:
                final FFmpeg ffmpeg;

                try {
                    ffmpeg = new FFmpeg(settings.getFfmpegPath());
                } catch (final IOException e) {
                    final TextArea outputArea = ((TextArea) tab.getContent());
                    outputArea.appendText("Error:");
                    outputArea.appendText("\n\t" + e.getMessage());
                    outputArea.appendText("\n\tSee log file for more information.");

                    tab.setClosable(true);
                    return;
                }

                // Build FFMPEG Settings:
                final FFmpegBuilder ffmpegBuilder = new FFmpegBuilder();
                ffmpegBuilder.setInput(inputFile.getAbsolutePath());

                ffmpegBuilder.overrideOutputFiles(true);
                ffmpegBuilder.addOutput(outputFile.getAbsolutePath())
                              .disableAudio()
                              .disableSubtitle()
                              .setVideoCodec("libx264")
                              .setVideoFrameRate(settings.getFrameRate().getFrameRate(), 1)
                              .done();

                // Build FFMPEG Executor:
                final FFmpegExecutor executor;

                try {
                    executor = new FFmpegExecutor(ffmpeg);
                } catch (final IOException e) {
                    final TextArea outputArea = ((TextArea) tab.getContent());
                    outputArea.appendText("Error:");
                    outputArea.appendText("\n\t" + e.getMessage());
                    outputArea.appendText("\n\tSee log file for more information.");

                    tab.setClosable(true);
                    return;
                }

                // Build FFMPEG Job:
                final TextArea outputArea = ((TextArea) tab.getContent());

                final FFmpegJob ffmpegJob = executor.createJob(ffmpegBuilder, progress -> {
                    final String text = String.format("status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
                                                         progress.status,
                                                         progress.frame,
                                                         FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                                                         progress.fps.doubleValue(),
                                                         progress.speed);
                    outputArea.appendText(text);
                });

                ffmpegJob.run();

                tab.setClosable(true);
            });

            encodingJobs.add(thread);
        }

        return encodingJobs;
    }

    public List<Thread> prepareDecodingJobs(final Settings settings, final MainView view) {
        final List<Thread> decodingJobs = new ArrayList<>();

        for (final Job job : getDecodingJobs()) {
            final Tab tab = view.addOutputTab(job.getName());

            for (final File inputFile : job.getFiles()) {
                final Thread thread = new Thread(() -> {
                    tab.setClosable(false);

                    System.err.println("DECODE STUFF NOT IMPLEMENTED, MAINMODEL");

                    inputFile.delete();
                    tab.setClosable(true);
                });

                decodingJobs.add(thread);
            }
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
