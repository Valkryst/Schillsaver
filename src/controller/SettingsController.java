package controller;

import com.valkryst.VMVC.SceneManager;
import com.valkryst.VMVC.Settings;
import com.valkryst.VMVC.controller.Controller;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import model.SettingsModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.SettingsView;

import javax.swing.JFileChooser;
import java.awt.HeadlessException;
import java.io.File;

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
     */
    public SettingsController(final SceneManager sceneManager, final Settings settings) {
        super (sceneManager, settings, new SettingsModel(), new SettingsView(settings));
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
            view.getTextField_ffmpegExecutablePath().setText(selectFfmpegExecutablePath());
        }

        if (source.equals(view.getButton_selectDefaultEncodingFolder())) {
            view.getTextField_defaultEncodingFolder().setText(selectFolder());
        }

        if (source.equals(view.getButton_selectDefaultDecodingFolder())) {
            view.getTextField_defaultDecodingFolder().setText(selectFolder());
        }

        if (source.equals(view.getButton_accept())) {
            if (updateSettings()) {
                dialog.close();
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
        final String ffmpegPath = view.getTextField_ffmpegExecutablePath().getText();
        final String encodeFolderPath = view.getTextField_defaultEncodingFolder().getText();
        final String decodeFolderPath = view.getTextField_defaultDecodingFolder().getText();
        final String codec = view.getTextField_codec().getText();
        final String frameDimension = view.getComboBox_frameDimensions().getSelectionModel().getSelectedItem();
        final String frameRate = view.getComboBox_frameRate().getSelectionModel().getSelectedItem();
        final String blockSize = view.getComboBox_blockSize().getSelectionModel().getSelectedItem();

        // Validate FFMPEG Path:
        File temp = new File(ffmpegPath);

        if (temp.exists() && temp.isFile()) {
            settings.setSetting("FFMPEG Executable Path", ffmpegPath);
        } else {
            final String alertMessage = "The FFMPEG executable path either doesn't exist or isn't a file.";
            final Alert alert = new Alert(Alert.AlertType.WARNING, alertMessage, ButtonType.OK);
            alert.showAndWait();

            return false;
        }

        // Validate Encode Folder Path:
        temp = new File(encodeFolderPath);

        if (temp.exists() && temp.isDirectory()) {
            settings.setSetting("Default Encoding Output Directory", encodeFolderPath);
        } else {
            final String alertMessage = "The default encode folder path either doesn't exist or isn't a directory.";
            final Alert alert = new Alert(Alert.AlertType.WARNING, alertMessage, ButtonType.OK);
            alert.showAndWait();

            view.getTextField_defaultEncodingFolder().clear();
             return false;
        }

        // Validate Decode Folder Path:
        temp = new File(decodeFolderPath);

        if (temp.exists() && temp.isDirectory()) {
            settings.setSetting("Default Decoding Output Directory", decodeFolderPath);
        } else {
            final String alertMessage = "The default decode folder path either doesn't exist or isn't a directory.";
            final Alert alert = new Alert(Alert.AlertType.WARNING, alertMessage, ButtonType.OK);
            alert.showAndWait();

            view.getTextField_defaultDecodingFolder().clear();
            return false;
        }

        // Validate Codec:
        if (codec.isEmpty()) {
            final String alertMessage = "No codec was set.";
            final Alert alert = new Alert(Alert.AlertType.WARNING, alertMessage, ButtonType.OK);
            alert.showAndWait();

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
        final FileChooser fileChooser = new FileChooser();
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
        final JFileChooser fileChooser = new JFileChooser();
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
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            final String alertMessage = "There was an issue selecting the folder.\nSee the log file for more information.";
            final Alert alert = new Alert(Alert.AlertType.ERROR, alertMessage, ButtonType.OK);
            alert.showAndWait();
        }

        return "";
    }
}
