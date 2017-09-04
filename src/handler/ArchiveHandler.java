package handler;


import configuration.Settings;
import controller.MainScreenController;
import eu.hansolo.enzo.notification.Notification;
import javafx.application.Platform;
import misc.Job;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class ArchiveHandler {
    /**
     * Compresses the specified handler(s), while outputting the command-line
     * results to the screen, into a single archive.
     *
     * The resulting archive will bear the specified name.
     * @param job The Job being run.
     * @param selectedFiles The file(s) to compress.
     * @param controller The controller for the view in which the output text area resides.
     * @param settings The object that handles settings for encoding, decoding, compression, and a number of other features.
     * @return The compressed archive.
     */
    public File packFiles(final Job job, final List<File> selectedFiles, final MainScreenController controller, final Settings settings) {
        // Basic command settings ripped from http://superuser.com/a/742034
        final StringBuilder stringBuilder = new StringBuilder();
        final Formatter formatter = new Formatter(stringBuilder, Locale.US);

        formatter.format("\"%s\" %s \"%s%s.%s\"",
                        settings.getStringSetting("Compression Program Path"),
                        settings.getStringSetting("Compression Commands"),
                        job.getOutputDirectory(),
                        job.getName(),
                        settings.getStringSetting("Compression Output Extension"));

        selectedFiles.parallelStream()
                     .forEach(file -> {
                         // todo If it's possible to do the below to lines with the formatter, then do so.
                         stringBuilder.append(" ");
                         stringBuilder.append("\"" + file.getAbsolutePath() + "\"");
                     });

        Platform.runLater(() -> controller.getView()
                                          .getTextArea_output()
                                          .appendText(stringBuilder.toString() + System.lineSeparator() +
                                                      System.lineSeparator() + System.lineSeparator()));

        CommandHandler.runProgram(stringBuilder.toString(), controller);

        // Return a File int to the newly created archive:
        final File file = new File(job.getOutputDirectory() + job.getName() + "." + settings.getStringSetting("Compression Output Extension"));

        if (! file.exists()) {
            final String error = "The file " + file.toString() + " does not exist. The most-likely causes are incorrect " +
                                 "commandline arguments or invalid characters in the file name.";

            final Logger logger = LogManager.getLogger();
            logger.error(error);

            Notification.Notifier.INSTANCE.notifyError("Error", error);
        }

        return file;
    }
}
