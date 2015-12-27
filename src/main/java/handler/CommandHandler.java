package handler;

import controller.MainScreenController;
import core.Driver;
import core.Log;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandHandler {
    /**
     * Executes the specified command on the commandline.
     * @param command The command to execute.
     * @param controller The controller for the main screen.
     */
    public static void runProgram(final String command, final MainScreenController controller) {
        try {
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            final Process process = builder.start();
            final InputStream is = process.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // Ensure the process shuts down if the program exits:
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    process.destroy();
                }
            }));


            String line;
            while((line = reader.readLine()) != null) {
                final String temp = line;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.getView().getTextArea_output().appendText(temp + System.lineSeparator());
                    }
                });
            }

            is.close();
        } catch(final IOException e) {
            Driver.LOGGER.addLog(Log.LOGTYPE_ERROR, e);
            System.exit(1);
        }
    }
}
