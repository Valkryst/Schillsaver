package Schillsaver.job.encode;

import Schillsaver.job.Job;
import Schillsaver.mvc.controller.MainController;
import Schillsaver.mvc.model.MainModel;
import Schillsaver.mvc.view.MainView;
import Schillsaver.setting.BlockSize;
import Schillsaver.setting.FrameDimension;
import Schillsaver.setting.FrameRate;
import Schillsaver.setting.Settings;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FFMPEGEndec extends Endec {
    @Override
    public List<Thread> prepareEncodingJobs(final @NonNull MainController controller) {
        final MainModel model = (MainModel) controller.getModel();
        final MainView view = (MainView) controller.getView();

        final List<Thread> encodingJobs = new ArrayList<>();

        for (final Job job : model.getEncodingJobs()) {
            final Tab tab = view.addOutputTab(job.getName());

            final Thread thread = new Thread(() -> {
                tab.setClosable(false);

                // Prepare Files
                File inputFile = null;
                final File outputFile;

                try {
                    inputFile = job.getArchiver().archive(job.getName(), job.getFiles());
                    outputFile = new File(job.getOutputDirectory() + FilenameUtils.removeExtension(inputFile.getName()) + ".mp4");

                    // Construct FFMPEG Command:
                    final Settings settings = Settings.getInstance();

                    final FrameDimension frameDimension = FrameDimension.valueOf(settings.getStringSetting("Encoding Frame Dimensions"));
                    final int blockSize = BlockSize.valueOf(settings.getStringSetting("Encoding Block Size")).getBlockSize();
                    final FrameRate frameRate = FrameRate.valueOf(settings.getStringSetting("Encoding Frame Rate"));
                    final String codec = settings.getStringSetting("Encoding Codec");

                    final List<String> ffmpegCommands = new ArrayList<>();
                    ffmpegCommands.add(settings.getStringSetting("FFMPEG Executable Path"));

                    ffmpegCommands.add("-f");
                    ffmpegCommands.add("rawvideo");

                    ffmpegCommands.add("-pix_fmt");
                    ffmpegCommands.add("monob");

                    ffmpegCommands.add("-s");
                    ffmpegCommands.add((frameDimension.getWidth() / blockSize) + "x" + (frameDimension.getHeight() / blockSize));

                    ffmpegCommands.add("-r");
                    ffmpegCommands.add(String.valueOf(frameRate.getFrameRate()));

                    ffmpegCommands.add("-i");
                    ffmpegCommands.add(inputFile.getAbsolutePath());

                    ffmpegCommands.add("-vf");
                    ffmpegCommands.add("scale=iw*" + (blockSize) +":-1");

                    ffmpegCommands.add("-sws_flags");
                    ffmpegCommands.add("neighbor");

                    ffmpegCommands.add("-c:v");
                    ffmpegCommands.add(codec);

                    ffmpegCommands.add("-threads");
                    ffmpegCommands.add("8");

                    ffmpegCommands.add("-loglevel");
                    ffmpegCommands.add("verbose");

                    ffmpegCommands.add("-preset");
                    ffmpegCommands.add("veryfast");

                    ffmpegCommands.add("-y");
                    ffmpegCommands.add(outputFile.getAbsolutePath());

                    Platform.runLater(() -> ((TextArea) tab.getContent()).appendText(ffmpegCommands.toString()));

                    // Construct FFMPEG Process
                    final ProcessBuilder builder = new ProcessBuilder(ffmpegCommands);
                    builder.redirectErrorStream(true);

                    final Process process = builder.start();
                    Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));

                    // Run FFMPEG Process
                    try (
                        final InputStream is = process.getInputStream();
                        final InputStreamReader isr = new InputStreamReader(is);
                        final BufferedReader br = new BufferedReader(isr)
                    ) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            final String temp = line;
                            Platform.runLater(() -> ((TextArea) tab.getContent()).appendText(System.lineSeparator() + temp));
                        }
                    } catch (final IOException e) {
						e.printStackTrace();

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
					e.printStackTrace();

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
                    model.getJobs().remove(job.getName());
                    model.saveJobs();
                });
            });

            encodingJobs.add(thread);
        }

        return encodingJobs;
    }

    @Override
    public List<Thread> prepareDecodingJobs(final @NonNull MainController controller) {
        final MainModel model = (MainModel) controller.getModel();
        final MainView view = (MainView) controller.getView();

        final List<Thread> decodingJobs = new ArrayList<>();

        for (final Job job : model.getDecodingJobs()) {
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
                    final int blockSize = BlockSize.valueOf(Settings.getInstance().getStringSetting("Encoding Block Size")).getBlockSize();

                    final List<String> ffmpegCommands = new ArrayList<>();
                    ffmpegCommands.add(Settings.getInstance().getStringSetting("FFMPEG Executable Path"));

                    ffmpegCommands.add("-i");
                    ffmpegCommands.add(inputFile.getAbsolutePath());

                    ffmpegCommands.add("-vf");
                    ffmpegCommands.add("format=pix_fmts=monob,scale=iw*" + (1.0 / blockSize) + ":-1");

                    ffmpegCommands.add("-sws_flags");
                    ffmpegCommands.add("area");

                    ffmpegCommands.add("-loglevel");
                    ffmpegCommands.add("verbose");

                    ffmpegCommands.add("-f");
                    ffmpegCommands.add("rawvideo");

                    ffmpegCommands.add("-preset");
                    ffmpegCommands.add("veryfast");

                    ffmpegCommands.add("-y");
                    ffmpegCommands.add(outputFile.getAbsolutePath());

                    Platform.runLater(() -> ((TextArea) tab.getContent()).appendText(ffmpegCommands.toString()));

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
						e.printStackTrace();

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
                        model.getJobs().remove(job.getName());
                        model.saveJobs();
                    });
                });

                decodingJobs.add(thread);
            }
        }

        return decodingJobs;
    }
}
