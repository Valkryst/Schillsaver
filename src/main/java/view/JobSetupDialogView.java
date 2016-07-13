package view;

import controller.JobSetupDialogController;
import handler.ConfigHandler;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import misc.Job;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JobSetupDialogView extends HBox {

    // todo JavaDoc
    @Getter private ListView<String> listView_selectedFiles = new ListView<>();

    /** The button to open the handler selection dialog. */
    @Getter private final Button button_addFiles = new Button("Add File(s)");
    /** The button to remove all files that are currently selected on the scrollpane_selectedFiles. */
    @Getter private final Button button_removeSelectedFiles = new Button("Remove File(s)");
    /** The button to remove all files from the list. */
    @Getter private final Button button_clearAllFiles = new Button("Clear List");

    /** The label to display an estimate of how long the Job will take to run. */
    @Getter private final Label label_job_estimatedDurationInMinutes = new Label("Estimated Time - Unknown");

    /** The button to close the JobCreationDialog while creating a Job. */
    @Getter private final Button button_accept = new Button("Accept");
    /** The button to close the JobCreationDialog without creating a Job. */
    @Getter private final Button button_cancel = new Button("Cancel");

    /** The text field for the name of the Job. */
    @Getter private final TextField field_jobName = new TextField();

    /** The comboBox to specify the type of Job to create. */
    @Getter private final ComboBox<String> comboBox_jobType = new ComboBox<>(FXCollections.observableArrayList("Encode", "Decode"));

    /** The text area for a rough description of the Job. */
    @Getter private final TextArea textArea_jobDescription = new TextArea();

    /** The toggle group of the "archive files before encoding" yes/no radio buttons. */
    @Getter private final ToggleGroup toggleGroup_archiveFiles = new ToggleGroup();
    /** The radio button that says that each of the currently selected files should be archived before encoding. */
    @Getter private final RadioButton toggleGroup_archiveFiles_yes = new RadioButton("Yes");
    /** The radio button that says that each of the currently selected files should not be archived before encoding. */
    @Getter private final RadioButton toggleGroup_archiveFiles_no = new RadioButton("No");

    /** The text field for the path to the directory in which to place the en/decoded file(s). */
    @Getter private final TextField textField_outputDirectory = new TextField();
    /** The button to select the folder to output the archive to if the "Yes" radio button is selected. */
    @Getter private final Button button_selectOutputDirectory = new Button("Select Output Folder");

    // todo JavaDoc
    public JobSetupDialogView(final Stage settingsStage, final JobSetupDialogController controller, final ConfigHandler configHandler, final Job jobToEdit) {
        // Setup Job  List:
        listView_selectedFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Setup Components:
        setTooltips();
        setFieldPrompts();

        setEventHandlers(controller);

        setRadioButtonToggleGroups();
        setRadioButtonDefaults(configHandler);

        // Load Existing Data:
        if(jobToEdit != null) {
            field_jobName.setText(jobToEdit.getName());

            textArea_jobDescription.setText(jobToEdit.getDescription());

            if(jobToEdit.isArchiveFiles()) {
                toggleGroup_archiveFiles_yes.setSelected(true);
            } else {

                toggleGroup_archiveFiles_no.setSelected(true);
            }

            for(final File f : jobToEdit.getFiles()) {
                listView_selectedFiles.getItems().addAll(f.getAbsolutePath());
            }
        } else {
            // Set a default job title based on the current time
            field_jobName.setText("Job " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }

        // Setup Combo Box:
        comboBox_jobType.getSelectionModel().select(0);

        // if possible, get default 'home' directory and set it as output default.
        try {
            File home = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory();
            textField_outputDirectory.setText(home.getCanonicalPath() + "/");
        } catch (Exception e) {
            // discard
        }

        // Setup the Layout:
        this.setSpacing(4);
        this.getChildren().addAll(setupLeftPanel(), setupRightPanel(settingsStage));
    }

    /** Sets the default tooltips for all relevant components. */
    private void setTooltips() {
        button_addFiles.setTooltip(new Tooltip("Open the file selection dialog to select a new file."));
        button_removeSelectedFiles.setTooltip(new Tooltip("Removes all files that are currently selected on the list."));
        button_clearAllFiles.setTooltip(new Tooltip("Clears the list of all files."));
        button_accept.setTooltip(new Tooltip("Accepts the Job settings and closes the dialog and creates a Job."));
        button_cancel.setTooltip(new Tooltip("Rejects the Job settings and closes the dialog without creating a Job."));

        comboBox_jobType.setTooltip(new Tooltip("Defines which type of Job to create."));

        textField_outputDirectory.setTooltip(new Tooltip("The directory in which to place the en/decoded file(s)."));
        button_selectOutputDirectory.setTooltip(new Tooltip("Open the directory selection dialog to select an output directory."));
    }

    /**
     * Sets the EventHandler for all relevant components.
     *
     * @param controller
     *         The EventHandler to use.
     */
    private void setEventHandlers(final JobSetupDialogController controller) {
        button_addFiles.setOnAction(controller);
        button_removeSelectedFiles.setOnAction(controller);
        button_clearAllFiles.setOnAction(controller);
        button_accept.setOnAction(controller);
        button_cancel.setOnAction(controller);
        comboBox_jobType.setOnAction(controller);
        button_selectOutputDirectory.setOnAction(controller);
        toggleGroup_archiveFiles_yes.setOnAction(controller);
    }

    /** Groups radio buttons into their toggle groups. */
    private void setRadioButtonToggleGroups() {
        toggleGroup_archiveFiles_yes.setToggleGroup(toggleGroup_archiveFiles);
        toggleGroup_archiveFiles_no.setToggleGroup(toggleGroup_archiveFiles);
    }

    /** Sets the default prompt text for all relevant fields. */
    private void setFieldPrompts() {
        field_jobName.setPromptText("Enter a name for the Job.");
        textArea_jobDescription.setPromptText("Enter a description for the Job.\nThis is not required.");
        textField_outputDirectory.setPromptText("Output Directory");
    }

    /**
     * Sets the default selections for the radio button toggle groups.
     *
     * @param configHandler
     *         todo JavaDoc
     */
    private void setRadioButtonDefaults(final ConfigHandler configHandler) {
        if(configHandler.getCompressionProgramPath().isEmpty()) {
            toggleGroup_archiveFiles_no.setSelected(true);
        } else {
            toggleGroup_archiveFiles_yes.setSelected(true);
        }

        // Disable archive options if no archival program is set:
        if(configHandler.getCompressionProgramPath().isEmpty()) {
            toggleGroup_archiveFiles_yes.setDisable(true);
            toggleGroup_archiveFiles_no.setDisable(true);
        }
    }

    /**
     * Constructs the left panel.
     *
     * @return
     *         The left panel.
     */
    private VBox setupLeftPanel() {
        final HBox top = new HBox(10);
        final HBox bottom = new HBox(10);
        final VBox panel = new VBox(4);

        top.setAlignment(Pos.CENTER);
        bottom.setAlignment(Pos.CENTER);

        HBox.setHgrow(panel, Priority.ALWAYS);
        VBox.setVgrow(listView_selectedFiles, Priority.ALWAYS);

        top.getChildren().addAll(button_addFiles, button_removeSelectedFiles, button_clearAllFiles);
        bottom.getChildren().addAll(button_accept, button_cancel);
        panel.getChildren().addAll(top, listView_selectedFiles, bottom);

        return panel;
    }

    /**
     * Constructs the right panel.
     *
     * @return
     *         The right panel.
     */
    private VBox setupRightPanel(final Stage settingsStage) {
        final VBox top = setupTopRightPanel();
        final VBox bottom = setupBottomRightPanel(settingsStage);
        final VBox panel = new VBox(4);

        HBox.setHgrow(panel, Priority.ALWAYS);
        VBox.setVgrow(textArea_jobDescription, Priority.ALWAYS);

        panel.getChildren().addAll(top, bottom);

        return panel;
    }

    /**
     * Constructs the top panel of the right panel.
     *
     * @return
     *         The top panel of the right panel.
     */
    private VBox setupTopRightPanel() {
        final HBox top = new HBox(10);
        final VBox bottom= new VBox(4);
        final VBox panel = new VBox(4);

        HBox.setHgrow(field_jobName, Priority.ALWAYS);
        VBox.setVgrow(textArea_jobDescription, Priority.ALWAYS);
        VBox.setVgrow(bottom, Priority.ALWAYS);
        VBox.setVgrow(panel, Priority.ALWAYS);

        bottom.setAlignment(Pos.CENTER);

        top.getChildren().addAll(comboBox_jobType, field_jobName);
        bottom.getChildren().addAll(textArea_jobDescription, label_job_estimatedDurationInMinutes);
        panel.getChildren().addAll(top, bottom);

        return panel;
    }

    /**
     * Constructs the bottom panel of the right panel.
     *
     * @return
     *         The bottom panel of the right panel.
     */
    private VBox setupBottomRightPanel(final Stage settingsStage) {
        final HBox pane_panel_singleArchive = new HBox(10);
        pane_panel_singleArchive.setAlignment(Pos.CENTER);
        pane_panel_singleArchive.getChildren().addAll(toggleGroup_archiveFiles_yes, toggleGroup_archiveFiles_no);

        final TitledPane pane_singleArchive = new TitledPane();
        HBox.setHgrow(pane_singleArchive, Priority.ALWAYS);
        pane_singleArchive.setText("Pack File(s) into Single Archive");
        pane_singleArchive.setCollapsible(false);
        pane_singleArchive.heightProperty().addListener((observable, oldValue, newValue) -> {
            settingsStage.sizeToScene();
        });
        pane_singleArchive.setContent(pane_panel_singleArchive);



        final HBox top = new HBox(10);
        top.getChildren().addAll(pane_singleArchive);

        final HBox bottom = new HBox(10);
        HBox.setHgrow(textField_outputDirectory, Priority.ALWAYS);
        bottom.getChildren().addAll(textField_outputDirectory, button_selectOutputDirectory);

        final VBox panel = new VBox(4);
        panel.getChildren().addAll(top, bottom);

        return panel;
    }

    /** @return Whether or not the Job is an Encode Job. If not, then it's a Decode Job. */
    public boolean getIsEncodeJob() {
        return comboBox_jobType.getSelectionModel().getSelectedItem().equals("Encode");
    }
}
