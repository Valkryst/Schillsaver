package view;

import controller.JobSetupDialogController;
import handler.ConfigHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JobSetupDialogView extends HBox {

    // todo JavaDoc
    private ListView<String> listView_selectedFiles = new ListView<>();

    /** The button to open the handler selection dialog. */
    private final Button button_addFiles = new Button("Add File(s)");
    /** The button to remove all files that are currently selected on the scrollpane_selectedFiles. */
    private final Button button_removeSelectedFiles = new Button("Remove File(s)");
    /** The button to remove all files from the list. */
    private final Button button_clearAllFiles = new Button("Clear List");

    /** The label to display an estimate of how long the Job will take to run. */
    private final Label label_job_estimatedDurationInMinutes = new Label("Estimated Time - Unknown");

    /** The button to close the JobCreationDialog while creating a Job. */
    private final Button button_accept = new Button("Accept");
    /** The button to close the JobCreationDialog without creating a Job. */
    private final Button button_cancel = new Button("Cancel");

    // todo JavaDoc
    private final TextField field_jobName = new TextField();

    /** The comboBox to specify the type of Job to create. */
    private final ComboBox<String> comboBox_jobType = new ComboBox<>(FXCollections.observableArrayList("Encode", "Decode"));

    // todo JavaDoc
    private final TextArea textArea_jobDescription = new TextArea();

    /** The toggle group of the "archive all files into a single archive before encoding" yes/no radio buttons. */
    private final ToggleGroup toggleGroup_singleArchive = new ToggleGroup();
    /** The radio button that says that each of the currently selected files should be archived as a single archive before encoding. */
    private final RadioButton radioButton_singleArchive_yes = new RadioButton("Yes");
    /** The radio button that says that each of the currently selected files should be archived individually before encoding them individually. */
    private final RadioButton radioButton_singleArchive_no = new RadioButton("No");

    /** The toggle group of the "archive just this handler before encoding" yes/no radio buttons. */
    private final ToggleGroup toggleGroup_individualArchives = new ToggleGroup();
    /** The radio button that says that each handler should be archived individually before encoding each of them individually. */
    private final RadioButton radioButton_individualArchives_yes = new RadioButton("Yes");
    /** The radio button that says that each handler should not be archived individually before encoding each of them individually. */
    private final RadioButton radioButton_individualArchives_no = new RadioButton("No");

    /** The text field for the path to the directory in which to place the en/decoded file(s). */
    private final TextField textField_outputDirectory = new TextField();
    /** The button to select the folder to output the archive to if the "Yes" radio button is selected. */
    private final Button button_selectOutputDirectory = new Button("Select Output Folder");

    // todo JavaDoc
    public JobSetupDialogView(final Stage settingsStage, final JobSetupDialogController controller, final ConfigHandler configHandler) {
        // Setup Job  List:
        listView_selectedFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Setup Toggle Groups:
        radioButton_singleArchive_yes.setToggleGroup(toggleGroup_singleArchive);
        radioButton_singleArchive_no.setToggleGroup(toggleGroup_singleArchive);

        radioButton_individualArchives_yes.setToggleGroup(toggleGroup_individualArchives);
        radioButton_individualArchives_no.setToggleGroup(toggleGroup_individualArchives);

        // Set Default Values:
        if(configHandler.getCompressionProgramPath().isEmpty()) {
            radioButton_singleArchive_no.setSelected(true);
            radioButton_individualArchives_no.setSelected(true);
        } else {
            radioButton_singleArchive_yes.setSelected(true);
            radioButton_individualArchives_no.setSelected(true);
        }

        // Disable Archive Options if Archival Program Not Set:
        if(configHandler.getCompressionProgramPath().isEmpty()) {
            radioButton_singleArchive_yes.setDisable(true);
            radioButton_singleArchive_no.setDisable(true);

            radioButton_individualArchives_yes.setDisable(true);
            radioButton_individualArchives_no.setDisable(true);
        }

        // Set Text Field/Area Background Text:
        field_jobName.setPromptText("Enter a name for the Job.");

        // Set a default job title based on the current time
        field_jobName.setText("Job " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        textArea_jobDescription.setPromptText("Enter a description for the Job.\nThis is not required.");
        textField_outputDirectory.setPromptText("Output Directory");

        // Setup Combo Box:
        comboBox_jobType.getSelectionModel().select(0);

        // Set Component Tooltips:
        button_addFiles.setTooltip(new Tooltip("Open the file selection dialog to select a new file."));
        button_removeSelectedFiles.setTooltip(new Tooltip("Removes all files that are currently selected on the list."));
        button_clearAllFiles.setTooltip(new Tooltip("Clears the list of all files."));
        button_accept.setTooltip(new Tooltip("Accepts the Job settings and closes the dialog and creates a Job."));
        button_cancel.setTooltip(new Tooltip("Rejects the Job settings and closes the dialog without creating a Job."));

        comboBox_jobType.setTooltip(new Tooltip("Defines which type of Job to create."));

        // if possible, get default 'home' directory and set it as output default.
        try {
            File home = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory();
            textField_outputDirectory.setText(home.getCanonicalPath() + "/");
        } catch (Exception e) {
            // discard
        }
        textField_outputDirectory.setTooltip(new Tooltip("The directory in which to place the en/decoded file(s)."));
        button_selectOutputDirectory.setTooltip(new Tooltip("Open the directory selection dialog to select an output directory."));

        // Set Component EventHandlers:
        button_addFiles.setOnAction(controller);
        button_removeSelectedFiles.setOnAction(controller);
        button_clearAllFiles.setOnAction(controller);
        button_accept.setOnAction(controller);
        button_cancel.setOnAction(controller);
        comboBox_jobType.setOnAction(controller);
        button_selectOutputDirectory.setOnAction(controller);
        radioButton_singleArchive_yes.setOnAction(controller);
        radioButton_individualArchives_yes.setOnAction(controller);

        // Setup the Layout:
        final HBox panel_left_top = new HBox(10);
        panel_left_top.setAlignment(Pos.CENTER);
        panel_left_top.getChildren().addAll(button_addFiles, button_removeSelectedFiles, button_clearAllFiles);

        final HBox panel_left_bottom = new HBox(10);
        panel_left_bottom.setAlignment(Pos.CENTER);
        panel_left_bottom.getChildren().addAll(button_accept, button_cancel);

        final VBox panel_left = new VBox(4);
        HBox.setHgrow(panel_left, Priority.ALWAYS);
        VBox.setVgrow(listView_selectedFiles, Priority.ALWAYS);
        panel_left.getChildren().addAll(panel_left_top, listView_selectedFiles, panel_left_bottom);



        final HBox panel_right_top_top = new HBox(10);
        HBox.setHgrow(field_jobName, Priority.ALWAYS);
        panel_right_top_top.getChildren().addAll(comboBox_jobType, field_jobName);

        final VBox panel_right_top_bottom= new VBox(4);
        VBox.setVgrow(textArea_jobDescription, Priority.ALWAYS);
        VBox.setVgrow(panel_right_top_bottom, Priority.ALWAYS);
        panel_right_top_bottom.setAlignment(Pos.CENTER);
        panel_right_top_bottom.getChildren().addAll(textArea_jobDescription, label_job_estimatedDurationInMinutes);

        final VBox panel_right_top = new VBox(4);
        VBox.setVgrow(panel_right_top,Priority.ALWAYS);
        panel_right_top.getChildren().addAll(panel_right_top_top, panel_right_top_bottom);

        final HBox pane_panel_singleArchive = new HBox(10);
        pane_panel_singleArchive.setAlignment(Pos.CENTER);
        pane_panel_singleArchive.getChildren().addAll(radioButton_singleArchive_yes, radioButton_singleArchive_no);

        final TitledPane pane_singleArchive = new TitledPane();
        HBox.setHgrow(pane_singleArchive, Priority.ALWAYS);
        pane_singleArchive.setText("Pack File(s) into Single Archive");
        pane_singleArchive.setCollapsible(false);
        pane_singleArchive.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_singleArchive.setContent(pane_panel_singleArchive);

        final HBox pane_panel_individualArchives = new HBox(10);
        pane_panel_individualArchives.setAlignment(Pos.CENTER);
        pane_panel_individualArchives.getChildren().addAll(radioButton_individualArchives_yes, radioButton_individualArchives_no);

        final TitledPane pane_individualArchives = new TitledPane();
        HBox.setHgrow(pane_individualArchives, Priority.ALWAYS);
        pane_individualArchives.setText("Pack File(s) into Individual Archives");
        pane_individualArchives.setCollapsible(false);
        pane_individualArchives.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_individualArchives.setContent(pane_panel_individualArchives);

        final HBox panel_right_bottom_top = new HBox(10);
        panel_right_bottom_top.getChildren().addAll(pane_singleArchive, pane_individualArchives);

        final HBox panel_right_bottom_bottom = new HBox(10);
        HBox.setHgrow(textField_outputDirectory, Priority.ALWAYS);
        panel_right_bottom_bottom.getChildren().addAll(textField_outputDirectory, button_selectOutputDirectory);

        final VBox panel_right_bottom = new VBox(4);
        panel_right_bottom.getChildren().addAll(panel_right_bottom_top, panel_right_bottom_bottom);

        final VBox panel_right = new VBox(4);
        HBox.setHgrow(panel_right, Priority.ALWAYS);
        VBox.setVgrow(textArea_jobDescription, Priority.ALWAYS);
        panel_right.getChildren().addAll(panel_right_top, panel_right_bottom);



        this.setSpacing(4);
        this.getChildren().addAll(panel_left, panel_right);
    }

    ////////////////////////////////////////////////////////// Getters

    // todo JavaDoc
    public ListView<String> getListView_selectedFiles() {
        return listView_selectedFiles;
    }

    /** @return The button to open the handler selection dialog. */
    public Button getButton_addFiles() {
        return button_addFiles;
    }

    /** @return The button to remove all files that are currently selected on the scrollpane_selectedFiles. */
    public Button getButton_removeSelectedFiles() {
        return button_removeSelectedFiles;
    }

    /** @return The button to remove all files from the list. */
    public Button getButton_clearAllFiles() {
        return button_clearAllFiles;
    }

    /** @return The label to display an estimate of how long the Job will take to run. */
    public Label getLabel_job_estimatedDurationInMinutes() {
        return label_job_estimatedDurationInMinutes;
    }

    /** @return Whether or not the Job is an Encode Job. If not, then it's a Decode Job. */
    public boolean getIsEncodeJob() {
        return comboBox_jobType.getSelectionModel().getSelectedItem().equals("Encode");
    }

    /** @return The button to close the JobCreationDialog while creating a Job. */
    public Button getButton_accept() {
        return button_accept;
    }

    /** @return The button to close the JobCreationDialog without creating a Job. */
    public Button getButton_cancel() {
        return button_cancel;
    }

    // todo JavaDoc
    public TextField getField_jobName() {
        return field_jobName;
    }

    // todo JavaDoc
    public TextArea getTextArea_jobDescription() {
        return textArea_jobDescription;
    }

    /** @return The radio button that says that each of the currently selected files should be archived as a single archive before encoding. */
    public RadioButton getRadioButton_singleArchive_yes() {
        return radioButton_singleArchive_yes;
    }

    /** @return The radio button that says that each of the currently selected files should be archived individually before encoding them individually. */
    public RadioButton getRadioButton_singleArchive_no() {
        return radioButton_singleArchive_no;
    }

    /** @return The radio button that says that each handler should be archived individually before encoding each of them individually. */
    public RadioButton getRadioButton_individualArchives_yes() {
        return radioButton_individualArchives_yes;
    }

    /** @return The radio button that says that each handler should not be archived individually before encoding each of them individually. */
    public RadioButton getRadioButton_individualArchives_no() {
        return radioButton_individualArchives_no;
    }

    /** @return The text field for the path to the directory in which to place the en/decoded file(s). */
    public TextField getTextField_outputDirectory() {
        return textField_outputDirectory;
    }

    /** @return The button to select the folder to output the archive to if the "Yes" radio button is selected. */
    public Button getButton_selectOutputDirectory() {
        return button_selectOutputDirectory;
    }
}
