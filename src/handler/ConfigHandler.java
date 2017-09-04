package handler;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

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

    /** The base commands to use when compressing a handler before encoding. */
    @Getter @Setter private String compressionCommands;
    /** The extension to use when outputting an archive. */
    @Getter @Setter private String compressionOutputExtension;

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
            final InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(streamReader);
        ) {
            final List<String> inputLines = bufferedReader.lines().collect(Collectors.toList());
            final String jsonData = String.join("\n", inputLines);

            final JSONParser jsonParser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);


            ffmpegPath = (String) jsonObject.get("FFMPEG Path");
            compressionProgramPath = (String) jsonObject.get("Compression Program Path");

            encodeFormat = (String) jsonObject.get("Enc Format");
            decodeFormat = (String) jsonObject.get("Dec Format");

            encodedVideoWidth = (int) (long) jsonObject.get("Enc Vid Width");
            encodedVideoHeight = (int) (long) jsonObject.get("Enc Vid Height");
            encodedFramerate = (int) (long) jsonObject.get("Enc Vid Framerate");
            macroBlockDimensions = (int) (long) jsonObject.get("Enc Vid Macro Block Dimensions");
            encodingLibrary = (String) jsonObject.get("Enc Library");

            ffmpegLogLevel = (String) jsonObject.get("FFMPEG Log Level");

            useFullyCustomFfmpegOptions = (Boolean) jsonObject.get("Use Custom FFMPEG Options");
            fullyCustomFfmpegEncodingOptions = (String) jsonObject.get("Custom FFMPEG Enc Options");
            fullyCustomFfmpegDecodingOptions = (String) jsonObject.get("Custom FFMPEG Dec Options");

            compressionCommands = (String) jsonObject.get("Compression Commands");
            compressionOutputExtension = (String) jsonObject.get("Compression Output Extension");

            warnUserIfSettingsMayNotWorkForYouTube = (Boolean) jsonObject.get("Warn If Settings Possibly Incompatible With YouTube");
        } catch(final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            createNewConfigFile();
            loadConfigSettings();
        } catch(final ClassCastException | NullPointerException | ParseException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);
            e.printStackTrace();

            setDefaultSettings();
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

        // Calculate Frame Size:
        frameSize = calculateFrameSize();
    }

    /**
     * Creates a new configuration file, or overwrites the existing file,
     * using the existing values for each option.
     */
    public void createConfigFile() {
        final JSONObject configFile = new JSONObject();
        configFile.put("FFMPEG Path", ffmpegPath);
        configFile.put("Compression Program Path", compressionProgramPath);

        configFile.put("Enc Format", encodeFormat);
        configFile.put("Dec Format", decodeFormat);

        configFile.put("Enc Vid Width", encodedVideoWidth);
        configFile.put("Enc Vid Height", encodedVideoHeight);
        configFile.put("Enc Vid Framerate", encodedFramerate);
        configFile.put("Enc Vid Macro Block Dimensions", macroBlockDimensions);
        configFile.put("Enc Library", encodingLibrary);

        configFile.put("FFMPEG Log Level", ffmpegLogLevel);

        configFile.put("Use Custom FFMPEG Options", useFullyCustomFfmpegOptions);
        configFile.put("Custom FFMPEG Enc Options", fullyCustomFfmpegEncodingOptions);
        configFile.put("Custom FFMPEG Dec Options", fullyCustomFfmpegDecodingOptions);

        configFile.put("Compression Commands", compressionCommands);
        configFile.put("Compression Output Extension", compressionOutputExtension);

        configFile.put("Warn If Settings Possibly Incompatible With YouTube", warnUserIfSettingsMayNotWorkForYouTube);


        try (
                final FileWriter fileWriter = new FileWriter(FILENAME_CONFIG);
        ) {
            fileWriter.write(configFile.toJSONString());
            fileWriter.flush();
        } catch(final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            setDefaultSettings();
        }
    }

    /**
     * Creates a new configuration file, or overwrites the existing file,
     * using the default values for each option.
     */
    public void createNewConfigFile() {
        final JSONObject configFile = new JSONObject();
        configFile.put("FFMPEG Path", "");
        configFile.put("Compression Program Path", "");

        configFile.put("Enc Format", "mkv");
        configFile.put("Dec Format", "jpg");

        configFile.put("Enc Vid Width", 1280);
        configFile.put("Enc Vid Height", 720);
        configFile.put("Enc Vid Framerate", 30);
        configFile.put("Enc Vid Macro Block Dimensions", 8);
        configFile.put("Enc Library", "libvpx");

        configFile.put("FFMPEG Log Level", "info");

        configFile.put("Use Custom FFMPEG Options", false);
        configFile.put("Custom FFMPEG Enc Options", "");
        configFile.put("Custom FFMPEG Dec Options", "");

        configFile.put("Delete Source File When Enc", false);
        configFile.put("Delete Source File When Dec", false);

        configFile.put("Compression Commands", "a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on");
        configFile.put("Compression Output Extension", "7z");

        configFile.put("Check For Updates", true);

        configFile.put("Warn If Settings Possibly Incompatible With YouTube", true);


        try (
            final FileWriter fileWriter = new FileWriter(FILENAME_CONFIG);
        ) {
            fileWriter.write(configFile.toJSONString());
            fileWriter.flush();
        } catch(final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            setDefaultSettings();
        }
    }

    /**
     * Sets all program settings to their default state.
     *
     * This should only be called if the configuration file cannot
     * be created or properly parsed.
     */
    private void setDefaultSettings() {
        ffmpegPath = "";
        compressionProgramPath = "";

        encodeFormat = "mkv";
        decodeFormat = "7z";

        encodedVideoWidth = 1280;
        encodedVideoHeight = 720;
        encodedFramerate = 30;
        macroBlockDimensions = 8;
        encodingLibrary = "libvpx";

        ffmpegLogLevel = "info";

        useFullyCustomFfmpegOptions = false;

        fullyCustomFfmpegEncodingOptions = "";
        fullyCustomFfmpegDecodingOptions = "";

        compressionCommands = "a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on";
        compressionOutputExtension = "7z";

        warnUserIfSettingsMayNotWorkForYouTube = true;
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
}
