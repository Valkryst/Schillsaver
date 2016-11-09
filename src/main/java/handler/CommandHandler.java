package handler;

import controller.MainScreenController;
import eu.hansolo.enzo.notification.Notification;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandHandler {
    /**
     * Executes the specified command on the commandline.
     *
     * @param command
     *         The command to execute.
     *
     * @param controller
     *         The controller for the main screen.
     *
     * @return
     *         True if the job completed, else false.
     */
    public static boolean runProgram(final String command, final MainScreenController controller) {
        try {
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            final Process process = builder.start();
            final InputStream is = process.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // Ensure the process shuts down if the program exits:
            Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));


            String line;
            while((line = reader.readLine()) != null) {
                final String temp = line;

                Platform.runLater(() -> controller.getView()
                                                  .getTextArea_output()
                                                  .appendText(temp + System.lineSeparator()));
            }

            is.close();

            return true;
        } catch(final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            Notification.Notifier.INSTANCE.notifyError("IOException", "Please view the log file.");
            return false;
        }
    }
}
