package handler;

import misc.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
     * @param configHandler The settings to use when padding the handler.
     */
    public static void padFile(final File file, final ConfigHandler configHandler) {
        try {
            final FileOutputStream outputStream = new FileOutputStream(file, true);
            int numberOfBytesToPad = configHandler.getFrameSize() - ( (int) (file.length() % configHandler.getFrameSize()) );
            outputStream.write(new byte[numberOfBytesToPad]);
            outputStream.close();
        } catch(final IOException e) {
            Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }
    }
}
