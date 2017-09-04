package handler;

import configuration.Settings;
import eu.hansolo.enzo.notification.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler {
    /**
     * Pads the specified handler to ensure it contains enough data to
     * have an exact number of frames. If there are, for example,
     * 1401 bytes and the frame size is 1400 bytes, then ffmpeg will
     * display an error about not having a full frame worth of bytes.
     * @param file The handler to pad.
     * @param settings The settings to use when padding the handler.
     */
    public static void padFile(final File file, final Settings settings) {
        try {
            final FileOutputStream outputStream = new FileOutputStream(file, true);
            int numberOfBytesToPad = settings.getIntegerSetting("Frame Size") - ( (int) (file.length() % settings.getIntegerSetting("Frame Size")) );
            outputStream.write(new byte[numberOfBytesToPad]);
            outputStream.close();
        } catch(final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            Notification.Notifier.INSTANCE.notifyError("IOException", "Please view the log file.");
        }
    }
}
