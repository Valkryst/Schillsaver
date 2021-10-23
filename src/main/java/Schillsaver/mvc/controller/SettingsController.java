package Schillsaver.mvc.controller;

import Schillsaver.SceneManager;
import Schillsaver.mvc.model.SettingsModel;
import Schillsaver.mvc.view.SettingsView;
import Schillsaver.setting.Settings;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.Setter;

import javax.swing.JFileChooser;
import java.awt.HeadlessException;
import java.io.File;

public class SettingsController extends Controller implements EventHandler {
    /** The dialog stage containing the setting view. */
    @Setter private Stage dialog;

    /**
     * Constructs a new MainController.
     *
     * @param model
     *          The model.
     *
     * @param view
     *          The view.
     *
     * @throws NullPointerException
     *          If the model or view are null.
     */
    public SettingsController(final @NonNull SettingsModel model, final @NonNull SettingsView view) {
        super(model, view);

        view.getButton_undo().setDisable(!model.canUndo());
        view.getButton_redo().setDisable(!model.canRedo());

        view.getButton_undo().setOnAction(this);
        view.getButton_redo().setOnAction(this);
        view.getButton_selectFfmpegExecutablePath().setOnAction(this);
        view.getButton_selectDefaultEncodingFolder().setOnAction(this);
        view.getButton_selectDefaultDecodingFolder().setOnAction(this);
        view.getButton_accept().setOnAction(this);
        view.getButton_cancel().setOnAction(this);
    }

    @Override
    public void handle(final Event event) {
        final SettingsModel model = (SettingsModel) super.getModel();
        final SettingsView view = (SettingsView) super.getView();

        final Object source = event.getSource();

        // Undo Button
        if (source.equals(view.getButton_undo())) {
            if (model.canUndo()) {
                model.undo();
                view.refreshComponents();

                view.getButton_undo().setDisable(!model.canUndo());
                view.getButton_redo().setDisable(!model.canRedo());
            }
            return;
        }

        // Redo Button
        if (source.equals(view.getButton_redo())) {
            if (model.canRedo()) {
                model.redo();
                view.refreshComponents();

                view.getButton_undo().setDisable(!model.canUndo());
                view.getButton_redo().setDisable(!model.canRedo());
            }
            return;
        }

        // Select FFMPEG Executable Path Button
        if (source.equals(view.getButton_selectFfmpegExecutablePath())) {
            String newPath = selectFfmpegExecutablePath();

            if (newPath.isEmpty()) {
                newPath = view.getTextField_ffmpegExecutablePath().getText();
            }

            view.getTextField_ffmpegExecutablePath().setText(newPath);
            return;
        }

        // Select Default Encoding Folder Button
        if (source.equals(view.getButton_selectDefaultEncodingFolder())) {
            String newPath = selectFolder();

            if (newPath.isEmpty()) {
                newPath = view.getTextField_defaultEncodingFolder().getText();
            }

            view.getTextField_defaultEncodingFolder().setText(newPath);
            return;
        }

        // Select Default Decoding Folder Button
        if (source.equals(view.getButton_selectDefaultDecodingFolder())) {
            String newPath = selectFolder();

            if (newPath.isEmpty()) {
                newPath = view.getTextField_defaultDecodingFolder().getText();
            }

            view.getTextField_defaultDecodingFolder().setText(newPath);
            return;
        }

        // Accept Button
        if (source.equals(view.getButton_accept())) {
            if (updateSettings()) {
                model.processQueue();
                Settings.getInstance().save();
                dialog.close();
                return;
            }
        }

        // Cancel Button
        if (source.equals(view.getButton_cancel())) {
            dialog.close();
            return;
        }
    }

    /**
     * Updates the program setting with the values in the view.
     *
     * @return
     *          Whether or not the update succeeded.
     */
    private boolean updateSettings() {
        final Settings settings = Settings.getInstance();
        final SettingsView view = (SettingsView) super.getView();
        final SettingsModel model = (SettingsModel) super.getModel();

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

        System.out.println(decodeFolderPath);
        System.out.println(encodeFolderPath);

        // Validate FFMPEG Path:
        if (settings.getStringSetting("FFMPEG Executable Path").equals(ffmpegPath) == false) {
            final File temp = new File(ffmpegPath);

            if (temp.exists() && temp.isFile()) {
                model.queue("FFMPEG Executable Path", ffmpegPath);
            } else {
                final Alert alert = new Alert(Alert.AlertType.WARNING, "The FFMPEG executable path either doesn't exist or isn't a file.", ButtonType.OK);
                alert.showAndWait();
                return false;
            }
        }

        // Validate Encode Folder Path:
        if (settings.getStringSetting("Default Encoding Output Directory").equals(encodeFolderPath) == false) {
            final File temp = new File(encodeFolderPath);

            if (encodeFolderPath.isEmpty() || (temp.exists() && temp.isDirectory())) {
                model.queue("Default Encoding Output Directory", encodeFolderPath);
            } else {
                final Alert alert = new Alert(Alert.AlertType.ERROR, "The default encode folder path either doesn't exist or isn't a directory.", ButtonType.OK);
                alert.showAndWait();

                view.getTextField_defaultEncodingFolder().clear();
                return false;
            }
        }

        // Validate Decode Folder Path:
        if (settings.getStringSetting("Default Decoding Output Directory").equals(decodeFolderPath) == false) {
            final File temp = new File(decodeFolderPath);

            if (decodeFolderPath.isEmpty() || (temp.exists() && temp.isDirectory())) {
                model.queue("Default Decoding Output Directory", decodeFolderPath);
            } else {
                final Alert alert = new Alert(Alert.AlertType.ERROR, "The default decode folder path either doesn't exist or isn't a directory.", ButtonType.OK);
                alert.showAndWait();

                view.getTextField_defaultDecodingFolder().clear();
                return false;
            }
        }

        // Validate Codec:
        if (codec.isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.WARNING, "No codec was set. Defaulting to libx264.", ButtonType.OK);
            alert.showAndWait();

            codec = "libx264";
            view.getTextField_codec().setText("libx264");
        }

        if (settings.getStringSetting("Encoding Codec").equals(codec) == false) {
            model.queue("Encoding Codec", codec);
        }

        // Set other setting:
        if (settings.getStringSetting("Encoding Frame Dimensions").equals(frameDimension) == false) {
            model.queue("Encoding Frame Dimensions", frameDimension);
        }

        if (settings.getStringSetting("Encoding Frame Rate").equals(frameRate) == false) {
            model.queue("Encoding Frame Rate", frameRate);
        }


        if (settings.getStringSetting("Encoding Block Size").equals(blockSize) == false) {
            model.queue("Encoding Block Size", blockSize);
        }

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

        final File selectedFile = fileChooser.showOpenDialog(SceneManager.getInstance().getStage());

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
            final Alert alert = new Alert(Alert.AlertType.ERROR, "There was an issue selecting the folder.\nSee the log file for more information.", ButtonType.OK);
            alert.showAndWait();

			e.printStackTrace();
        }

        return "";
    }
}
