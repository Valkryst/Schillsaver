package view;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import lombok.Getter;

public class JobView extends View {
    @Getter private Button button_addFiles;
    @Getter private Button button_removeSelectedFiles;
    @Getter private Button button_selectOutputFolder;
    @Getter private Button button_accept;
    @Getter private Button button_cancel;

    @Getter private TextField textField_jobName;
    @Getter private TextField textField_outputFolder;

    @Getter private Label label_timeEstimate;

    @Getter private ComboBox<String> comboBox_jobType;

    @Getter private ToggleGroup toggleGroup_singleArchive;
    @Getter private RadioButton radioButton_singleArchive_yes;
    @Getter private RadioButton radioButton_singleArchive_no;

    public JobView() {
        initializeComponents();
    }

    /** Initializes the components. */
    private void initializeComponents() {
        button_addFiles = new Button("Add Files");
        button_removeSelectedFiles = new Button("Remove Selected File(s)");
        button_selectOutputFolder = new Button("Select Output Folder");
        button_accept = new Button("Accept");
        button_cancel = new Button("Cancel");

        textField_jobName = new TextField();
        textField_jobName.setPromptText("Job Name");

        textField_outputFolder = new TextField();
        textField_outputFolder.setPromptText("Output Folder Path");

        label_timeEstimate = new Label("Unknown");

        comboBox_jobType  = new ComboBox<>(FXCollections.observableArrayList("Encode", "Decode"));

        toggleGroup_singleArchive = new ToggleGroup();
        radioButton_singleArchive_yes = new RadioButton("Yes");
        radioButton_singleArchive_no = new RadioButton("No");
    }

    /** Sets the tooltips of the components. */
    private void setComponentTooltips() {
        setTooltip(button_addFiles, "Opens a file selection dialog to select files to add to the job.");
        setTooltip(button_removeSelectedFiles, "Removes all selected files from the list.");
        setTooltip(button_selectOutputFolder, "Opens a folder selection dialog to select the output folder of the job.");
        setTooltip(button_accept, "Accepts and creates the job, then returns to the main screen.");
        setTooltip(button_cancel, "Cancels job creation and returns to the main screen.");

        setTooltip(textField_jobName, "The name of the job. This must be unique.");

        setTooltip(textField_outputFolder, "The path of the output folder.");

        setTooltip(label_timeEstimate, "An estimate of how long the job will take to run.");

        setTooltip(comboBox_jobType, "The type of the job.");

        setTooltip(radioButton_singleArchive_yes, "Combine all of the job's files into a single archive when processing the job.");
        setTooltip(radioButton_singleArchive_no, "Process each of the job's files individually when processing the job.");
    }
}
