package com.valkryst.Schillsaver.display.controller;

import com.valkryst.Schillsaver.archiver.Archiver;
import com.valkryst.Schillsaver.encoder.Encoder;
import com.valkryst.Schillsaver.display.Display;
import com.valkryst.Schillsaver.display.model.EncodeModel;
import com.valkryst.Schillsaver.display.model.SettingsTabModel;
import com.valkryst.VMVC.controller.Controller;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class EncodeController extends Controller<EncodeModel> {
    private Archiver archiver;

    /**
     * Constructs a new {@code EncodeController}.
     *
     * @param model {@link EncodeModel} associated with this controller.
     */
    public EncodeController(final @NonNull EncodeModel model) {
        super(model);
    }

    public void startEncoding(final List<Path> paths, final Runnable enableUi, final Runnable disableUi, final Consumer<String> updateProgress) {
        disableUi.run();

        try {
            final var settings = new SettingsTabModel();
            settings.validateSettings();
        } catch (final IllegalStateException e) {
            Display.displayWarning(null, e.getMessage());
            enableUi.run();
            return;
        } catch (final IOException e) {
            Display.displayError(null, e);
            enableUi.run();
            return;
        }

        new Thread(() -> {
            archiver = new Archiver(paths);
            archiver.setOnCompletion((final Path archivePath) -> {
                if (archiver.isInterrupted()) {
                    updateProgress.accept("Encoding cancelled.\n");
                } else {
                    updateProgress.accept("Encoding complete.\n\n");

                    final var encoder = new Encoder(archivePath);
                    encoder.setOnCompletion((final Path videoPath) -> {
                        if (encoder.isInterrupted()) {
                            updateProgress.accept("Encoding cancelled.\n");
                        } else {
                            updateProgress.accept("Encoding complete.\n");

                            try {
                                Files.deleteIfExists(archivePath);

                                // Move video file to output folder
                                final var settings = new SettingsTabModel();
                                Files.createDirectories(settings.getOutputFolderPath());
                                Files.move(videoPath, settings.getOutputFolderPath().resolve(videoPath.getFileName()));
                            } catch (final IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        enableUi.run();
                    });
                    encoder.setOnError((final Exception e) -> updateProgress.accept(e.getMessage() + "\n"));
                    encoder.setUpdateProgress(updateProgress);
                    encoder.start();
                }

                enableUi.run();
            });
            archiver.setOnError((final Exception e) -> updateProgress.accept(e.getMessage() + "\n"));
            archiver.setUpdateProgress(updateProgress);
            archiver.start();
        }).start();
    }

    public void stopEncoding(final Runnable enableUi) {
        if (archiver == null) {
            enableUi.run();
            return;
        }

        archiver.interrupt();
    }
}
