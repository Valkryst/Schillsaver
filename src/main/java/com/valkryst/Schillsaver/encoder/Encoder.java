package com.valkryst.Schillsaver.encoder;

import com.valkryst.Schillsaver.display.model.SettingsTabModel;
import lombok.NonNull;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Encoder extends Thread {
    /** Formatter used to generate unique output file names. */
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /** Consumer to call when encoding is complete. */
    @Setter private Consumer<Path> onCompletion = (final Path path) -> {};

    /** Consumer to call if an error occurs. */
    @Setter private Consumer<Exception> onError = (final Exception e) -> {};

    /** Consumer to call when the progress needs to be updated. */
    @Setter private Consumer<String> updateProgress = (final String progress) -> {};

    /** Path of the file to encode. */
    private final Path inputFilePath;

    public Encoder(final @NonNull Path inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    // todo Handle interruption and delete temp files.
    public void encode() throws IOException {
        final var outputDirectory = Files.createTempDirectory(UUID.randomUUID().toString());
        final var outputFilePath = outputDirectory.resolve(LocalDateTime.now().format(DATE_TIME_FORMATTER) + ".mp4");
        updateProgress.accept("Created temporary directory: " + outputDirectory + "\n");

        final var process = getFfmpegProcess(outputFilePath);

        try (
            final var inputStream = process.getInputStream();
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                updateProgress.accept(line + "\n");
            }
        } catch (final IOException e) {
            updateProgress.accept("An error occurred while encoding the file:\n");
            updateProgress.accept("\t" + e.getMessage() + "\n");
            onError.accept(e);

            Files.deleteIfExists(outputDirectory);
            return;
        } finally {
            process.destroy();
        }

        if (!Files.exists(outputFilePath)) {
            onError.accept(new IOException("An error occurred while encoding the file."));
            Files.deleteIfExists(outputDirectory);
            return;
        }

        onCompletion.accept(outputFilePath);
    }

    @Override
    public void run() {
        try {
            encode();
        } catch (final IOException e) {
            onError.accept(e);
        }
    }

    private List<String> getFfmpegCommand(final @NonNull Path outputFilePath) throws IOException {
        final var settings = new SettingsTabModel();

        final var blockSize = settings.getBlockSize();
        final var resolution = settings.getResolution();
        final var frameRate = settings.getFramerate();

        final var command = new ArrayList<String>();
        command.add("ffmpeg");
        command.add("-hwaccel");
        command.add("auto");
        command.add("-f");
        command.add("rawvideo");
        command.add("-pix_fmt");
        command.add("monob");
        command.add("-s");
        command.add((resolution.width / blockSize.blockSize) + "x" + (resolution.height / blockSize.blockSize));
        command.add("-r");
        command.add(String.valueOf(frameRate.frameRate));
        command.add("-i");
        command.add(inputFilePath.toString());
        command.add("-vf");
        command.add("scale=iw*" + blockSize.blockSize + ":-1");
        command.add("-sws_flags");
        command.add("neighbor");
        command.add("-c:v");
        command.add(settings.getCodec());
        command.add("-loglevel");
        command.add("verbose");
        command.add("-preset");
        command.add("veryfast");
        command.add("-y");
        command.add(outputFilePath.toString());
        return command;
    }

    private Process getFfmpegProcess(final @NonNull Path outputFilePath) throws IOException {
        final var ffmpegCommand = getFfmpegCommand(outputFilePath);
        updateProgress.accept("FFMPEG Command: " + String.join(" ", ffmpegCommand) + "\n");

        final var processBuilder = new ProcessBuilder(ffmpegCommand);
        processBuilder.redirectErrorStream(true);

        final var process = processBuilder.start();
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy)); // todo We should remove the hook when the process is finished.

        return process;
    }
}
