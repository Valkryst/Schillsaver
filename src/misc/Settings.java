package misc;

import lombok.NonNull;

import java.util.HashMap;

public class Settings {
    /** The settings. */
    private final HashMap<String, String> settings = new HashMap<>();

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
}
