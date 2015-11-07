package handler;


import controller.MainScreenController;
import misc.Job;
import misc.Logger;

import java.io.File;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class ArchiveHandler {
    /**
     * Compresses the specified handler while outputting the command-line
     * results to the screen.
     *
     * The resulting archive will bear the name of the input handler.
     * If the input handler is test.jpg, then the output archive will be test.jpg.7z
     * as an example.
     * @param job The Job being run.
     * @param selectedFile The file to compress.
     * @param controller The controller for the view in which the output text area resides.
     * @param configHandler The object that handles settings for encoding, decoding, compression, and a number of other features.
     * @return The compressed archive.
     */
    public File packFile(final Job job, final File selectedFile, final MainScreenController controller, final ConfigHandler configHandler) {
        // Basic command settings ripped from http://superuser.com/a/742034
        final StringBuilder stringBuilder = new StringBuilder();
        final Formatter formatter = new Formatter(stringBuilder, Locale.US);

        formatter.format("\"%s\" %s \"%s.%s\" \"%s\"",
                        configHandler.getCompressionProgramPath(),
                        configHandler.getCompressionCommands(),
                        selectedFile.getAbsolutePath(),
                        configHandler.getDecodeFormat(),
                        job.getOutputDirectory());

        controller.getView().getTextArea_output().appendText(stringBuilder.toString() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

        CommandHandler.runProgram(stringBuilder.toString(), controller);

        // Return a File poingint to the newly created archive:
        final File file = new File(selectedFile.getAbsoluteFile() + "." + configHandler.getDecodeFormat());


        if(!file.exists()) {
            Logger.writeLog("Could not create " + file.getAbsolutePath() + " shutting down.", Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }
        return file;
    }

    /**
     * Compresses the specified handler(s), while outputting the command-line
     * results to the screen, into a single archive.
     *
     * The resulting archive will bear the specified name.
     * @param job The Job being run.
     * @param selectedFiles The file(s) to compress.
     * @param controller The controller for the view in which the output text area resides.
     * @param configHandler The object that handles settings for encoding, decoding, compression, and a number of other features.
     * @return The compressed archive.
     */
    public File packFiles(final Job job, final List<File> selectedFiles, final MainScreenController controller, final ConfigHandler configHandler) {
        // Basic command settings ripped from http://superuser.com/a/742034
        final StringBuilder stringBuilder = new StringBuilder();
        final Formatter formatter = new Formatter(stringBuilder, Locale.US);

        formatter.format("\"%s\" %s \"%s.%s\"",
                        configHandler.getCompressionProgramPath(),
                        configHandler.getCompressionCommands(),
                        job.getName(),
                        job.getOutputDirectory());

        for(final File f : selectedFiles) {
            // todo If it's possible to do the below to lines with the formatter, then do so.
            stringBuilder.append(" ");
            stringBuilder.append("\"" + f.getAbsolutePath() + "\"");
        }

        controller.getView().getTextArea_output().appendText(stringBuilder.toString() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

        CommandHandler.runProgram(stringBuilder.toString(), controller);

        // Return a File int to the newly created archive:
        final File file = new File(job.getName() + "." + configHandler.getDecodeFormat());

        if(!file.exists()) {
            Logger.writeLog("Could not create " + job.getName() + " shutting down.", Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }

        return file;
    }
}
