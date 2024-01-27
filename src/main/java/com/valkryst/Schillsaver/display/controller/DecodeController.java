package Schillsaver.display.controller;

import Schillsaver.decoder.Decoder;
import Schillsaver.display.Display;
import Schillsaver.display.model.DecodeModel;
import Schillsaver.display.model.SettingsTabModel;
import com.valkryst.VMVC.controller.Controller;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class DecodeController extends Controller<DecodeModel> {
    /**
     * Constructs a new {@code DecodeController}.
     *
     * @param model {@link DecodeModel} associated with this controller.
     */
    public DecodeController(final @NonNull DecodeModel model) {
        super(model);
    }

    public void startDecoding(final Path inputPath, final Runnable enableUi, final Runnable disableUi, final Consumer<String> updateProgress) {
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

        final var decoder = new Decoder(inputPath);
        decoder.setOnCompletion((final Path videoPath) -> {
            if (decoder.isInterrupted()) {
                updateProgress.accept("Decoding cancelled.\n");
            } else {
                updateProgress.accept("Decoding complete.\n");

                try {
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
        decoder.setOnError((final Exception e) -> updateProgress.accept(e.getMessage() + "\n"));
        decoder.setUpdateProgress(updateProgress);
        decoder.start();
    }

    public void stopDecoding(final Runnable enableUi) {
        enableUi.run();
    }
}
