package configuration;

import misc.FrameDimension;
import misc.FrameRate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Dimension;
import java.io.*;
import java.util.HashMap;

public class Settings implements Serializable {
    private static final long serialVersionUID = 0;

    /** The settings. */
    private HashMap<String, String> settings = new HashMap<>();

    /** Construct a new Settings. */
    public Settings() {
        settings.put("Encoding Frame Dimensions", FrameDimension.P720.name());
        settings.put("Encoding Frame Rate", FrameRate.FPS30.name());
        settings.put("Encoding Block Size", String.valueOf(8));
        settings.put("Encoding Codec", "libx264");

        loadSettings();
        saveSettings();
    }

    /** Deserializes the settings, if the file exists. */
    private void loadSettings() {
        final String filePath = System.getProperty("user.dir") + "/Settings.ser";

        try (
            final FileInputStream fis = new FileInputStream(filePath);
            final ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            final Object object = ois.readObject();
            settings = (HashMap<String, String>) object;
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

    /** Serializes the settings to a file. */
    public void saveSettings() {
        final String filePath = System.getProperty("user.dir") + "/Settings.ser";

        if (settings.size() == 0) {
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
            oos.writeObject(settings);
        } catch (IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);
        }
    }

    /**
     * Retrieves the frame dimensions.
     *
     * @return
     *          The frame dimensions.
     */
    public FrameDimension getFrameDimensions() {
        return FrameDimension.valueOf(settings.get("Encoding Frame Dimensions"));
    }

    /**
     * Retrieves the frame rate.
     *
     * @return
     *          The frame rate.
     */
    public FrameRate getFrameRate() {
        return FrameRate.valueOf(settings.get("Encoding Frame Rate"));
    }

    /**
     * Retrieves the block dimensions.
     *
     * @return
     *          The block dimensions.
     */
    public Dimension getBlockDimensions() {
        final int size = Integer.valueOf(settings.get("Encoding Block Size"));
        return new Dimension(size, size);
    }

    /**
     * Retrieves the encoding codec.
     *
     * @return
     *          The encoding codec.
     */
    public String getCodec() {
        return settings.get("Encoding Codec");
    }
}
