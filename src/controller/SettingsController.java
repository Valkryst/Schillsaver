package controller;

import com.valkryst.VMVC.AlertManager;
import com.valkryst.VMVC.SceneManager;
import com.valkryst.VMVC.Settings;
import com.valkryst.VMVC.controller.Controller;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.Setter;
import model.SettingsModel;
import org.apache.logging.log4j.LogManager;
import view.SettingsView;

import javax.swing.JFileChooser;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;

public class SettingsController extends Controller<SettingsModel, SettingsView> implements EventHandler {
    /** The dialog stage containing the settings view. */
    @Setter private Stage dialog;

    /**
     * Constructs a new SettingsController.
     *
     * @param sceneManager
     *          The scene manager.
     *
     * @param settings
     *          The program settings.
     *
     * @throws NullPointerException
     *         If the sceneManager or settings is null.
     */
    SettingsController(final @NonNull SceneManager sceneManager, final @NonNull Settings settings) {
        super(sceneManager, settings, new SettingsModel(), new SettingsView(settings));
        addEventHandlers();
    }

    /** Sets all of the view's controls to use this class as their event handler. */
    private void addEventHandlers() {
        view.getButton_selectFfmpegExecutablePath().setOnAction(this);
        view.getButton_selectDefaultEncodingFolder().setOnAction(this);
        view.getButton_selectDefaultDecodingFolder().setOnAction(this);
        view.getButton_accept().setOnAction(this);
        view.getButton_cancel().setOnAction(this);
    }

    @Override
    public void handle(final Event event) {
        final Object source = event.getSource();

        if (source.equals(view.getButton_selectFfmpegExecutablePath())) {
            String newPath = selectFfmpegExecutablePath();
            final String oldPath = view.getTextField_ffmpegExecutablePath().getText();

            if (newPath.isEmpty()) {
                newPath = oldPath;
            }

            view.getTextField_ffmpegExecutablePath().setText(newPath);
            return;
        }

        if (source.equals(view.getButton_selectDefaultEncodingFolder())) {
            String newPath = selectFolder();
            final String oldPath = view.getTextField_defaultEncodingFolder().getText();

            if (newPath.isEmpty()) {
                newPath = oldPath;
            }

            view.getTextField_defaultEncodingFolder().setText(newPath);
            return;
        }

        if (source.equals(view.getButton_selectDefaultDecodingFolder())) {
            String newPath = selectFolder();
            final String oldPath = view.getTextField_defaultDecodingFolder().getText();

            if (newPath.isEmpty()) {
                newPath = oldPath;
            }

            view.getTextField_defaultDecodingFolder().setText(newPath);
            return;
        }

        if (source.equals(view.getButton_accept())) {
            if (updateSettings()) {
                try {
                    settings.saveSettings();
                } catch (final IOException e) {
                    LogManager.getLogger().error(e);

                    AlertManager.showErrorAndWait("There was an issue saving the settings.\nSee the log file for more information.");
                }

                dialog.close();
                return;
            }
        }

        if (source.equals(view.getButton_cancel())) {
            dialog.close();
        }
    }

    /**
     * Updates the program settings with the values in the view.
     *
     * @return
     *          Whether or not the update succeeded.
     */
    private boolean updateSettings() {
        String ffmpegPath = view.getTextField_ffmpegExecutablePath().getText();
        String encodeFolderPath = view.getTextField_defaultEncodingFolder().getText();
        String decodeFolderPath = view.getTextField_defaultDecodingFolder().getText();
        String codec = view.getTextField_codec().getText();
        String frameDimension = view.getComboBox_frameDimensions().getSelectionModel().getSelectedItem();
        String frameRate = view.getComboBox_frameRate().getSelectionModel().getSelectedItem();
        String blockSize = view.getComboBox_blockSize().getSelectionModel().getSelectedItem();

        // Ensure some strings are empty if they're null.
        if (ffmpegPath == null) {
            ffmpegPath = "";
        }

        if (encodeFolderPath == null) {
            encodeFolderPath = "";
        }

        if (decodeFolderPath == null) {
            decodeFolderPath = "";
        }

        if (codec == null) {
            codec = "";
        }

        // Validate FFMPEG Path:
        File temp = new File(ffmpegPath);

        if (temp.exists() && temp.isFile()) {
            settings.setSetting("FFMPEG Executable Path", ffmpegPath);
        } else {
            AlertManager.showWarningAndWait("The FFMPEG executable path either doesn't exist or isn't a file.");
            return false;
        }

        // Validate Encode Folder Path:
        temp = new File(encodeFolderPath);

        if (encodeFolderPath.isEmpty() || (temp.exists() && temp.isDirectory())) {
            settings.setSetting("Default Encoding Output Directory", encodeFolderPath);
        } else {
            AlertManager.showWarningAndWait("The default encode folder path either doesn't exist or isn't a directory.");
            view.getTextField_defaultEncodingFolder().clear();
            return false;
        }

        // Validate Decode Folder Path:
        temp = new File(decodeFolderPath);

        if (decodeFolderPath.isEmpty() || (temp.exists() && temp.isDirectory())) {
            settings.setSetting("Default Decoding Output Directory", decodeFolderPath);
        } else {
            AlertManager.showWarningAndWait("The default decode folder path either doesn't exist or isn't a directory.");
            view.getTextField_defaultDecodingFolder().clear();
            return false;
        }

        // Validate Codec:
        if (codec.isEmpty()) {
            AlertManager.showWarningAndWait("No codec was set. Defaulting to libx264.");
            view.getTextField_codec().setText("libx264");
            return false;
        } else {
            settings.setSetting("Encoding Codec", codec);
        }

        // Set other settings:
        settings.setSetting("Encoding Frame Dimensions", frameDimension);
        settings.setSetting("Encoding Frame Rate", frameRate);
        settings.setSetting("Encoding Block Size", blockSize);
        return true;
    }

    /**
     * Opens a file chooser for the user to select the FFMPEG executable.
     *
     * @return
     *          The file path.
     */
    private String selectFfmpegExecutablePath() {
        final var fileChooser = new FileChooser();
        fileChooser.setTitle("FFMPEG Executable Selection");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        final File selectedFile = fileChooser.showOpenDialog(sceneManager.getPrimaryStage());

        if (selectedFile != null) {
            if (selectedFile.exists()) {
                return selectedFile.getPath();
            }
        }

        return "";
    }

    /**
     * Open a file chooser for the user to select a directory.
     *
     * @return
     *          The file path.
     */
    private String selectFolder() {
        final var fileChooser = new JFileChooser();
        fileChooser.setDragEnabled(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.setDialogTitle("Directory Selection");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        fileChooser.setApproveButtonText("Accept");

        try {
            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();

                if (file.exists() && file.isDirectory()) {
                    return file.getPath() + "/";
                } else {
                    return "";
                }
            }
        } catch(final HeadlessException e) {
            LogManager.getLogger().error(e);
            AlertManager.showErrorAndWait("There was an issue selecting the folder.\nSee the log file for more information.");
        }

        return "";
    }
}
