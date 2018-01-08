package model;

import com.valkryst.VMVC.model.Model;
import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobModel extends Model {
    /** The job's files. */
    private final List<File> files = new ArrayList<>();

    /**
     * Adds a file to the job's files.
     *
     * @param file
     *          The file.
     *
     * @throws NullPointerException
     *          If the file is null.
     */
    public void addFile(final @NonNull File file) {
        files.add(file);
    }

    /**
     * Removes every file, from the job's files, whose file name matches
     * the specified file name.
     *
     * @param fileName
     *          The file name to look for.
     *
     * @throws NullPointerException
     *          If the fileName is null.
     */
    public void removeFilesWithFilename(final @NonNull String fileName) {
        files.removeIf(file -> file.getName().equals(fileName));
    }

    /**
     * Retrieves the job's files.
     *
     * @return
     *          An unmodifiable list of the job's files.
     */
    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }
}
