package view;

import controller.MainScreenController;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainScreenView extends HBox {

    // todo JavaDoc
    private ListView<String> listView_jobs = new ListView<>();

    /** The button to open the Job creation dialog. */
    private final Button button_createJob = new Button("Create Job");
    /** The button to open the first of the currently selected jobs. */
    private final Button button_editJob = new Button("Edit Job");
    /** The button to remove all Jobs that are currently selected on the listView_jobs list.. */
    private final Button button_deleteSelectedJobs = new Button("Remove Selected Job(s)");
    /** The button to delete all Jobs from the list. */
    private final Button button_deleteAllJobs = new Button("Delete All Jobs");

    /** The text area for the ouput of FFMPEG from the most recent encode or decode job. */
    private TextArea textArea_output = new TextArea();

    /** The button to clear the output screen. */
    private final Button button_clearOutput = new Button("Clear Output");

    /** The button to open the settings dialog. */
    private final Button button_editSettings = new Button("Edit Settings");

    /** The button to encode the currently selected handler(s). */
    private final Button button_encode = new Button("Encode");
    /** The button to decode the currently selected handler(s). */
    private final Button button_decode = new Button("Decode");

    public MainScreenView(final MainScreenController controller) {
        // Setup Job  List:
        listView_jobs.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Setup output area:
        textArea_output.setEditable(false);
        textArea_output.setFocusTraversable(false);

        // Set Component Tooltips:
        button_createJob.setTooltip(new Tooltip("Open the Job creation dialog to create a new Job."));
        button_editJob.setTooltip(new Tooltip("Edit the first of the currently selected Jobs."));
        button_deleteSelectedJobs.setTooltip(new Tooltip("Removes all Jobs that are currently selected on the list."));
        button_deleteAllJobs.setTooltip(new Tooltip("Clears the list of all Jobs."));
        button_clearOutput.setTooltip(new Tooltip("Clears the output screen."));
        button_editSettings.setTooltip(new Tooltip("Open the settings menu."));
        button_encode.setTooltip(new Tooltip("Encodes the selected handler(s)."));
        button_decode.setTooltip(new Tooltip("Decodes the selected handler(s).\n\n" +
                                             "No checking is done to see if the files have ever been encoded,\n" +
                                             "so it's up to you to ensure you're decoding the correct files."));

        // Set Component EventHandlers:
        button_createJob.setOnAction(controller);
        button_editJob.setOnAction(controller);
        button_encode.setOnAction(controller);
        button_decode.setOnAction(controller);
        button_deleteSelectedJobs.setOnAction(controller);
        button_deleteAllJobs.setOnAction(controller);
        button_clearOutput.setOnAction(controller);
        button_editSettings.setOnAction(controller);

        // Setup the Layout:
        final HBox panel_left_top = new HBox(10);
        panel_left_top.setAlignment(Pos.CENTER);
        panel_left_top.getChildren().addAll(button_createJob, button_editJob, button_deleteSelectedJobs, button_deleteAllJobs);

        final HBox panel_left_bottom = new HBox(10);
        panel_left_bottom.setAlignment(Pos.CENTER);
        panel_left_bottom.getChildren().addAll(button_encode, button_decode);

        final VBox panel_left = new VBox(4);
        HBox.setHgrow(panel_left, Priority.ALWAYS);
        VBox.setVgrow(listView_jobs, Priority.ALWAYS);
        panel_left.getChildren().addAll(panel_left_top, listView_jobs, panel_left_bottom);



        final BorderPane panel_right_bottom = new BorderPane();
        panel_right_bottom.setLeft(button_clearOutput);
        panel_right_bottom.setRight(button_editSettings);

        final VBox panel_right = new VBox(4);
        HBox.setHgrow(panel_right, Priority.ALWAYS);
        VBox.setVgrow(textArea_output, Priority.ALWAYS);
        panel_right.getChildren().addAll(textArea_output, panel_right_bottom);



        this.setSpacing(4);
        this.getChildren().addAll(panel_left, panel_right);
    }

    ////////////////////////////////////////////////////////// Getters

    // todo JavaDoc
    public ListView<String> getListView_jobs() {
        return listView_jobs;
    }

    /** @return The text area for the ouput of FFMPEG from the most recent encode or decode job. */
    public TextArea getTextArea_output() {
        return textArea_output;
    }

    /** @return The button to open the handler selection dialog. */
    public Button getButton_createJob() {
        return button_createJob;
    }

    /** @return The button to open the first of the currently selected jobs. */
    public Button getButton_editJob() {
        return button_editJob;
    }

    /** @return The button to encode the currently selected handler(s). */
    public Button getButton_encode() {
        return button_encode;
    }

    /** @return The button to decode the currently selected handler(s). */
    public Button getButton_decode() {
        return button_decode;
    }

    /** @return The button to remove all files that are currently selected on the scrollpane_selectedFiles. */
    public Button getButton_deleteSelectedJobs() {
        return button_deleteSelectedJobs;
    }

    /** @return The button to remove all files from the list. */
    public Button getButton_deleteAllJobs() {
        return button_deleteAllJobs;
    }

    /** @return The button to clearn the output screen. */
    public Button getButton_clearOutput() {
        return button_clearOutput;
    }

    /** @return The button to open the settings dialog. */
    public Button getButton_editSettings() {
        return button_editSettings;
    }
}
