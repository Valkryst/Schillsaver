package Schillsaver.job;

import Schillsaver.setting.BlockSize;
import Schillsaver.setting.FrameDimension;
import Schillsaver.setting.Settings;
import lombok.NonNull;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipArchiver implements Serializable {
    private static final long serialVersionUID = 0;

    /** The archive file extension. */
    private final static String EXTENSION = ".zip";

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
			e.printStackTrace();
		}

		return file;
	}

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
    public File archive(final @NonNull String fileName, final List<File> files) throws IOException {
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("The file name cannot be empty.");
        }

        if (files.size() == 0) {
            throw new IllegalArgumentException("There must be at least one file to archive.");
        }

        final File zipFile = new File(fileName + EXTENSION);

        final var fos = new FileOutputStream(zipFile);
        final var zos = new ZipOutputStream(fos);

        byte[] buffer = new byte[32_768];

        for (final File file : files) {
            if (! file.isDirectory()) {
                final var entry = new ZipEntry(file.getName());
                final var fis = new FileInputStream(file);

                zos.putNextEntry(entry);

                int read;
                while ((read = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, read);
                }

                zos.closeEntry();
                fis.close();
            }
        }

        zos.flush();
        fos.flush();

        zos.close();
        fos.close();

        return this.padFile(zipFile);
    }
}
