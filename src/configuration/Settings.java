package configuration;

import lombok.NonNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Settings {
    /** The settings. */
    private final HashMap<String, String> settings = new HashMap<>();

    public Settings() {
        settings.put("FFMPEG Path", "");
        settings.put("Compression Program Path", "");

        settings.put("Enc Format", "mkv");
        settings.put("Dec Format", "7z");

        settings.put("Enc Vid Width",  String.valueOf(1280));
        settings.put("Enc Vid Height",  String.valueOf(720));
        settings.put("Enc Vid Framerate", String.valueOf(30));
        settings.put("Enc Vid Macro Block Dimensions", String.valueOf(8));
        settings.put("Enc Library", "libvpx");

        settings.put("FFMPEG Log Level", "info");

        settings.put("Use Custom FFMPEG Options", String.valueOf(false));
        settings.put("Custom FFMPEG Enc Options", "");
        settings.put("Custom FFMPEG Dec Options", "");

        settings.put("Delete Source File When Enc", String.valueOf(false));
        settings.put("Delete Source File When Dec", String.valueOf(false));

        settings.put("Compression Commands", "a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on");
        settings.put("Compression Output Extension", "7z");

        settings.put("Warn If Settings Possibly Incompatible With YouTube", String.valueOf(true));

        validateSettings();
    }

    /**
     * Ensures the settings are in a valid state.
     *
     * @throws IllegalStateException
     *          If the settings are in an invalid state.
     */
    public void validateSettings() throws IllegalStateException {
        if (getStringSetting("Enc Format").isEmpty()) {
            throw new IllegalStateException("'Enc Format' cannot be empty/not set.");
        }

        if (getStringSetting("Dec Format").isEmpty()) {
            throw new IllegalStateException("'Dec Format' cannot be empty/not set.");
        }



        if (getIntegerSetting("Enc Vid Width") < 1) {
            throw new IllegalStateException("'Enc Vid Width' cannot be less than 1.");
        }

        if (getIntegerSetting("Enc Vid Height") < 1) {
            throw new IllegalStateException("'Enc Vid Height' cannot be less than 1.");
        }

        if (getIntegerSetting("Enc Vid Framerate") < 1) {
            throw new IllegalStateException("'Enc Vid Framerate' cannot be less than 1.");
        }

        if (getIntegerSetting("Enc Vid Macro Block Dimensions") < 1) {
            throw new IllegalStateException("'Enc Vid Macro Block Dimensions' cannot be less than 1.");
        }

        if (getStringSetting("Enc Library").isEmpty()) {
            throw new IllegalStateException("'Enc Library' cannot be empty/not set.");
        }



        if (getStringSetting("FFMPEG Log Level").isEmpty()) {
            throw new IllegalStateException("'FFMPEG Log Level' cannot be empty/not set.");
        }



        if (getStringSetting("Compression Output Extension").isEmpty()) {
            throw new IllegalStateException("'Compression Output Extension' cannot be empty/not set.");
        }

        /*
         * We want to calculate the frame size in bytes given a resolution (width x height).
         *
         * width * height = Total pixels in frame.
         *
         * Each pixel is scaled up by a factor of (8 * 8) to ensure
         * the video uses 8x8 blocks for each pixel, or 64 pixels
         * per bit.
         *
         * Each input byte is a set of 8 1-bit pixels.
         * Therefore 1 byte = 8 pixels.
         * This is where the "/ 8" comes from in the last step.
         */
        int frameSize = (getIntegerSetting("Enc Vid Width") * getIntegerSetting("Enc Vid Height"));
        frameSize /= (getIntegerSetting("Enc Vid Macro Block Dimensions") * getIntegerSetting("Enc Vid Macro Block Dimensions"));
        frameSize /= Byte.SIZE;// / 8
        setSetting("Frame Size", frameSize);
    }

    /**
     * Writes the settings to a JSON file.
     *
     * @throws IOException
     *          If the names file exists but is a directory rather than a
     *          regular file, does not exist but cannot be created, or
     *          cannot be opened for any other reason.
     */
    public void writeToFile() throws IOException {
        final JSONObject jsonObject = new JSONObject();

        for (final Map.Entry<String, String> entry : settings.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }

        final FileWriter fileWriter = new FileWriter("config.json");
        fileWriter.write(jsonObject.toJSONString());
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * Loads the settings from a the config file.
     *
     * Creates the config file if it doesn't already exist.
     *
     * @throws ParseException
     *          If an error occurs while parsing the JSON.
     */
    public void loadFromFile() throws IOException, ParseException {
        try {
            final JSONParser jsonParser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("config.json"));

            jsonObject.forEach((key, value) -> {
                if (value instanceof String == false) {
                    settings.put((String) key, value.toString());
                } else {
                    settings.put((String) key, (String) value);
                }
            });
        } catch (final IOException e) {
            writeToFile();
        }

        validateSettings();
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
     *         The setting.
     *
     * @return
     *         The value.
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
     *         The setting.
     *
     * @return
     *         The value.
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
     *         The setting.
     *
     * @return
     *         The value.
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
     *         The setting.
     *
     * @return
     *         The value.
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
     *         The setting.
     *
     * @return
     *         The value.
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
     *         The setting.
     *
     * @return
     *         The value.
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
     *         The setting.
     *
     * @return
     *         The value.
     */
    public String getStringSetting(final @NonNull String setting) {
        return settings.get(setting);
    }

    /**
     * Sets the value of a setting.
     *
     * @param setting
     *         The setting.
     *
     * @param value
     *         The value.
     */
    public void setSetting(final @NonNull String setting, final @NonNull Object value) {
        settings.put(setting, String.valueOf(value));
    }
}
