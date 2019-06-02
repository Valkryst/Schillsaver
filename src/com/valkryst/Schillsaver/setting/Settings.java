package com.valkryst.Schillsaver.setting;

import com.valkryst.Schillsaver.FileManager;
import com.valkryst.Schillsaver.log.LogLevel;
import com.valkryst.Schillsaver.log.Logger;
import com.valkryst.Schillsaver.log.SLF4JLogger;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Settings {
    /** The singleton instance. */
    private static Settings INSTANCE;

    /** The setting file path. */
    private final static String FILE_PATH = System.getProperty("user.dir") + "\\Settings.ser";

    /** The settings. */
    private final HashMap<String, String> settings;

    /** The logger. */
    @Getter private final Logger logger = SLF4JLogger.getInstance();

    /** Constructs a new Settings object. */
    private Settings() {
        HashMap<String, String> settings;

        // Attempt to load the setting from disk.
        try {
            settings = (HashMap<String, String>) FileManager.deserializeObjectWithGZIP(FILE_PATH);
        } catch (final IOException e) {
            logger.log(e, LogLevel.ERROR);

            // If there was an IO exception, then it is likely that the setting file is corrupt or incompatible
            // with the current version, so delete it.
            final File file = new File(FILE_PATH);

            if (file.exists()) {
                if (file.delete() == false) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to delete '" + FILE_PATH + "', please delete it manually.", ButtonType.OK);
                    alert.showAndWait();

                    logger.log("Unable to delete '" + FILE_PATH + "'.", LogLevel.ERROR);

                    System.exit(0);
                }
            }

            settings = new HashMap<>();
        } catch (final ClassNotFoundException e) {
            logger.log(e, LogLevel.ERROR);

            settings = new HashMap<>();
        }

        this.settings = settings;

        // If the setting are empty, then initialize the default setting.
        if (settings.size() == 0) {
            setSetting("FFMPEG Executable Path", "");

            setSetting("Default Encoding Output Directory", System.getProperty("user.dir"));
            setSetting("Default Decoding Output Directory", System.getProperty("user.dir"));

            setSetting("Encoding Codec", "libx264");
            setSetting("Encoding Frame Dimensions", FrameDimension.P720.name());
            setSetting("Encoding Frame Rate", FrameRate.FPS30.name());
            setSetting("Encoding Block Size", BlockSize.S8.name());
            save();
        }
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static Settings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Settings();
        }

        return INSTANCE;
    }

    /** Saves the setting to disk. */
    public void save() {
        if (settings.size() == 0) {
            // Delete the file:
            final File file = new File(FILE_PATH);

            if (file.exists()) {
                if (file.delete() == false) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to delete '" + FILE_PATH + "', please delete it manually.", ButtonType.OK);
                    alert.showAndWait();

                    logger.log("Unable to delete '" + FILE_PATH + "'.", LogLevel.ERROR);
                }
            }

            return;
        }

        try {
            FileManager.serializeObjectWithGZIP(FILE_PATH, settings);
        } catch (final IOException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to save settings.", ButtonType.OK);
            alert.showAndWait();

            logger.log(e, LogLevel.ERROR);
        }
    }

    /**
     * Retrieves a setting as a boolean.
     *
     * @param setting
     *         The setting.
     *
     * @return
     *         The value.
     *
     * @throws NumberFormatException
     *          If the setting string doesn't contain a parsable boolean.
     */
    public boolean getBooleanSetting(final @NonNull String setting) {
        return Boolean.valueOf(settings.get(setting));
    }

    /**
     * Retrieves a setting as a byte.
     *
     * @param setting
     *         The setting name.
     *
     * @return
     *         The value.
     *
     * @throws NullPointerException
     *          If the setting name is null.
     *
     * @throws NumberFormatException
     *          If the setting string doesn't contain a parsable byte.
     */
    public byte getByteSetting(final @NonNull String setting) {
        return Byte.valueOf(settings.get(setting));
    }

    /**
     * Retrieves a setting as a short.
     *
     * @param setting
     *         The setting name.
     *
     * @return
     *         The value.
     *
     * @throws NullPointerException
     *          If the setting name is null.
     *
     * @throws NumberFormatException
     *          If the setting string doesn't contain a parsable short.
     */
    public short getShortSetting(final @NonNull String setting) {
        return Short.valueOf(settings.get(setting));
    }

    /**
     * Retrieves a setting as a integer.
     *
     * @param setting
     *         The setting name.
     *
     * @return
     *         The value.
     *
     * @throws NullPointerException
     *          If the setting name is null.
     *
     * @throws NumberFormatException
     *          If the setting string doesn't contain a parsable integer.
     */
    public int getIntegerSetting(final @NonNull String setting) {
        return Integer.valueOf(settings.get(setting));
    }

    /**
     * Retrieves a setting as a long.
     *
     * @param setting
     *         The setting name.
     *
     * @return
     *         The value.
     *
     * @throws NullPointerException
     *          If the setting name is null.
     *
     * @throws NumberFormatException
     *          If the setting string doesn't contain a parsable long.
     */
    public long getLongSetting(final @NonNull String setting) {
        return Long.valueOf(settings.get(setting));
    }

    /**
     * Retrieves a setting as a float.
     *
     * @param setting
     *         The setting name.
     *
     * @return
     *         The value.
     *
     * @throws NullPointerException
     *          If the setting name is null.
     *
     * @throws NumberFormatException
     *          If the setting string doesn't contain a parsable float.
     */
    public float getFloatSetting(final @NonNull String setting) {
        return Float.valueOf(settings.get(setting));
    }

    /**
     * Retrieves a setting as a double.
     *
     * @param setting
     *         The setting name.
     *
     * @return
     *         The value.
     *
     * @throws NullPointerException
     *          If the setting name is null.
     *
     * @throws NumberFormatException
     *          If the setting string doesn't contain a parsable double.
     */
    public double getDoubleSetting(final @NonNull String setting) {
        return Double.valueOf(settings.get(setting));
    }

    /**
     * Retrieves a setting as a string.
     *
     * @param setting
     *         The setting name.
     *
     * @return
     *         The value.
     *
     * @throws NullPointerException
     *          If the setting name is null.
     */
    public String getStringSetting(final @NonNull String setting) {
        return settings.get(setting);
    }

    /**
     * Sets the value of a setting.
     *
     * @param key
     *         The setting.
     *
     * @param value
     *         The value.
     *
     * @throws NullPointerException
     *          If the key or value are null.
     */
    public void setSetting(final @NonNull String key, final @NonNull Object value) {
        settings.put(key, String.valueOf(value));
    }
}
