package com.valkryst.Schillsaver.display.model;

import com.google.gson.JsonObject;
import com.valkryst.Schillsaver.display.Display;
import com.valkryst.Schillsaver.display.controller.SettingsTabController;
import com.valkryst.Schillsaver.display.view.SettingsTabView;
import com.valkryst.Schillsaver.io.FileIO;
import com.valkryst.Schillsaver.io.FolderIO;
import com.valkryst.Schillsaver.setting.BlockSize;
import com.valkryst.Schillsaver.setting.FrameRate;
import com.valkryst.Schillsaver.setting.FrameResolution;
import com.valkryst.Schillsaver.setting.SwingTheme;
import com.valkryst.VMVC.model.Model;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class SettingsTabModel extends Model<SettingsTabController, SettingsTabView> {
    /** The path to the settings file. */
    private static final Path FILE_PATH = FileIO.getFilePath(FolderIO.getFolderPath("Settings"), "settings.json");

    /** Path to output folder. */
    @Setter private Path outputFolderPath = Path.of("");

    /** Encoding codec. */
    @Setter private String codec = "libx264";

    /** Video resolution. */
    @Setter private FrameResolution resolution = FrameResolution.P1080;

    /** Video framerate. */
    @Setter private FrameRate framerate = FrameRate.FPS_30;

    /** Video block size. */
    @Setter private BlockSize blockSize = BlockSize.S6;

    /** Swing theme to use. */
    @Setter private SwingTheme swingTheme = SwingTheme.DARK;

    /**
     * Constructs a new {@code SettingsTabModel}.
     *
     * @throws IOException If an I/O exception occurs while loading the settings.
     */
    public SettingsTabModel() throws IOException {
        load();
    }

    @Override
    protected SettingsTabController createController() {
        return new SettingsTabController(this);
    }

    @Override
    protected SettingsTabView createView(final @NonNull SettingsTabController controller) {
        return new SettingsTabView(controller);
    }

    /**
     * Saves the settings to disk.
     *
     * @throws IOException If an I/O exception occurs while saving the settings.
     */
    public void save() throws IOException {
        final var json = new JsonObject();
        json.addProperty("outputFolderPath", outputFolderPath.toString());
        json.addProperty("codec", codec);
        json.addProperty("resolution", resolution.name());
        json.addProperty("framerate", framerate.name());
        json.addProperty("blockSize", blockSize.name());
        json.addProperty("theme", swingTheme.name());

        FileIO.saveJsonToDisk(FILE_PATH, json);
    }

    public void load() throws IOException {
        if (Files.notExists(FILE_PATH)) {
            save();
        }

        if (!this.isFfmpegAvailable()) {
            Display.displayWarning(null, "FFmpeg is not installed or not available in the PATH. Please install FFmpeg to use this application.");
            return;
        }

        final var json = FileIO.loadJsonFromDisk(FILE_PATH);

        outputFolderPath = Path.of(loadSetting(json, "outputFolderPath", ""));
        codec = loadSetting(json, "codec", "libx264");
        resolution = FrameResolution.valueOf(loadSetting(json, "resolution", FrameResolution.P1080.name()));
        framerate = FrameRate.valueOf(loadSetting(json, "framerate", FrameRate.FPS_30.name()));
        blockSize = BlockSize.valueOf(loadSetting(json, "blockSize", BlockSize.S6.name()));
        swingTheme = SwingTheme.valueOf(loadSetting(json, "theme", SwingTheme.DARK.name()));

        save();
    }

    private String loadSetting(final @NonNull JsonObject json, final @NonNull String key, final @NonNull String defaultValue) {
        final var element = json.get(key);
        final var value = element == null ? "" : element.getAsString();
        return value.isEmpty() ? defaultValue : value;
    }

    /**
     * Attempts to validate the settings and throws an exception, with a descriptive message, if a setting is invalid.
     *
     * @throws IllegalStateException If a setting is invalid.
     */
    public void validateSettings() throws IllegalStateException {
        if (outputFolderPath == null || outputFolderPath.toString().isBlank()) {
            throw new IllegalStateException("The path to the output folder is blank. Please select it in the Settings menu.");
        }
    }

    /**
     * Attempts to determine whether FFmpeg is available in the system PATH.
     *
     * @return Whether FFmpeg is available in the system PATH.
     */
    private boolean isFfmpegAvailable() {
        try {
            final var process = new ProcessBuilder("ffmpeg", "-version").start();
            return process.waitFor() == 0;
        } catch (final IOException | InterruptedException e) {
            return false;
        }
    }
}
