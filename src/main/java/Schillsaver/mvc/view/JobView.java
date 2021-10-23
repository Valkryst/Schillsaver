package Schillsaver.mvc.view;

import Schillsaver.mvc.JFXHelper;
import Schillsaver.mvc.model.JobModel;
import Schillsaver.setting.Settings;
import com.valkryst.VIcons.VIconType;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;

public class JobView extends View {
    @Getter private Button button_addFiles;
    @Getter private Button button_removeSelectedFiles;
    @Getter private Button button_selectOutputFolder;
    @Getter private Button button_accept;
    @Getter private Button button_cancel;

    @Getter private TextField textField_jobName;
    @Getter private TextField textField_outputFolder;

    @Getter private ListView<String> fileList;

    @Getter private ComboBox<String> comboBox_jobType;

    /**
     * Constructs a new JobView.
     *
     * @param model
     *          The model.
     *
     * @throws NullPointerException
     *         If the model is null.
     */
    public JobView(final @NonNull JobModel model) {
        initializeComponents(model);
        setComponentTooltips();

        final Pane fileSelectionArea = createFileSelectionArea();
        final Pane fileDetailsArea = createJobDetailsArea();
        final Pane bottomMenuBar = createBottomMenuBar();

        final VBox vBox = new VBox(fileSelectionArea, fileDetailsArea);

        VBox.setVgrow(vBox, Priority.ALWAYS);

        super.setPane(new VBox());
        super.getPane().setMinSize(512, 512);
        super.getPane().getChildren().addAll(vBox, bottomMenuBar);
    }

    /**
     * Initializes the components.
     *
     * @param model
     *          The model.
     *
     * @throws NullPointerException
     *         If the model is null.
     */
    private void initializeComponents(final @NonNull JobModel model) {
        button_addFiles = new Button("Add Files");
        button_removeSelectedFiles = new Button("Remove Selected Files");
        button_selectOutputFolder = new Button("Select Output Folder");
        button_accept = JFXHelper.createIconButton(VIconType.BUTTON_ACCEPT.getFilePath(), 16, 16);
        button_cancel = JFXHelper.createIconButton(VIconType.BUTTON_CANCEL.getFilePath(), 16, 16);

        textField_jobName = new TextField();
        textField_jobName.setPromptText("Job Name");

        textField_outputFolder = new TextField();
        textField_outputFolder.setPromptText("Output Folder Path");

        fileList = new ListView<>();
        fileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        comboBox_jobType = JFXHelper.createComboBox("Encode", "Decode");
        comboBox_jobType.getSelectionModel().select(model.getJob().isEncodeJob() ? "Encode" : "Decode");
        textField_outputFolder.setText(Settings.getInstance().getStringSetting(model.getJob().isEncodeJob() ? "Default Encoding Output Directory" : "Default Decoding Output Directory"));
        comboBox_jobType.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (textField_outputFolder.getText().isEmpty()) {
                return;
            }

            if (newValue.equals("Encode")) {
                textField_outputFolder.setText(Settings.getInstance().getStringSetting("Default Encoding Output Directory"));
            } else {
                textField_outputFolder.setText(Settings.getInstance().getStringSetting("Default Decoding Output Directory"));
            }

            if (textField_outputFolder.getText().isEmpty()) {
                try {
                    final File home = FileSystemView.getFileSystemView().getHomeDirectory();
                    textField_outputFolder.setText(home.getCanonicalPath() + "/");
                } catch (final IOException e) {
                    final Alert alert = new Alert(Alert.AlertType.ERROR, "There was an issue retrieving the home directory path.", ButtonType.OK);
                    alert.showAndWait();

					e.printStackTrace();
                }
            }
        });
    }

    /** Sets the tooltips of the components. */
    private void setComponentTooltips() {
        JFXHelper.setTooltip(button_addFiles, "Opens a file selection dialog to select files to add to the job.");
        JFXHelper.setTooltip(button_removeSelectedFiles, "Removes all selected files from the list.");
        JFXHelper.setTooltip(button_selectOutputFolder, "Opens a folder selection dialog to select the output folder of the job.");
        JFXHelper.setTooltip(button_accept, "Accepts and creates the job, then returns to the main screen.");
        JFXHelper.setTooltip(button_cancel, "Cancels job creation and returns to the main screen.");

        JFXHelper.setTooltip(textField_jobName, "The name of the job. This must be unique.");

        JFXHelper.setTooltip(comboBox_jobType, "The type of the job.");

        JFXHelper.setTooltip(textField_outputFolder, "The path of the output folder.");
    }

    /**
     * Creates the file selection area.
     *
     * @return
     *         The file selection area.
     */
    private Pane createFileSelectionArea() {
        final GridPane buttonPane = JFXHelper.createHorizontalGridPane(button_addFiles, button_removeSelectedFiles);

        // Create the Pane:
        final VBox pane = new VBox(buttonPane, fileList);

        // Ensure pane and file list fill all available vertical space:
        VBox.setVgrow(pane, Priority.ALWAYS);
        VBox.setVgrow(fileList, Priority.ALWAYS);

        return pane;
    }

    /**
     * Creates the job details area.
     *
     * @return
     *         The job details area.
     */
    private Pane createJobDetailsArea() {
        // Add job type & name side-by-side
        final HBox typeNamePane = new HBox(comboBox_jobType, textField_jobName);
        HBox.setHgrow(textField_jobName, Priority.ALWAYS);

        // Add output folder field/button side-by-side:
        final HBox outputPane = new HBox(textField_outputFolder, button_selectOutputFolder);
        HBox.setHgrow(textField_outputFolder, Priority.ALWAYS);

        // Add panes to a VBox
        final VBox vBox = new VBox(typeNamePane, outputPane);
        HBox.setHgrow(vBox, Priority.ALWAYS);

        return vBox;
    }

    /**
     * Creates the bottom menu bar.
     *
     * @return
     *         The bottom menu bar.
     */
    private Pane createBottomMenuBar() {
        return JFXHelper.createHorizontalGridPane(button_accept, button_cancel);
    }
}
