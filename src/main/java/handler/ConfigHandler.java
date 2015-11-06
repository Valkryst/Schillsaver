package handler;

import files.FileInput;
import misc.Logger;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ConfigHandler {
    /** The name of the configuration handler. */
    private static final String FILENAME_CONFIG = "config.ini";

    /** The logging levels supported by FFMPEG as of 2015/Nov/1. */
    public static final String[] FFMPEG_LOG_LEVELS = {"quiet", "panic", "fatal", "error", "warning", "info", "verbose", "debug", "trace"};

    /** The absolute path to ffmpeg/ffmpeg.exe. */
    private String ffmpegPath;
    /** The absolute path to 7zip/7zip.exe or whichever compression program is specified. */
    private String compressionProgramPath;

    /** The path of  both the archived and encoded files. */
    private String encodedFilePath;

    /** The format to encode to. */
    private String encodeFormat = "mkv";
    /** The format to decode to. */
    private String decodeFormat = "7z";

    /** The width, in pixels, of the encoded video. */
    private int encodedVideoWidth = 1280;
    /** The height, in pixels, of the encoded video. */
    private int encodedVideoHeight = 720;
    /** The framerate of the video. Ex ~ 30fps, 60fps, etc... */
    private int encodedFramerate = 30;
    /** The size of each frame of video in bytes. */
    private int frameSize;
    /** The width/height of each encoded macroblock. */
    private int macroBlockDimensions = 8;
    /** The codec to encode/decode the video with. */
    private String encodingLibrary = "libvpx";
    /** The level of information that should be given by ffmpeg while ffmpeg is running. */
    private String ffmpegLogLevel = "info";

    /** Whether or not to ignore all other ffmpeg options and to use the fullyCustomFfmpegEncodingOptions and fullyCustomFfmpegDecodingOptions instead. */
    private boolean useFullyCustomFfmpegOptions = false;
    /** The user-entered command line arguments to use when encoding with ffmpeg. */
    private String fullyCustomFfmpegEncodingOptions = "";
    /** The user-entered command line arguments to use when encoding with ffmpeg. */
    private String fullyCustomFfmpegDecodingOptions = "";

    /** Whether or not to delete the source file after encoding. */
    private boolean deleteSourceFileWhenEncoding = false;
    /** whether or not to delete the osource file after decoding. */
    private boolean deleteSourceFileWhenDecoding = false;
    /** Whether or not to pack all of the currently selected files into a single archive before encoding. */
    private boolean combineAllFilesIntoSingleArchive = false;
    /** Whether or not to pack every handler into it's own individual archive before encoding each handler individually. */
    private boolean combineIntoIndividualArchives = false;

    /** Whether or not to show the splash screen on startup. */
    private boolean showSplashScreen = true;
    /** The absolute path to the splash screen to display. */
    private String splashScreenFilePath = "Splash.png";
    /** The amount of time, in milliseconds, to display the splash screen. */
    private int splashScreenDisplayTime = 3000;

    /** The base commands to use when compressing a handler before encoding. */
    private String compressionCommands = "a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on";

    /** @return Whether or not the config handler exists. */
    public boolean doesConfigFileExist() {
        return Files.exists(Paths.get(FILENAME_CONFIG));
    }

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
            }
        } catch(final IOException e) { // Should only happen if the config handler doesn't exist.
            createConfigFile();
            loadConfigSettings();
        } catch(final NumberFormatException e) { // If encodedVideoWidth/Height fail conversion to ints.
            Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }

        // Check if all options have been loaded correctly:
        boolean exitProgram = false;

        if(ffmpegPath == null) {
            Logger.writeLog("Could not load the ffmpegPath option from the config handler. Check if it's spelled correctly.", Logger.LOG_TYPE_ERROR);
            exitProgram = true;
        }

        if(compressionProgramPath == null) {
            Logger.writeLog("Could not load the compressionProgramPath option from the config handler. Check if it's spelled correctly.", Logger.LOG_TYPE_ERROR);
            exitProgram = true;
        }

        if(encodeFormat == null) {
            Logger.writeLog("Could not load the encodeFormat option from the config handler. Check if it's spelled correctly.", Logger.LOG_TYPE_ERROR);
            exitProgram = true;
        }

        if(decodeFormat == null) {
            Logger.writeLog("Could not load the decodeFormat option from the config handler. Check if it's spelled correctly.", Logger.LOG_TYPE_ERROR);
            exitProgram = true;
        }

        if(encodedVideoWidth < 1) {
            Logger.writeLog("Either could not load the encodedVideoWidth option from the config handler or the value is less than 1. Check if it's spelled correctly and ensure the value is 1 or greater.", Logger.LOG_TYPE_ERROR);
            exitProgram = true;
        }

        if(encodedVideoHeight < 1) {
            Logger.writeLog("Either could not load the encodedVideoHeight option from the config handler or the value is less than 1. Check if it's spelled correctly and ensure the value is 1 or greater.", Logger.LOG_TYPE_ERROR);
            exitProgram = true;
        }

        if(encodedFramerate < 1) {
            Logger.writeLog("Could not load the encodedFramerate option from the config handler or the value is less than 1. Check if it's spelled correctly and ensure the value is 1 or greater.", Logger.LOG_TYPE_ERROR);
            exitProgram = true;
        }

        if(macroBlockDimensions < 1) {
            Logger.writeLog("Could not load the macroBlockDimensions option from the config handler or the value is less than 1. Check if it's spelled correctly and ensure the value is 1 or greater.", Logger.LOG_TYPE_ERROR);
            exitProgram = true;
        }

        if(splashScreenDisplayTime < 1) {
            Logger.writeLog("Could not load splashScreenDisplayTime option from the config handler or the value is less than 1. Check if it's spelled correctly and ensure the value is 1 or greater.", Logger.LOG_TYPE_ERROR);
            exitProgram = true;
        }

        if(compressionCommands == null) {
            Logger.writeLog("Could not load the compressionCommands option from the config handler. Check if it's spelled correctly.", Logger.LOG_TYPE_ERROR);
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
            outputStream.close();
        } catch(final IOException e) {
            Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_ERROR);
            System.exit(1);
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

    ////////////////////////////////////////////////////////// Getters

    /** @return The absolute path to ffmpeg/ffmpeg.exe. */
    public String getFfmpegPath() {
        return ffmpegPath;
    }

    /** @return The absolute path to 7zip/7zip.exe. */
    public String getCompressionProgramPath() {
        return compressionProgramPath;
    }

    /** @return The Path of both the archived and encoded files. */
    public String getEncodedFilePath() {
        return encodedFilePath;
    }

    /** @return The format to encode to. */
    public String getEncodeFormat() {
        return encodeFormat;
    }

    /** @return The format to decode to. */
    public String getDecodeFormat() {
        return decodeFormat;
    }

    /** @return The width, in pixels, of the encoded video. */
    public int getEncodedVideoWidth() {
        return encodedVideoWidth;
    }

    /** @return The height, in pixels, of the encoded video. */
    public int getEncodedVideoHeight() {
        return encodedVideoHeight;
    }

    /** @return The framerate of the video. Ex ~ 30fps, 60fps, etc... */
    public int getEncodedFramerate() {
        return encodedFramerate;
    }

    /** @return The size of each frame of video in bytes. */
    public int getFrameSize() {
        return frameSize;
    }

    /** @return The width/height of each encoded macroblock. */
    public int getMacroBlockDimensions() {
        return macroBlockDimensions;
    }

    /** @return The codec to encode/decode the video with. */
    public String getEncodingLibrary() {
        return encodingLibrary;
    }

    /** @return The level of information that should be given by ffmpeg while ffmpeg is running. */
    public String getFfmpegLogLevel() {
        return ffmpegLogLevel;
    }

    /** @return Whether or not to ignore all other ffmpeg options and to use the fullyCustomFfmpegEncodingOptions and fullyCustomFfmpegDecodingOptions instead. */
    public boolean getUseFullyCustomFfmpegOptions() {
        return useFullyCustomFfmpegOptions;
    }

    /** @return The user-entered command line arguments to use when encoding with ffmpeg. */
    public String getFullyCustomFfmpegEncodingOptions() {
        return fullyCustomFfmpegEncodingOptions;
    }

    /** @return The user-entered command line arguments to use when encoding with ffmpeg. */
    public String getFullyCustomFfmpegDecodingOptions() {
        return fullyCustomFfmpegDecodingOptions;
    }

    /** @return Whether or not to delete the source file after encoding. */
    public boolean getdeleteSourceFileWhenEncoding() {
        return deleteSourceFileWhenEncoding;
    }

    /** @return Whether or not to delete the source file after decoding. */
    public boolean getdeleteSourceFileWhenDecoding() {
        return deleteSourceFileWhenDecoding;
    }

    /** @return Whether or not to combine all of the input files into a single archive when encoding. */
    public boolean getCombineAllFilesIntoSingleArchive() {
        return combineAllFilesIntoSingleArchive;
    }

    /** @return Whether or not to pack every handler into it's own individual archive before encoding each handler individually. */
    public boolean getCombineIntoIndividualArchives() {
        return combineIntoIndividualArchives;
    }

    /** @return Whether or not to show the splash screen on startup. */
    public boolean getShowSplashScreen() {
        return showSplashScreen;
    }

    /** @return The path to the splash screen to display. */
    public String getSplashScreenFilePath() {
        return splashScreenFilePath;
    }

    /** @return The amount of time, in milliseconds, to display the splash screen. */
    public int getSplashScreenDisplayTime() {
        return splashScreenDisplayTime;
    }

    /** @return The base commands to use when compressing a handler before encoding. */
    public String getCompressionCommands() {
        return compressionCommands;
    }

    ////////////////////////////////////////////////////////// Setters

    // todo JavaDoc
    public void setFfmpegPath(final String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    // todo JavaDoc
    public void setCompressionProgramPath(final String compressionProgramPath) {
        this.compressionProgramPath = compressionProgramPath;
    }

    // todo JavaDoc
    public void setEncodedFilePath(final String encodedFilePath) {
        this.encodedFilePath = encodedFilePath;
    }

    // todo JavaDoc
    public void setEncodeFormat(final String encodeFormat) {
        this.encodeFormat = encodeFormat;
    }

    // todo JavaDoc
    public void setDecodeFormat(final String decodeFormat) {
        this.decodeFormat = decodeFormat;
    }

    // todo JavaDoc
    public void setEncodedVideoWidth(final int encodedVideoWidth) throws IllegalArgumentException {
        if(encodedVideoWidth > 1) {
            this.encodedVideoWidth = encodedVideoWidth;
        } else {
            throw new IllegalArgumentException("The encodedVideoWidth must be larger than 0.");
        }
    }

    // todo JavaDoc
    public void setEncodedVideoHeight(final int encodedVideoHeight) throws IllegalArgumentException {
        if(encodedVideoHeight > 1) {
            this.encodedVideoHeight = encodedVideoHeight;
        } else {
            throw new IllegalArgumentException("The encodedVideoHeight must be larger than 0.");
        }
    }

    // todo JavaDoc
    public void setEncodedFramerate(final int encodedFramerate) throws IllegalArgumentException {
        if(encodedFramerate > 1) {
            this.encodedFramerate = encodedFramerate;
        } else {
            throw new IllegalArgumentException("The encodedFramerate must be larger than 0.");
        }
    }

    // todo JavaDoc
    public void setMacroBlockDimensions(final int macroBlockDimensions) throws IllegalArgumentException {
        if(macroBlockDimensions > 1) {
            this.macroBlockDimensions = macroBlockDimensions;
        } else {
            throw new IllegalArgumentException("The macroBlockDimensions must be larger than 0.");
        }
    }

    // todo JavaDoc
    public void setEncodingLibrary(final String encodingLibrary) {
        this.encodingLibrary = encodingLibrary;
    }

    // todo JavaDoc
    public void setFfmpegLogLevel(final String ffmpegLogLevel) {
        this.ffmpegLogLevel = ffmpegLogLevel;
    }

    // todo JavaDoc
    public void setUseFullyCustomFfmpegOptions(final boolean useFullyCustomFfmpegOptions) {
        this.useFullyCustomFfmpegOptions = useFullyCustomFfmpegOptions;
    }

    // todo JavaDoc
    public void setFullyCustomFfmpegEncodingOptions(final String fullyCustomFfmpegEncodingOptions) {
        this.fullyCustomFfmpegEncodingOptions = fullyCustomFfmpegEncodingOptions;
    }

    // todo JavaDoc
    public void setFullyCustomFfmpegDecodingOptions(final String fullyCustomFfmpegDecodingOptions) {
        this.fullyCustomFfmpegDecodingOptions = fullyCustomFfmpegDecodingOptions;
    }

    // todo JavaDoc
    public void setdeleteSourceFileWhenEncoding(final boolean deleteSourceFileWhenEncoding) {
        this.deleteSourceFileWhenEncoding = deleteSourceFileWhenEncoding;
    }

    // todo JavaDoc
    public void setdeleteSourceFileWhenDecoding(final boolean deleteSourceFileWhenDecoding) {
        this.deleteSourceFileWhenDecoding = deleteSourceFileWhenDecoding;
    }

    // todo JavaDoc
    public void setCombineAllFilesIntoSingleArchive(final boolean combineAllFilesIntoSingleArchive) {
        this.combineAllFilesIntoSingleArchive = combineAllFilesIntoSingleArchive;
    }

    // todo JavaDoc
    public void setCombineIntoIndividualArchives(final boolean combineIntoIndividualArchives) {
        this.combineIntoIndividualArchives = combineIntoIndividualArchives;
    }

    // todo JavaDoc
    public void setShowSplashScreen(final boolean showSplashScreen) {
        this.showSplashScreen = showSplashScreen;
    }

    // todo JavaDoc
    public void setSplashScreenFilePath(final String splashScreenFilePath) {
        this.splashScreenFilePath = splashScreenFilePath;
    }

    // todo JavaDoc
    public void setSplashScreenDisplayTime(final int splashScreenDisplayTime) throws IllegalArgumentException {
        if(splashScreenDisplayTime > 1) {
            this.splashScreenDisplayTime = splashScreenDisplayTime;
        } else {
            throw new IllegalArgumentException("The splashScreenDisplayTime must be larger than 0.");
        }
    }

    // todo JavaDoc
    public void setCompressionCommands(final String compressionCommands) {
        this.compressionCommands = compressionCommands;
    }
}
