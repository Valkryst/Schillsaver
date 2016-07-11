package handler;

import core.Driver;
import core.Log;
import eu.hansolo.enzo.notification.Notification;
import files.FileInput;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

public class ConfigHandler {
    /** The name of the configuration handler. */
    private static final String FILENAME_CONFIG = "config.ini";

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
    @Getter private int encodedVideoWidth = 1280;
    /** The height, in pixels, of the encoded video. */
    @Getter private int encodedVideoHeight = 720;
    /** The framerate of the video. Ex ~ 30fps, 60fps, etc... */
    @Getter private int encodedFramerate = 30;
    /** The size of each frame of video in bytes. */
    @Getter private int frameSize;
    /** The width/height of each encoded macroblock. */
    @Getter private int macroBlockDimensions = 8;
    /** The codec to encode/decode the video with. */
    @Getter @Setter private String encodingLibrary = "libvpx";
    /** The level of information that should be given by ffmpeg while ffmpeg is running. */
    @Getter @Setter private String ffmpegLogLevel = "info";

    /** Whether or not to ignore all other ffmpeg options and to use the fullyCustomFfmpegEncodingOptions and fullyCustomFfmpegDecodingOptions instead. */
    @Getter @Setter private boolean useFullyCustomFfmpegOptions = false;
    /** The user-entered command line arguments to use when encoding with ffmpeg. */
    @Getter @Setter private String fullyCustomFfmpegEncodingOptions = "";
    /** The user-entered command line arguments to use when encoding with ffmpeg. */
    @Getter @Setter private String fullyCustomFfmpegDecodingOptions = "";

    /** Whether or not to delete the source file after encoding. */
    @Getter @Setter private boolean deleteSourceFileWhenEncoding = false;
    /** whether or not to delete the osource file after decoding. */
    @Getter @Setter private boolean deleteSourceFileWhenDecoding = false;

    /** Whether or not to show the splash screen on startup. */
    @Getter @Setter private boolean showSplashScreen = true;
    /** The absolute path to the splash screen to display. */
    @Getter @Setter private String splashScreenFilePath = "Splash.png";
    /** The amount of time, in milliseconds, to display the splash screen. */
    @Getter private int splashScreenDisplayTime = 3000;

    /** The base commands to use when compressing a handler before encoding. */
    @Getter @Setter private String compressionCommands = "a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on";

    /** Whether or not to check for program updates on program start. */
    @Getter @Setter private boolean checkForUpdatesOnStart = false;

    /** Whether or not to warn the user if their settings may not work with YouTube. */
    @Getter @Setter private boolean warnUserIfSettingsMayNotWorkForYouTube = true;

    /**
     * Reads in each line from the configuration handler and attempts to parse
     * the specified parameters of the program.
     *
     * If a line cannot be parsed, then a warning is logged.
     */
    public void loadConfigSettings() {
        calculateFrameSize();

        try {
            List<String> lines = FileInput.readEntireFile(Paths.get(FILENAME_CONFIG), true);


            for(String currentLine : lines) {
                if(currentLine.contains("ffmpegPath ")) {
                    ffmpegPath = currentLine.replace("ffmpegPath ", "");
                }

                if(currentLine.contains("compressionProgramPath ")) {
                    compressionProgramPath = currentLine.replace("compressionProgramPath ", "");
                }

                if(currentLine.contains("encodeFormat ")) {
                    encodeFormat = currentLine.replace("encodeFormat ", "");
                }

                if(currentLine.contains("decodeFormat ")) {
                    decodeFormat = currentLine.replace("decodeFormat ", "");
                }

                if(currentLine.contains("encodedVideoWidth ")) {
                    encodedVideoWidth = Integer.valueOf(currentLine.replace("encodedVideoWidth ", ""));
                }

                if(currentLine.contains("encodedVideoHeight ")) {
                    encodedVideoHeight = Integer.valueOf(currentLine.replace("encodedVideoHeight ", ""));
                }

                if(currentLine.contains("encodedFramerate ")) {
                    encodedFramerate = Integer.valueOf(currentLine.replace("encodedFramerate ", ""));
                }

                if(currentLine.contains("macroBlockDimensions ")) {
                    macroBlockDimensions = Integer.valueOf(currentLine.replace("macroBlockDimensions ", ""));
                }

                if(currentLine.contains("encodingLibrary ")) {
                    encodingLibrary = currentLine.replace("encodingLibrary ", "");
                }

                if(currentLine.contains("ffmpegLogLevel ")) {
                    ffmpegLogLevel = currentLine.replace("ffmpegLogLevel ", "");
                }

                if(currentLine.contains("useFullyCustomFfmpegOptions ")) {
                    useFullyCustomFfmpegOptions = Boolean.valueOf(currentLine.replace("useFullyCustomFfmpegOptions ", ""));
                }

                if(currentLine.contains("fullyCustomFfmpegEncodingOptions ")) {
                    fullyCustomFfmpegEncodingOptions = currentLine.replace("fullyCustomFfmpegEncodingOptions ", "");
                }

                if(currentLine.contains("fullyCustomFfmpegDecodingOptions ")) {
                    fullyCustomFfmpegDecodingOptions = currentLine.replace("fullyCustomFfmpegDecodingOptions ", "");
                }

                if(currentLine.contains("deleteSourceFileWhenEncoding ")) {
                    deleteSourceFileWhenEncoding = Boolean.valueOf(currentLine.replace("deleteSourceFileWhenEncoding ", ""));
                }

                if(currentLine.contains("deleteSourceFileWhenDecoding ")) {
                    deleteSourceFileWhenDecoding = Boolean.valueOf(currentLine.replace("deleteSourceFileWhenDecoding ", ""));
                }

                if(currentLine.contains("showSplashScreen ")) {
                    showSplashScreen = Boolean.valueOf(currentLine.replace("showSplashScreen ", ""));
                }

                if(currentLine.contains("splashScreenFilePath ")) {
                    splashScreenFilePath = currentLine.replace("splashScreenFilePath ", "");
                }

                if(currentLine.contains("splashScreenDisplayTime ")) {
                    splashScreenDisplayTime = Integer.valueOf(currentLine.replace("splashScreenDisplayTime ", ""));
                }

                if(currentLine.contains("compressionCommands ")) {
                    compressionCommands = currentLine.replace("compressionCommands ", "");
                }

                if(currentLine.contains("checkForUpdatesOnStart ")) {
                    checkForUpdatesOnStart = Boolean.valueOf(currentLine.replace("checkForUpdatesOnStart ", ""));
                }

                if(currentLine.contains("warnUserIfSettingsMayNotWorkForYouTube ")) {
                    warnUserIfSettingsMayNotWorkForYouTube = Boolean.valueOf(currentLine.replace("warnUserIfSettingsMayNotWorkForYouTube ", ""));
                }
            }
        } catch(final IOException e) { // Should only happen if the config handler doesn't exist.
            createConfigFile();
            loadConfigSettings();
        } catch(final NumberFormatException e) { // If encodedVideoWidth/Height fail conversion to ints.
            final Logger logger = LogManager.getLogger();
            logger.error(e);
        }

        // Check if all options have been loaded correctly:
        final Logger logger = LogManager.getLogger();
        boolean exitProgram = false;

        if(ffmpegPath == null) {
            logger.error("Could not load the ffmpegPath option from the config handler. Check if it's spelled " +
                         "correctly.");
            exitProgram = true;
        }

        if(compressionProgramPath == null) {
            logger.error("Could not load the compressionProgramPath option from the config handler. Check if it's " +
                         "spelled correctly.");
            exitProgram = true;
        }

        if(encodeFormat == null) {
            logger.error("Could not load the encodeFormat option from the config handler. Check if it's spelled " +
                         "correctly.");
            exitProgram = true;
        }

        if(decodeFormat == null) {
            logger.error("Could not load the decodeFormat option from the config handler. Check if it's spelled " +
                         "correctly.");
            exitProgram = true;
        }

        if(encodedVideoWidth < 1) {
            logger.error("Either could not load the encodedVideoWidth option from the config handler or the value is " +
                         "less than 1. Check if it's spelled correctly and ensure the value is 1 or greater.");
            exitProgram = true;
        }

        if(encodedVideoHeight < 1) {
            logger.error("Either could not load the encodedVideoHeight option from the config handler or the value is " +
                         "less than 1. Check if it's spelled correctly and ensure the value is 1 or greater.");
            exitProgram = true;
        }

        if(encodedFramerate < 1) {
            logger.error("Could not load the encodedFramerate option from the config handler or the value is less than " +
                         "1. Check if it's spelled correctly and ensure the value is 1 or greater.");
            exitProgram = true;
        }

        if(macroBlockDimensions < 1) {
            logger.error("Could not load the macroBlockDimensions option from the config handler or the value is less " +
                         "than 1. Check if it's spelled correctly and ensure the value is 1 or greater.");
            exitProgram = true;
        }

        if(splashScreenDisplayTime < 1) {
            logger.error("Could not load splashScreenDisplayTime option from the config handler or the value is less " +
                         "than 1. Check if it's spelled correctly and ensure the value is 1 or greater.");
            exitProgram = true;
        }

        if(compressionCommands == null) {
            logger.error("Could not load the compressionCommands option from the config handler. Check if it's spelled " +
                         "correctly.");
            exitProgram = true;
        }

        if(exitProgram) {
            System.exit(1);
        }

        // If the program hasn't exited, then finish up some final work:
        frameSize = calculateFrameSize();
    }

    /**
     * Creates a new config handler, or overwrites the existing one.
     * The config handler is populated with the values as specified by
     * the class.
     *
     * todo Explain the purpose/function of this class more clearly.
     */
    public void createConfigFile() {
        File file = new File(FILENAME_CONFIG);

        try {
            file.createNewFile();

            final BufferedWriter outputStream = new BufferedWriter(new FileWriter(file, false));
            outputStream.write("ffmpegPath " + ffmpegPath + System.lineSeparator());
            outputStream.write("compressionProgramPath " + compressionProgramPath + System.lineSeparator());
            outputStream.write("encodeFormat " + encodeFormat + System.lineSeparator());
            outputStream.write("decodeFormat " + decodeFormat + System.lineSeparator());
            outputStream.write("encodedVideoWidth " + encodedVideoWidth + System.lineSeparator());
            outputStream.write("encodedVideoHeight " + encodedVideoHeight + System.lineSeparator());
            outputStream.write("encodedFramerate " + encodedFramerate + System.lineSeparator());
            outputStream.write("macroBlockDimensions " + macroBlockDimensions + System.lineSeparator());
            outputStream.write("encodingLibrary " + encodingLibrary + System.lineSeparator());
            outputStream.write("ffmpegLogLevel " + ffmpegLogLevel + System.lineSeparator());
            outputStream.write("useFullyCustomFfmpegOptions " + useFullyCustomFfmpegOptions + System.lineSeparator());
            outputStream.write("fullyCustomFfmpegEncodingOptions " + fullyCustomFfmpegEncodingOptions + System.lineSeparator());
            outputStream.write("fullyCustomFfmpegDecodingOptions " + fullyCustomFfmpegDecodingOptions + System.lineSeparator());
            outputStream.write("deleteSourceFileWhenEncoding " + deleteSourceFileWhenEncoding + System.lineSeparator());
            outputStream.write("deleteSourceFileWhenDecoding " + deleteSourceFileWhenDecoding + System.lineSeparator());
            outputStream.write("showSplashScreen " + showSplashScreen + System.lineSeparator());
            outputStream.write("splashScreenFilePath " + splashScreenFilePath + System.lineSeparator());
            outputStream.write("splashScreenDisplayTime " + splashScreenDisplayTime + System.lineSeparator());
            outputStream.write("compressionCommands " + compressionCommands + System.lineSeparator());
            outputStream.write("checkForUpdatesOnStart " + checkForUpdatesOnStart + System.lineSeparator());
            outputStream.write("warnUserIfSettingsMayNotWorkForYouTube " + warnUserIfSettingsMayNotWorkForYouTube + System.lineSeparator());
            outputStream.close();
        } catch(final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            Notification.Notifier.INSTANCE.notifyError("IOException", "Please view the log file.");
        }

        calculateFrameSize();
    }

    /**
     * Calculates and returns the frameSize for the current
     * encodedVideoWidth/Height.
     * @return The frameSize for the current encodedVideoWidth/Height.
     */
    public int calculateFrameSize() {
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
     * Attempts to locate 7-Zip at it's default install location.
     * If the program is found, then the path to it is set.
     */
    public void searchForDefaultProgramPaths() {
        final File[] driveRoots = File.listRoots();

        if(SystemUtils.IS_OS_WINDOWS) {
            // Search for 7-Zip:
            for(final File f : driveRoots) {
                if(Files.exists(Paths.get(f.toPath() + "Program Files/7-Zip/7z.exe"))) {
                    compressionProgramPath = f.toPath() + "Program Files/7-Zip/7z.exe";
                    break;
                } else if(Files.exists(Paths.get(f.toPath() + "Program Files (x86)/7-Zip/7z.exe"))) {
                    compressionProgramPath = f.toURI() + "Program Files (x86)/7-Zip/7z.exe";
                    break;
                }
            }
        } else if(SystemUtils.IS_OS_LINUX) {
            // todo Figure out how to get this to actually work.
            /*
            // Search for 7-Zip:
            final String path = CommandHandler.runCommandWithResults("sh -c command -v 7za").get(0);

            if(Files.exists(Paths.get(path))) {
                compressionProgramPath = path;
            }
            */
        }
    }

    /**
     * @param encodedVideoWidth The new width, in pixels, of the encoded video.
     * @throws IllegalArgumentException Thrown if the input width is less than 1.
     */
    public void setEncodedVideoWidth(final int encodedVideoWidth) throws IllegalArgumentException {
        if(encodedVideoWidth > 1) {
            this.encodedVideoWidth = encodedVideoWidth;
        } else {
            throw new IllegalArgumentException("The encodedVideoWidth must be larger than 0.");
        }
    }

    /**
     * @param encodedVideoHeight The new height, in pixels, of the encoded video.
     * @throws IllegalArgumentException Thrown if the input height is less than 1.
     */
    public void setEncodedVideoHeight(final int encodedVideoHeight) throws IllegalArgumentException {
        if(encodedVideoHeight > 1) {
            this.encodedVideoHeight = encodedVideoHeight;
        } else {
            throw new IllegalArgumentException("The encodedVideoHeight must be larger than 0.");
        }
    }

    /**
     * @param encodedFramerate The new framerate of the video. Ex ~ 30fps, 60fps, etc...
     * @throws IllegalArgumentException Thrown if the input framerate is less than 1.
     */
    public void setEncodedFramerate(final int encodedFramerate) throws IllegalArgumentException {
        if(encodedFramerate > 1) {
            this.encodedFramerate = encodedFramerate;
        } else {
            throw new IllegalArgumentException("The encodedFramerate must be larger than 0.");
        }
    }

    /**
     * @param macroBlockDimensions The new width/height of each encoded macroblock.
     * @throws IllegalArgumentException Thrown if the input dimensions are less than 1.
     */
    public void setMacroBlockDimensions(final int macroBlockDimensions) throws IllegalArgumentException {
        if(macroBlockDimensions > 1) {
            this.macroBlockDimensions = macroBlockDimensions;
        } else {
            throw new IllegalArgumentException("The macroBlockDimensions must be larger than 0.");
        }
    }

    /** @param splashScreenDisplayTime The new amount of time, in milliseconds, to display the splash screen. */
    public void setSplashScreenDisplayTime(final int splashScreenDisplayTime) throws IllegalArgumentException {
        if(splashScreenDisplayTime > 1) {
            this.splashScreenDisplayTime = splashScreenDisplayTime;
        } else {
            throw new IllegalArgumentException("The splashScreenDisplayTime must be larger than 0.");
        }
    }
}
