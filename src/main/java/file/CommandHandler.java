package file;

import gui.MainScreenController;
import misc.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
                controller.getView().getTextArea_ffmpegOutput().append(line + System.lineSeparator());
            }

            is.close();
        } catch(final IOException e) {
            Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }
    }

    /**
     * Executes the specified command on the commandline and
     * returns the results.
     * @param command The command to execute.
     * @return The results of the command run.
     */
    public static List<String> runCommandWithResults(final String command) {
        final List<String> results = new ArrayList<>();

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
                results.add(line);
            }

            is.close();
        } catch(final IOException e) {
            Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }

        return results;
    }
}
