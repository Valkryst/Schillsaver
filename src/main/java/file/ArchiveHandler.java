package file;

import gui.MainScreenController;
import misc.Logger;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

public class ArchiveHandler {
    public File packFile(final File selectedFile, final MainScreenController controller,  final ConfigHandler configHandler) {
        // Basic command settings ripped from http://superuser.com/a/742034
        final StringBuilder stringBuilder = new StringBuilder();
        final Formatter formatter = new Formatter(stringBuilder, Locale.US);

        formatter.format("\"%s\" %s \"%s.%s\" \"%s\"",
                        configHandler.getCompressionProgramPath(),
                        configHandler.getCompressionCommands(),
                        selectedFile.getAbsolutePath(),
                        configHandler.getDecodeFormat(),
                        selectedFile.getAbsolutePath());

        controller.getView().getTextArea_ffmpegOutput().append(stringBuilder.toString() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

        CommandHandler.runProgram(stringBuilder.toString(), controller);

        // Return a File poingint to the newly created archive:
        final File file = new File(selectedFile.getAbsoluteFile() + "." + configHandler.getDecodeFormat());


        if(!file.exists()) {
            Logger.writeLog("Could not create " + file.getAbsolutePath() + " shutting down.", Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }
        return file;
    }

    public File packFiles(final File[] selectedFiles, final MainScreenController controller,  final ConfigHandler configHandler, final String archiveName) {
        // Basic command settings ripped from http://superuser.com/a/742034
        final StringBuilder stringBuilder = new StringBuilder();
        final Formatter formatter = new Formatter(stringBuilder, Locale.US);

        formatter.format("\"%s\" %s \"%s.%s\"",
                        configHandler.getCompressionProgramPath(),
                        configHandler.getCompressionCommands(),
                        archiveName,
                        configHandler.getDecodeFormat());

        for(final File f : selectedFiles) {
            // todo If it's possible to do the below to lines with the formatter, then do so.
            stringBuilder.append(" ");
            stringBuilder.append("\"" + f.getAbsolutePath() + "\"");
        }

        controller.getView().getTextArea_ffmpegOutput().append(stringBuilder.toString() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

        CommandHandler.runProgram(stringBuilder.toString(), controller);

        // Return a File int to the newly created archive:
        final File file = new File(archiveName + "." + configHandler.getDecodeFormat());

        if(!file.exists()) {
            Logger.writeLog("Could not create " + archiveName + " shutting down.", Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }

        return file;
    }
}
