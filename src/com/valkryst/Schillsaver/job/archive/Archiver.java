package com.valkryst.Schillsaver.job.archive;

import com.valkryst.Schillsaver.log.LogLevel;
import com.valkryst.Schillsaver.setting.BlockSize;
import com.valkryst.Schillsaver.setting.FrameDimension;
import com.valkryst.Schillsaver.setting.Settings;
import lombok.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public abstract class Archiver {
    /**
     * Packs one or more files into an archive.
     *
     * @param fileName
     *          The archive file name.
     *
     * @param files
     *          The files.
     *
     * @return
     *          The archive file.
     *
     * @throws IllegalArgumentException
     *          If an I/O error occurs.
     */
    public abstract File archive(final @NonNull String fileName, final List<File> files) throws IOException;

    /**
     * Pads the specified file to ensure it contains enough data to have an exact number of frames.
     *
     * If there are, for example, 1401 bytes and the frame size is 1400 bytes, then FFMPEG will display an
     * error about not having a full frame worth of bytes.
     *
     * @param file
     *          The file.
     *
     * @throws NullPointerException
     *          If the file or settings is null.
     */
    protected File padFile(final @NonNull File file) {
        final Settings settings = Settings.getInstance();

        try (final FileOutputStream outputStream = new FileOutputStream(file, true)) {
            final FrameDimension frameDimension = FrameDimension.valueOf(settings.getStringSetting("Encoding Frame Dimensions"));
            final int blockSize = BlockSize.valueOf(settings.getStringSetting("Encoding Block Size")).getBlockSize();

            int bytesPerFrame = frameDimension.getWidth() * frameDimension.getHeight();
            bytesPerFrame /= blockSize * blockSize;
            bytesPerFrame /= 8;

            final int numberOfBytesToPad = bytesPerFrame - (int) (file.length() % bytesPerFrame);

            outputStream.write(new byte[numberOfBytesToPad]);
        } catch (final IOException e) {
            Settings.getInstance().getLogger().log(e, LogLevel.ERROR);
        }

        return file;
    }

    /**
     * Retrieves the file name, appended with the archive extension.
     *
     * @return
     *          The file name, appended with the archive extension.
     */
    public abstract String getFileName(final String fileName);
}
