package Schillsaver.archiver;

import Schillsaver.display.model.SettingsTabModel;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archiver extends Thread {

    /** Consumer to call when the archive has been created. */
    @Setter private Consumer<Path> onCompletion = (final Path path) -> {};

    /** Consumer to call if an error occurs. */
    @Setter private Consumer<Exception> onError = (final Exception e) -> {};

    /** Consumer to call when the progress needs to be updated. */
    @Setter private Consumer<String> updateProgress = (final String progress) -> {};

    /** List of paths to archive. */
    private final List<Path> paths;

    /**
     * Constructs a new {@code Archiver}.
     *
     * @param paths List of paths to archive.
     */
    public Archiver(final @NonNull List<Path> paths) {
        if (paths.isEmpty()) {
            throw new IllegalArgumentException("Paths list is empty.");
        }

        this.paths = paths;
    }

    @Override
    public void run() {
        try {
            final var archivePath = createArchive();
            onCompletion.accept(archivePath);
        } catch (final IOException e) {
            updateProgress.accept("An error occurred while creating the archive:\n");
            updateProgress.accept("\t" + e.getMessage() + "\n");
            onError.accept(e);
        }
    }

    /**
     * Attempts to create an archive containing the specified paths.
     *
     * @return Path to the archive file.
     *
     * @throws NullPointerException If {@code paths} is null.
     * @throws IllegalArgumentException If {@code paths} is empty.
     * @throws IOException If an I/O exception occurs.
     */
    protected Path createArchive() throws IOException {
        final var archivePath = Files.createTempFile(UUID.randomUUID().toString(), ".zip");
        updateProgress.accept("Creating Archive\n");
        updateProgress.accept("\tTemporary File Path: " + archivePath.toAbsolutePath() + "\n");
        updateProgress.accept("\tTotal Paths to Add: " + paths.size() + "\n\n");

        try (
            final var fileOutputStream = Files.newOutputStream(archivePath, StandardOpenOption.CREATE);
            final var zipOutputStream = new ZipOutputStream(fileOutputStream);
        ) {
            final var baseDir = paths.getFirst().getParent();
            final var buffer = new byte[128 * 1024];
            for (final Path path : paths) {
                if (this.isInterrupted()) {
                    break;
                }

                final var entry = new ZipEntry(baseDir.relativize(path) + (Files.isDirectory(path) ? "/" : ""));
                zipOutputStream.putNextEntry(entry);

                if (Files.isDirectory(path)) {
                    updateProgress.accept("\tAdding Directory: " + path.toAbsolutePath() + "\n");
                    zipOutputStream.closeEntry();
                    continue;
                }

                updateProgress.accept("\tAdding File: " + path.toAbsolutePath() + "\n");

                try (final var fileInputStream = Files.newInputStream(path)) {
                    int read;
                    while (!this.isInterrupted() && ((read = fileInputStream.read(buffer)) != -1)) {
                        zipOutputStream.write(buffer, 0, read);
                    }
                }
                zipOutputStream.closeEntry();
            }
        }

        if (this.isInterrupted()) {
            Files.deleteIfExists(archivePath);
            onError.accept(new IOException("Archiving process was interrupted. Deleted temporary file."));
            return null;
        }

        updateProgress.accept("\n\tPadding Archive: " + archivePath.toAbsolutePath() + "\n");
        this.padFile(archivePath);

        updateProgress.accept("Archive Created.\n");
        return archivePath;
    }

    /**
     * If the specified file does not have a size that is a multiple of the frame size, then the file is padded with
     * zeroes until it is. This is done to ensure FFMPEG has enough data to have an exact number of frames, otherwise
     * it will display an error about not having a full frame worth of bytes at the end of the file.
     *
     * @param file Path to the file.
     * @throws IOException If an I/O exception occurs.
     */
    protected void padFile(final @NonNull Path file) throws IOException {
        if (Files.notExists(file)) {
            throw new IOException("File does not exist.");
        }

        if (!Files.isRegularFile(file)) {
            throw new IOException("File is not a regular file.");
        }

        final SettingsTabModel settings = new SettingsTabModel();
        final var bytesPerFrame = (settings.getResolution().pixelCount / settings.getBlockSize().pixelCount) / 8;
        long padding = bytesPerFrame - (Files.size(file) % bytesPerFrame);

        if (padding == 0) {
            return;
        }

        try (final var outputStream = Files.newOutputStream(file, StandardOpenOption.APPEND)) {
            while (!this.isInterrupted() && padding > 0) {
                int temp = (int) Math.min(padding, Integer.MAX_VALUE);
                outputStream.write(new byte[temp]);
                padding -= temp;
            }
        }

        if (this.isInterrupted()) {
            onError.accept(new IOException("Padding process was interrupted."));
        }
    }
}
