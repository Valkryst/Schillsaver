package handler;

import eu.hansolo.enzo.notification.Notification;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;

public class ConfigHandler {
    /** The name of the configuration handler. */
    private static final String FILENAME_CONFIG = "config.json";

    /** The logging levels supported by FFMPEG as of 2015/Nov/1. */
    public static final String[] FFMPEG_LOG_LEVELS = {"quiet", "panic", "fatal", "error", "warning", "info", "verbose", "debug", "trace"};

    /** The absolute path to ffmpeg/ffmpeg.exe. */
    @Getter @Setter private String ffmpegPath = "";
    /** The absolute path to 7zip/7zip.exe or whichever compression program is specified. */
    @Getter @Setter private String compressionProgramPath = "";

    /** The format to encode to. */
    @Getter @Setter  private String encodeFormat = "mkv";
    /** The format to decode to. */
    @Getter @Setter private String decodeFormat = "7z";

    /** The width, in pixels, of the encoded video. */
    @Getter private int encodedVideoWidth;
    /** The height, in pixels, of the encoded video. */
    @Getter private int encodedVideoHeight;
    /** The framerate of the video. Ex ~ 30fps, 60fps, etc... */
    @Getter private int encodedFramerate;
    /** The size of each frame of video in bytes. */
    @Getter private int frameSize;
    /** The width/height of each encoded macroblock. */
    @Getter private int macroBlockDimensions;
    /** The codec to encode/decode the video with. */
    @Getter @Setter private String encodingLibrary = "libvpx";
    /** The level of information that should be given by ffmpeg while ffmpeg is running. */
    @Getter @Setter private String ffmpegLogLevel = "info";

    /** Whether or not to ignore all other ffmpeg options and to use the fullyCustomFfmpegEncodingOptions and fullyCustomFfmpegDecodingOptions instead. */
    @Getter @Setter private boolean useFullyCustomFfmpegOptions;
    /** The user-entered command line arguments to use when encoding with ffmpeg. */
    @Getter @Setter private String fullyCustomFfmpegEncodingOptions;
    /** The user-entered command line arguments to use when encoding with ffmpeg. */
    @Getter @Setter private String fullyCustomFfmpegDecodingOptions;

    /** Whether or not to delete the source file after encoding. */
    @Getter @Setter private boolean deleteSourceFileWhenEncoding ;
    /** whether or not to delete the osource file after decoding. */
    @Getter @Setter private boolean deleteSourceFileWhenDecoding ;

    /** Whether or not to show the splash screen on startup. */
    @Getter @Setter private boolean showSplashScreen;
    /** The absolute path to the splash screen to display. */
    @Getter @Setter private String splashScreenFilePath;
    /** The amount of time, in milliseconds, to display the splash screen. */
    @Getter private int splashScreenDisplayTime;

    /** The base commands to use when compressing a handler before encoding. */
    @Getter @Setter private String compressionCommands;

    /** Whether or not to check for program updates on program start. */
    @Getter @Setter private boolean checkForUpdates = false;

    /** Whether or not to warn the user if their settings may not work with YouTube. */
    @Getter @Setter private boolean warnUserIfSettingsMayNotWorkForYouTube = true;

    /**
     * Reads in each line from the configuration handler and attempts to parse
     * the specified parameters of the program.
     *
     * If a line cannot be parsed, then a warning is logged.
     */
    public void loadConfigSettings() {
        try (
                final InputStream inputStream = new FileInputStream("config.json");
                final JsonReader reader = Json.createReader(inputStream);
        ) {
            final JsonObject configFile = reader.readObject();

            ffmpegPath = configFile.getString("FFMPEG Path");
            compressionProgramPath = configFile.getString("Compression Program Path");

            encodeFormat = configFile.getString("Enc Format");
            decodeFormat = configFile.getString("Dec Format");

            encodedVideoWidth = configFile.getInt("Enc Vid Width");
            encodedVideoHeight = configFile.getInt("Enc Vid Height");
            encodedFramerate = configFile.getInt("Enc Vid Framerate");
            macroBlockDimensions = configFile.getInt("Enc Vid Macro Block Dimensions");
            encodingLibrary = configFile.getString("Enc Library");

            ffmpegLogLevel = configFile.getString("FFMPEG Log Level");

            useFullyCustomFfmpegOptions = configFile.getBoolean("Use Custom FFMPEG Options");
            fullyCustomFfmpegEncodingOptions = configFile.getString("Custom FFMPEG Enc Options");
            fullyCustomFfmpegDecodingOptions = configFile.getString("Custom FFMPEG Dec Options");

            deleteSourceFileWhenEncoding = configFile.getBoolean("Delete Source File When Enc");
            deleteSourceFileWhenDecoding = configFile.getBoolean("Delete Source File When Dec");

            showSplashScreen = configFile.getBoolean("Show Splash Screen");
            splashScreenFilePath = configFile.getString("Splash Screen File Path");
            splashScreenDisplayTime = configFile.getInt("Splash Screen Display Time");

            compressionCommands = configFile.getString("Compression Commands");

            checkForUpdates = configFile.getBoolean("Check For Updates");

            warnUserIfSettingsMayNotWorkForYouTube = configFile.getBoolean("Warn If Settings Possibly Incompatible With YouTube");
        } catch(final IOException e) {
            createConfigFile();
            loadConfigSettings();
        } catch(final NullPointerException e) {
            ffmpegPath = "";
            compressionProgramPath = "";

            encodeFormat = "mkv";
            decodeFormat = "jpg";

            encodedVideoWidth = 1280;
            encodedVideoHeight = 720;
            encodedFramerate = 30;
            macroBlockDimensions = 8;
            encodingLibrary = "libvpx";

            ffmpegLogLevel = "info";

            useFullyCustomFfmpegOptions = false;

            fullyCustomFfmpegEncodingOptions = "";
            fullyCustomFfmpegDecodingOptions = "";

            deleteSourceFileWhenEncoding = false;
            deleteSourceFileWhenDecoding = false;

            showSplashScreen = true;
            splashScreenFilePath = "Splash.png";
            splashScreenDisplayTime = 3000;

            compressionCommands = "a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on";

            checkForUpdates = true;

            warnUserIfSettingsMayNotWorkForYouTube = true;

        }

        // Check if all options have been loaded correctly:
        final Logger logger = LogManager.getLogger();

        if(encodedVideoWidth < 1) {
            logger.warn("Encoded Video Width option is less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 1280.");
            encodedVideoWidth = 1280;
        }

        if(encodedVideoHeight < 1) {
            logger.warn("Encoded Video Height option is less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 720.");
            encodedVideoWidth = 720;
        }

        if(encodedFramerate < 1) {
            logger.warn("Encoded Video Framerate option is less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 30.");
            encodedFramerate = 30;
        }

        if(macroBlockDimensions < 1) {
            logger.warn("Encoded Video Macro Block Dimensions is less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 8.");
            macroBlockDimensions = 8;
        }

        if(splashScreenDisplayTime < 1) {
            logger.warn("Splash Screen Display Time is less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 3000.");
            splashScreenDisplayTime = 3000;
        }

        // Calculate Frame Size:
        frameSize = calculateFrameSize();
    }

    /**
     * Creates a new configuration file, or overwrites the existing file,
     * using the default values for each option.
     */
    public void createConfigFile() {
        final File file = new File(FILENAME_CONFIG);

        try {
            file.createNewFile();

            final BufferedWriter outputStream = new BufferedWriter(new FileWriter(file, false));
            outputStream.write("{" + System.lineSeparator());
            outputStream.write("    \"FFMPEG Path\": \"\"," + System.lineSeparator());
            outputStream.write("    \"Compression Program Path\": \"\"," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"Enc Format\": \"mkv\"," + System.lineSeparator());
            outputStream.write("    \"Dec Format\": \"jpg\"," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"Enc Vid Width\": 1280," + System.lineSeparator());
            outputStream.write("    \"Enc Vid Height\": 720," + System.lineSeparator());
            outputStream.write("    \"Enc Vid Framerate\": 30," + System.lineSeparator());
            outputStream.write("    \"Enc Vid Macro Block Dimensions\": 8," + System.lineSeparator());
            outputStream.write("    \"Enc Library\": \"libvpx\"," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"FFMPEG Log Level\": \"info\"," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"Use Custom FFMPEG Options\": false," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"Custom FFMPEG Enc Options\": \"\"," + System.lineSeparator());
            outputStream.write("    \"Custom FFMPEG Dec Options\": \"\"," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"Delete Source File When Enc\": false," + System.lineSeparator());
            outputStream.write("    \"Delete Source File When Dec\": false," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"Show Splash Screen\": true," + System.lineSeparator());
            outputStream.write("    \"Splash Screen File Path\": \"Splash.png\"," + System.lineSeparator());
            outputStream.write("    \"Splash Screen Display Time\": 3000," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"Compression Commands\": \"a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on\"," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"Check For Updates\": true," + System.lineSeparator());
            outputStream.write(System.lineSeparator());
            outputStream.write("    \"Warn If Settings Possibly Incompatible With YouTube\": true" + System.lineSeparator());
            outputStream.write("}");
            outputStream.close();
        } catch(final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            Notification.Notifier.INSTANCE.notifyError("IOException", "Please view the log file.");
        }
    }

    /**
     * Calculates and returns the Frame Size for the current Encoded
     * Video Width & Height.
     *
     * @return
     *         The Frame Size for the current Encoded Video Width &
     *         Height.
     */
    private int calculateFrameSize() {
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
        int frameSize = (encodedVideoWidth * encodedVideoHeight);
        frameSize /= (macroBlockDimensions * macroBlockDimensions); // (8 *8)
        frameSize /= Byte.SIZE;// / 8
        return frameSize;
    }

    /**
     * Sets the new Encoded Video Width, then recalculates the Frame Size.
     *
     * @param encodedVideoWidth
*              The width, in pixels, to use when encoding a video.
     */
    public void setEncodedVideoWidth(final int encodedVideoWidth) {
        if (encodedVideoWidth > 1) {
            this.encodedVideoWidth = encodedVideoWidth;
        } else {
            final Logger logger = LogManager.getLogger();
            logger.warn("Encoded Video Width cannot be set to less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 1280.");

            this.encodedVideoWidth = 1280;
        }

        calculateFrameSize();
    }

    /**
     * Sets the new Encoded Video Height, then recalculates the Frame Size.
     *
     * @param encodedVideoHeight
     *         The height, in pixels, to use when encoding a video.
     */
    public void setEncodedVideoHeight(final int encodedVideoHeight) {
        if(encodedVideoHeight >= 1) {
            this.encodedVideoHeight = encodedVideoHeight;
        } else {
            final Logger logger = LogManager.getLogger();
            logger.warn("Encoded Video Height cannot be set to less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 720.");

            this.encodedVideoHeight = 720;
        }

        calculateFrameSize();
    }

    /**
     * Sets the new Encoded Video Framerate.
     *
     * @param encodedFramerate
     *         The framerate to use when encoding a video.
     */
    public void setEncodedFramerate(final int encodedFramerate) {
        if(encodedFramerate >= 1) {
            this.encodedFramerate = encodedFramerate;
        } else {
            final Logger logger = LogManager.getLogger();
            logger.warn("Encoded Video Framerate cannot be set to less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 30.");

            this.encodedFramerate = 30;
        }
    }

    /**
     * Sets the new Encoded Video Macro Block Dimensions.
     *
     * @param macroBlockDimensions
     *        The new Macro Block width/height to use when encoding a video.
     */
    public void setMacroBlockDimensions(final int macroBlockDimensions) {
        if(macroBlockDimensions >= 1) {
            this.macroBlockDimensions = macroBlockDimensions;
        } else {
            final Logger logger = LogManager.getLogger();
            logger.warn("Encoded Video Framerate cannot be set to less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 8.");

            this.macroBlockDimensions = 8;
        }
    }

    /**
     * Sets the new Splash Screen Display Time.
     *
     * @param splashScreenDisplayTime
     *         The amount of time, in milliseconds, to display the splash screen for.
     */
    public void setSplashScreenDisplayTime(final int splashScreenDisplayTime) {
        if(splashScreenDisplayTime >= 1) {
            this.splashScreenDisplayTime = splashScreenDisplayTime;
        } else {
            final Logger logger = LogManager.getLogger();
            logger.warn("Splash Screen Display Time cannot be set to less than 1. Ensure the value is 1 or greater. " +
                        "Defaulting to 3000.");

            this.splashScreenDisplayTime = 3000;
        }
    }
}
