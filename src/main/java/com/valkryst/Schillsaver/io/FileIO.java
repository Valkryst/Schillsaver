package Schillsaver.io;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileIO {
    /**
     * Saves the specified object to a JSON file.
     *
     * @param filePath The path to the file.
     * @param object The object to save.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void saveJsonToDisk(@NonNull Path filePath, final @NonNull Object object) throws IOException {
        if (Files.isDirectory(filePath)) {
            throw new IOException("The path points to a folder, not a file.");
        }

        if (!FileIO.getFileExtension(filePath).equals("json")) {
            System.out.println("The file path does not end with \".json\". Changing it to: " + filePath.getFileName() + ".json");
            filePath = filePath.resolveSibling(filePath.getFileName() + ".json");
        }

        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, new Gson().toJson(object));
    }

    /**
     * Loads an object from a JSON file.
     *
     * @param filePath The path to the file.
     *
     * @return The loaded object.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static JsonObject loadJsonFromDisk(final @NonNull Path filePath) throws IOException {
        if (Files.notExists(filePath)) {
            throw new IOException("There is no file at the specified path.");
        }

        if (Files.isDirectory(filePath)) {
            throw new IOException("The path points to a folder, not a file.");
        }

        if (Files.size(filePath) == 0) {
            throw new IOException("The file is empty.");
        }

        if (!FileIO.getFileExtension(filePath).equals("json")) {
            throw new IOException("The file must be a JSON file.");
        }

        return new Gson().fromJson(Files.readString(filePath), JsonObject.class);
    }

    /**
     * Retrieves the path, relative to the specified directory, to the file with the specified filename.
     *
     * @param directoryPath The path to the directory.
     * @param filename The name of the file.
     *
     * @return The path to the file with the specified filename.
     */
    public static Path getFilePath(final @NonNull Path directoryPath, final @NonNull String filename) {
        if (filename.isEmpty()) {
            throw new IllegalArgumentException("You must specify a filename.");
        }

        return directoryPath.resolve(filename);
    }

    /**
     * Retrieves the file extension of the specified file.
     *
     * @param filePath The path to the file.
     * @return The file extension.
     */
    private static String getFileExtension(final @NonNull Path filePath) {
        final var fileName = filePath.getFileName().toString();
        final var index = fileName.lastIndexOf('.');

        if (index == -1) {
            return "";
        }

        return fileName.substring(index + 1);
    }
}
