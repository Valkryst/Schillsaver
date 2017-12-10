package configuration;

import misc.BlockSize;
import misc.FrameDimension;
import misc.FrameRate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;

public class Settings implements Serializable {
    private static final long serialVersionUID = 0;

    /** The settings. */
    private HashMap<String, String> settings = new HashMap<>();

    /** Construct a new Settings. */
    public Settings() {
        settings.put("FFMPEG Path", "");

        settings.put("Total Encoding Threads", String.valueOf(1));
        settings.put("Total Decoding Threads", String.valueOf(1));

        settings.put("Encoding Frame Dimensions", FrameDimension.P720.name());
        settings.put("Encoding Frame Rate", FrameRate.FPS30.name());
        settings.put("Encoding Block Size", BlockSize.S8.name());
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
     * Retrieves the path to FFMPEG.
     *
     * @return
     *          The path to FFMPEG.
     */
    public String getFfmpegPath() {
        return settings.get("FFMPEG PAth");
    }

    /**
     * Retrieves the total number of encoding threads to use.
     *
     * @return
     *          The total number of encoding threads.
     */
    public int getTotalEncodingThreads() {
        return Integer.valueOf(settings.get("Total Encoding Threads"));
    }

    /**
     * Retrieves the total number of decoding threads to use.
     *
     * @return
     *          The total number of decoding threads.
     */
    public int getTotalDecodingThreads() {
        return Integer.valueOf(settings.get("Total Decoding Threads"));
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
    public BlockSize getBlockDimensions() {
        return BlockSize.valueOf(settings.get("Encoding Block Size"));
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

    /**
     * Sets the new path to FFMPEG.
     *
     * @param ffmpegPath
     *          The new path to FFMPEG.
     */
    public void setFfmpegPath(final String ffmpegPath) {
        if (ffmpegPath == null) {
            settings.put("FFMPEG Path", "");
        } else {
            settings.put("FFMPEG Path", ffmpegPath);
        }
    }

    /**
     * Sets the new total number of encoding threads.
     *
     * @param totalEncodingThreads
     *          The total number of encoding threads.
     */
    public void setTotalEncodingThreads(final int totalEncodingThreads) {
        if (totalEncodingThreads < 0) {
            settings.put("Total Encoding Threads", String.valueOf(1));
        } else {
            settings.put("Total Encoding Threads", String.valueOf(totalEncodingThreads));
        }
    }

    /**
     * Sets the new total number of decoding threads.
     *
     * @param totalDecodingThreads
     *          The total number of decoding threads.
     */
    public void setTotalDecodingThreads(final int totalDecodingThreads) {
        if (totalDecodingThreads < 0) {
            settings.put("Total Decoding Threads", String.valueOf(1));
        } else {
            settings.put("Total Decoding Threads", String.valueOf(totalDecodingThreads));
        }
    }

    /**
     * Sets the new frame dimensions.
     *
     * @param frameDimensions
     *          The frame dimensions.
     */
    public void setFrameDimensions(final FrameDimension frameDimensions) {
        if (frameDimensions == null) {
            settings.put("Encoding Frame Dimensions", FrameDimension.P720.name());
        } else {
            settings.put("Encoding Frame Dimensions", frameDimensions.name());
        }
    }

    /**
     * Sets the new frame rate.
     *
     * @param frameRate
     *          The frame rate.
     */
    public void setFrameRate(final FrameRate frameRate) {
        if (frameRate == null) {
            settings.put("Encoding Frame Rate", FrameRate.FPS30.name());
        } else {
            settings.put("Encoding Frame Rate", frameRate.name());
        }
    }

    /**
     * Sets the new block size.
     *
     * @param blockSize
     *          The block size.
     */
    public void setBlockDimensions(final BlockSize blockSize) {
        if (blockSize == null) {
            settings.put("Encoding Block Size", blockSize.S8.name());
        } else {
            settings.put("Encoding Block Size", blockSize.name());
        }
    }

    /**
     * Sets the new codec.
     *
     * @param codec
     *          The codec.
     */
    public void setCodec(final String codec) {
        if (codec == null || codec.isEmpty()) {
            settings.put("Encoding Codec", "libx264");
        } else {
            settings.put("Encoding Codec", codec);
        }
    }
}