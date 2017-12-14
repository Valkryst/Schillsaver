package model;

import com.valkryst.VMVC.Settings;
import com.valkryst.VMVC.model.Model;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;
import misc.BlockSize;
import misc.FrameDimension;
import misc.FrameRate;
import misc.Job;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
                    inputFile = job.zipFiles(settings);
                    outputFile = new File(job.getOutputDirectory() + FilenameUtils.removeExtension(inputFile.getName()) + ".mp4");

                    // Construct FFMPEG String:
                    final FrameDimension frameDimension = FrameDimension.valueOf(settings.getStringSetting("Encoding Frame Dimensions"));
                    final Dimension blockSize = BlockSize.valueOf(settings.getStringSetting("Encoding Block Size")).getBlockSize();
                    final FrameRate frameRate = FrameRate.valueOf(settings.getStringSetting("Encoding Frame Rate"));
                    final String codec = settings.getStringSetting("Encoding Codec");

                    final StringBuilder sb = new StringBuilder();
                    final Formatter formatter = new Formatter(sb, Locale.US);

                    System.out.println(inputFile.getAbsolutePath());
                    System.out.println(outputFile.getAbsolutePath());
                    formatter.format("\"%s\" -f rawvideo -pix_fmt monob -s %dx%d -r %d -i \"%s\" -vf \"scale=iw*%d:-1\" -sws_flags neighbor -c:v %s -threads 8 -loglevel %s -y \"%s\"",
                                        settings.getStringSetting("FFMPEG Executable Path"),
                                        frameDimension.getWidth() / blockSize.width,
                                        frameDimension.getHeight() / blockSize.height,
                                        frameRate.getFrameRate(),
                                        inputFile.getAbsolutePath(),
                                        blockSize.width,
                                        codec,
                                        "verbose",
                                        outputFile.getAbsoluteFile());

                    Platform.runLater(() -> ((TextArea) tab.getContent()).appendText(System.lineSeparator() + System.lineSeparator() + sb.toString()));

                    // Construct FFMPEG Process:
                    final ProcessBuilder builder = new ProcessBuilder(sb.toString());
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
                        final Logger logger = LogManager.getLogger();
                        logger.error(e);

                        final TextArea outputArea = ((TextArea) tab.getContent());
                        outputArea.appendText("Error:");
                        outputArea.appendText("\n\t" + e.getMessage());
                        outputArea.appendText("\n\tSee log file for more information.");

                        process.destroy();
                        tab.setClosable(true);
                        return;
                    }


                    Platform.runLater(() -> ((TextArea) tab.getContent()).appendText("\n\nEncoding Complete"));
                } catch (final IOException e) {
                    final Logger logger = LogManager.getLogger();
                    logger.error(e);

                    final TextArea outputArea = ((TextArea) tab.getContent());
                    outputArea.appendText("Error:");
                    outputArea.appendText("\n\t" + e.getMessage());
                    outputArea.appendText("\n\tSee log file for more information.");

                    tab.setClosable(true);
                    return;
                }

                inputFile.delete();
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
