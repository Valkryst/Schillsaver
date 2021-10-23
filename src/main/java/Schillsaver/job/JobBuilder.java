package Schillsaver.job;

import Schillsaver.job.archive.ArchiveType;
import Schillsaver.setting.Settings;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Data;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
public class JobBuilder {
    /** The name of the Job. */
    private String name;
    /** The output directory. */
    private String outputDirectory;
    /** The type of archiver to archive the file(s) with. */
    private ArchiveType archiveType;
    /** The file(s) to process.*/
    private List<File> files;
    /** Whether the Job is an Encode Job or a Decode Job. */
    private boolean isEncodeJob;

    /**
     * Builds a new Job.
     *
     * @return
     *         The job.
     *
     * @throws IllegalArgumentException
     *          If the output directory doesn't exist or isn't a directory.
     */
    public Job build() {
        if (name == null || name.isEmpty()) {
            final UUID uuid = UUID.randomUUID();
            name = uuid.toString();
        }

        // If the output dir isn't specified, try setting it to the home dir.
        if (outputDirectory.isEmpty()) {
            setOutputToHomeDirectory();

            if (outputDirectory.isEmpty()) {
                throw new NullPointerException("The output directory was not set.");
            }
        }

        // Ensure output directory has the correct trailing slash:
        if (!outputDirectory.endsWith("\\") && !outputDirectory.endsWith("/")) {
            outputDirectory += "/";
        }

        // Ensure the output directory is actually a directory:
        final File outputDirectory = new File(this.outputDirectory);

        if (! outputDirectory.exists()) {
            if (! outputDirectory.mkdir()) {
                throw new IllegalArgumentException("The output directory '" + this.outputDirectory + "' does not exist and could not be created.");
            }
        }

        if (! outputDirectory.isDirectory()) {
            throw new IllegalArgumentException("The output directory '" + this.outputDirectory + "' is not a directory.");
        }

        // Ensure there's an archive type:
        if (archiveType == null) {
            archiveType = ArchiveType.ZIP;
        }

        // Ensure there's a files list:
        if (files == null) {
            files = new LinkedList<>();
        }

        return new Job(this);
    }

    /** Sets the output directory to the home directory. */
    private void setOutputToHomeDirectory() {
        if (isEncodeJob) {
            final String defaultEncodeDir = Settings.getInstance().getStringSetting("Default Encoding Output Directory");

            if (! defaultEncodeDir.isEmpty()) {
                final File file = new File(defaultEncodeDir);
                if (file.exists() && file.isDirectory()) {
                    outputDirectory = defaultEncodeDir;
                    return;
                }
            }
        } else {
            final String defaultDecodeDir = Settings.getInstance().getStringSetting("Default Decoding Output Directory");

            if (! defaultDecodeDir.isEmpty()) {
                final File file = new File(defaultDecodeDir);
                if (file.exists() && file.isDirectory()) {
                    outputDirectory = defaultDecodeDir;
                    return;
                }
            }
        }

        try {
            final File home = FileSystemView.getFileSystemView().getHomeDirectory();
            outputDirectory = home.getCanonicalPath() + "/";
        } catch (final IOException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "There was an issue retrieving the home directory path.\nSee the log file for more information.", ButtonType.OK);
            alert.showAndWait();

			e.printStackTrace();
        }
    }
}
