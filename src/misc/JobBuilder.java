package misc;

import com.valkryst.VMVC.Settings;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Data;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Data
public class JobBuilder {
    /** The program settings. */
    private Settings settings;

    /** The name of the Job. */
    private String name;
    /** The output directory. */
    private String outputDirectory;
    /** The file(s) belonging to the Job.*/
    private List<File> files;
    /** Whether the Job is an Encode Job or a Decode Job. */
    private boolean isEncodeJob = true;

    /**
     * Constructs a new JobBuilder.
     *
     * @param settings
     *          The program settings.
     *
     * @throws NullPointerException
     *         If the settings is null.
     */
    public JobBuilder(final @NonNull Settings settings) {
        this.settings = settings;
        reset();
    }

    /**
     * Build a new job.
     *
     * @return
     *         The job.
     */
    public Job build() {
        checkState();
        return new Job(this);
    }

    /**
     * Checks the state of the builder.
     *
     * @throws java.lang.NullPointerException
     *          If the output directory or files list is null.
     */
    private void checkState() {
        Objects.requireNonNull(outputDirectory);
        Objects.requireNonNull(files);

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

        // Ensure the files are sorted from smallest to largest:
        this.files.sort(Comparator.comparing(File::length));
    }

    /** Resets the state of the builder. */
    public void reset() {
        name = null;
        setOutputToHomeDirectory();
        files = new ArrayList<>();
        isEncodeJob = true;
    }

    /** Sets the output directory to the home directory. */
    private void setOutputToHomeDirectory() {
        if (isEncodeJob) {
            final String defaultEncodeDir = settings.getStringSetting("Default Encoding Output Directory");

            if (defaultEncodeDir.isEmpty() == false) {
                final File file = new File(defaultEncodeDir);
                if (file.exists() && file.isDirectory()) {
                    outputDirectory = defaultEncodeDir;
                    return;
                }
            }
        } else {
            final String defaultDecodeDir = settings.getStringSetting("Default Decoding Output Directory");

            if (defaultDecodeDir.isEmpty() == false) {
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
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            final String alertMessage = "There was an issue retrieving the home directory path.\nSee the log file for more information.";
            final Alert alert = new Alert(Alert.AlertType.ERROR, alertMessage, ButtonType.OK);
            alert.showAndWait();
        }
    }
}
