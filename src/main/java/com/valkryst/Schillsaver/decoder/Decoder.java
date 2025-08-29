package com.valkryst.Schillsaver.decoder;

import com.valkryst.Schillsaver.display.model.SettingsTabModel;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Decoder extends Thread {
    /** Consumer to call when decoding is complete. */
    @Setter private Consumer<Path> onCompletion = (final Path path) -> {};

    /** Consumer to call if an error occurs. */
    @Setter private Consumer<Exception> onError = (final Exception e) -> {};

    /** Consumer to call when the progress needs to be updated. */
    @Setter private Consumer<String> updateProgress = (final String progress) -> {};

    /** Path of the file to decode. */
    private final Path inputFilePath;

    public Decoder(final @NonNull Path inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    // todo Handle interruption and delete temp files.
    public void decode() throws IOException {
        final var outputDirectory = Files.createTempDirectory(UUID.randomUUID().toString());
        final var outputFilePath = outputDirectory.resolve(FilenameUtils.getBaseName(inputFilePath.toString()) + ".zip");
        updateProgress.accept("Created temporary directory: " + outputDirectory + "\n");

        final var process = getFfmpegProcess(outputFilePath);

        try (
            final var inputStream = process.getInputStream();
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader);
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
            onError.accept(new IOException("An error occurred while decoding the file."));
            Files.deleteIfExists(outputDirectory);
            return;
        }

        onCompletion.accept(outputFilePath);
    }

    @Override
    public void run() {
        try {
            decode();
        } catch (final IOException e) {
            onError.accept(e);
        }
    }

    private List<String> getFfmpegCommand(final @NonNull Path outputFilePath) throws IOException {
        final var settings = new SettingsTabModel();
        final var blockSize = settings.getBlockSize();

        final var command = new ArrayList<String>();
        command.add("ffmpeg");
        command.add("-hwaccel");
        command.add("auto");
        command.add("-i");
        command.add(inputFilePath.toString());
        command.add("-vf");
        command.add("format=pix_fmts=monob,scale=iw*" + (1.0 / blockSize.blockSize) + ":-1");
        command.add("-sws_flags");
        command.add("area");
        command.add("-f");
        command.add("rawvideo");
        command.add("-loglevel");
        command.add("verbose");
        command.add("-preset");
        command.add("ultrafast");
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
