package model;

import com.valkryst.VMVC.Settings;
import com.valkryst.VMVC.model.Model;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import misc.BlockSize;
import misc.FrameDimension;
import misc.FrameRate;
import misc.Job;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import view.MainView;

import java.awt.Dimension;
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
        } catch (final IOException | ClassNotFoundException e) {
            LogManager.getLogger().error(e);

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
        } catch (final IOException e) {
            LogManager.getLogger().error(e);
        }
    }

    /**
     * Prepares each of the encoding jobs with their own thread.
     *
     * @param settings
     *          The settings.
     *
     * @param view
     *          The view.
     *
     * @return
     *          The threads.
     *
     * @throws NullPointerException
     *         If the settings or view is null.
     */
    public List<Thread> prepareEncodingJobs(final @NonNull Settings settings, final @NonNull MainView view) {
        final List<Thread> encodingJobs = new ArrayList<>();

        for (final Job job : getEncodingJobs()) {
            final Tab tab = view.addOutputTab(job.getName());

            final Thread thread = new Thread(() -> {
                tab.setClosable(false);

                // Prepare Files:
                File inputFile = null;
                final File outputFile;

                try {
                    inputFile = job.zipFiles(settings);
                    outputFile = new File(job.getOutputDirectory() + FilenameUtils.removeExtension(inputFile.getName()) + ".mp4");

                    // Construct FFMPEG Command:
                    final FrameDimension frameDimension = FrameDimension.valueOf(settings.getStringSetting("Encoding Frame Dimensions"));
                    final Dimension blockSize = BlockSize.valueOf(settings.getStringSetting("Encoding Block Size")).getBlockSize();
                    final FrameRate frameRate = FrameRate.valueOf(settings.getStringSetting("Encoding Frame Rate"));
                    final String codec = settings.getStringSetting("Encoding Codec");

                    final List<String> ffmpegCommands = new ArrayList<>();
                    ffmpegCommands.add(settings.getStringSetting("FFMPEG Executable Path"));

                    ffmpegCommands.add("-f");
                    ffmpegCommands.add("rawvideo");

                    ffmpegCommands.add("-pix_fmt");
                    ffmpegCommands.add("monob");

                    ffmpegCommands.add("-s");
                    ffmpegCommands.add((frameDimension.getWidth() / blockSize.width) + "x" + (frameDimension.getHeight() / blockSize.height));

                    ffmpegCommands.add("-r");
                    ffmpegCommands.add(String.valueOf(frameRate.getFrameRate()));

                    ffmpegCommands.add("-i");
                    ffmpegCommands.add(inputFile.getAbsolutePath());

                    ffmpegCommands.add("-vf");
                    ffmpegCommands.add("scale=iw*" + (blockSize.width) +":-1");

                    ffmpegCommands.add("-sws_flags");
                    ffmpegCommands.add("neighbor");

                    ffmpegCommands.add("-c:v");
                    ffmpegCommands.add(codec);

                    ffmpegCommands.add("-threads");
                    ffmpegCommands.add("8");

                    ffmpegCommands.add("-loglevel");
                    ffmpegCommands.add("verbose");

                    ffmpegCommands.add("-y");
                    ffmpegCommands.add(outputFile.getAbsolutePath());

                    Platform.runLater(() -> ((TextArea) tab.getContent()).appendText(ffmpegCommands.toString()));

                    // Construct FFMPEG Process:
                    final ProcessBuilder builder = new ProcessBuilder(ffmpegCommands);
                    builder.redirectErrorStream(true);

                    final Process process = builder.start();
                    Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

                    // Run FFMPEG Process:
                    try (
                        final InputStream is = process.getInputStream();
                        final InputStreamReader isr = new InputStreamReader(is);
                        final BufferedReader br = new BufferedReader(isr);
                    ) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            final String temp = line;
                            Platform.runLater(() -> ((TextArea) tab.getContent()).appendText(System.lineSeparator() + temp));
                        }
                    } catch (final IOException e) {
                        LogManager.getLogger().error(e);

                        final TextArea outputArea = ((TextArea) tab.getContent());
                        Platform.runLater(() -> {
                            outputArea.appendText("\nError:");
                            outputArea.appendText("\n\t" + e.getMessage());
                            outputArea.appendText("\n\tSee log file for more information.");
                        });

                        process.destroy();
                        tab.setClosable(true);
                        return;
                    }


                    Platform.runLater(() -> ((TextArea) tab.getContent()).appendText("\n\nEncoding Complete"));
                } catch (final IOException e) {
                    LogManager.getLogger().error(e);

                    final TextArea outputArea = ((TextArea) tab.getContent());
                    Platform.runLater(() -> {
                        outputArea.appendText("\nError:");
                        outputArea.appendText("\n\t" + e.getMessage());
                        outputArea.appendText("\n\tSee log file for more information.");
                    });

                    if (inputFile != null) {
                        inputFile.delete();
                    }

                    tab.setClosable(true);
                    return;
                }

                inputFile.delete();
                tab.setClosable(true);

                Platform.runLater(() -> {
                    view.getJobsList().getItems().remove(job.getName());
                    getJobs().remove(job.getName());
                });
            });

            encodingJobs.add(thread);
        }

        return encodingJobs;
    }

    /**
     * Prepares each of the decoding jobs with their own thread.
     *
     * @param settings
     *          The settings.
     *
     * @param view
     *          The view.
     *
     * @return
     *          The threads.
     *
     * @throws NullPointerException
     *         If the settings or view is null.
     */
    public List<Thread> prepareDecodingJobs(final @NonNull Settings settings, final @NonNull MainView view) {
        final List<Thread> decodingJobs = new ArrayList<>();

        for (final Job job : getDecodingJobs()) {
            final Tab tab = view.addOutputTab(job.getName());

            for (final File inputFile : job.getFiles()) {
                final Thread thread = new Thread(() -> {
                    tab.setClosable(false);

                    // Prepare Files:
                    final File outputFile = new File(job.getOutputDirectory() + FilenameUtils.removeExtension(inputFile.getName()) + ".zip");

                    if (outputFile.exists()) {
                        if (outputFile.delete() == false) {
                            final TextArea outputArea = ((TextArea) tab.getContent());
                            Platform.runLater(() -> {
                                outputArea.appendText("\nError:");
                                outputArea.appendText("\n\tUnable to delete " + outputFile.getAbsolutePath());
                                outputArea.appendText("\n\tTry manually deleting the file, then re-running this decode job.");
                            });

                            tab.setClosable(true);
                            return;
                        }
                    }
                    // Construct FFMPEG Command:
                    final Dimension blockSize = BlockSize.valueOf(settings.getStringSetting("Encoding Block Size")).getBlockSize();

                    final List<String> ffmpegCommands = new ArrayList<>();
                    ffmpegCommands.add(settings.getStringSetting("FFMPEG Executable Path"));

                    ffmpegCommands.add("-i");
                    ffmpegCommands.add(inputFile.getAbsolutePath());

                    ffmpegCommands.add("-vf");
                    ffmpegCommands.add("format=pix_fmts=monob,scale=iw*" + (1.0 / blockSize.width) + ":-1");

                    ffmpegCommands.add("-sws_flags");
                    ffmpegCommands.add("area");

                    ffmpegCommands.add("-loglevel");
                    ffmpegCommands.add("verbose");

                    ffmpegCommands.add("-f");
                    ffmpegCommands.add("rawvideo");

                    ffmpegCommands.add("-y");
                    ffmpegCommands.add(outputFile.getAbsolutePath());

                    // Construct FFMPEG Process:
                    final ProcessBuilder builder = new ProcessBuilder(ffmpegCommands);
                    builder.redirectErrorStream(true);

                    Process process = null;

                    try {
                        process = builder.start();
                        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

                        // Run FFMPEG Process:
                        final InputStream is = process.getInputStream();
                        final InputStreamReader isr = new InputStreamReader(is);
                        final BufferedReader br = new BufferedReader(isr);

                        String line;
                        while ((line = br.readLine()) != null) {
                            final String temp = line;
                            Platform.runLater(() -> ((TextArea) tab.getContent()).appendText(System.lineSeparator() + temp));
                        }

                        br.close();
                        isr.close();
                        is.close();
                    } catch (final IOException e) {
                        LogManager.getLogger().error(e);

                        final TextArea outputArea = ((TextArea) tab.getContent());
                        Platform.runLater(() -> {
                            outputArea.appendText("\nError:");
                            outputArea.appendText("\n\t" + e.getMessage());
                            outputArea.appendText("\n\tSee log file for more information.");
                        });

                        if (process != null) {
                            process.destroy();
                        }

                        tab.setClosable(true);
                        return;
                    }

                    Platform.runLater(() -> ((TextArea) tab.getContent()).appendText("\n\nDecoding Complete"));

                    tab.setClosable(true);

                    Platform.runLater(() -> {
                        view.getJobsList().getItems().remove(job.getName());
                        getJobs().remove(job.getName());
                    });
                });

                decodingJobs.add(thread);
            }
        }

        return decodingJobs;
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
    private List<Job> getEncodingJobs() {
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
    private List<Job> getDecodingJobs() {
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
