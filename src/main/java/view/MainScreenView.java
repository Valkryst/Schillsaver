package view;

import controller.MainScreenController;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class MainScreenView extends HBox {

    // todo JavaDoc
    @Getter private ListView<String> listView_jobs = new ListView<>();

    /** The button to open the Job creation dialog. */
    @Getter private final Button button_createJob = new Button("Create Job");
    /** The button to open the first of the currently selected jobs. */
    @Getter private final Button button_editJob = new Button("Edit Job");
    /** The button to remove all Jobs that are currently selected on the listView_jobs list.. */
    @Getter private final Button button_deleteSelectedJobs = new Button("Remove Selected Job(s)");
    /** The button to delete all Jobs from the list. */
    @Getter private final Button button_deleteAllJobs = new Button("Delete All Jobs");

    /** The text area for the ouput of FFMPEG from the most recent encode or decode job. */
    @Getter private TextArea textArea_output = new TextArea();

    /** The button to clear the output screen. */
    @Getter private final Button button_clearOutput = new Button("Clear Output");

    /** The button to open the settings dialog. */
    @Getter private final Button button_editSettings = new Button("Edit Settings");

    /** The button to encode the currently selected handler(s). */
    @Getter private final Button button_encode = new Button("Encode");
    /** The button to decode the currently selected handler(s). */
    @Getter private final Button button_decode = new Button("Decode");

    public MainScreenView(final MainScreenController controller) {
        // Setup Job  List:
        listView_jobs.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Setup output area:
        textArea_output.setEditable(false);
        textArea_output.setFocusTraversable(false);

        setTooltips();
        setEventHandlers(controller);

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

    /** Sets the default tooltips for all relevant components. */
    private void setTooltips() {
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
    }

    /**
     * Sets the EventHandler for all relevant components.
     *
     * @param controller
     *         The EventHandler to use.
     */
    private void setEventHandlers(final MainScreenController controller) {
        button_createJob.setOnAction(controller);
        button_editJob.setOnAction(controller);
        button_encode.setOnAction(controller);
        button_decode.setOnAction(controller);
        button_deleteSelectedJobs.setOnAction(controller);
        button_deleteAllJobs.setOnAction(controller);
        button_clearOutput.setOnAction(controller);
        button_editSettings.setOnAction(controller);
    }
}
