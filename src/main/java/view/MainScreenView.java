package view;

import controller.MainScreenController;
import javafx.scene.control.*;
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

    /** The text area for the output of FFMPEG from the most recent encode or decode job. */
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
        this.getChildren().addAll(setupLeftPanel(), setupRightPanel());
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

    /**
     * Constructs the left panel.
     *
     * @return
     *         The left panel.
     */
    private VBox setupLeftPanel() {
        final HBox top = new HBox();
        final HBox bottom = new HBox();
        final VBox panel = new VBox();

        HBox.setHgrow(panel, Priority.ALWAYS);
        VBox.setVgrow(listView_jobs, Priority.ALWAYS);

        // Bottom - Set buttons to fill all available space:
        HBox.setHgrow(button_encode, Priority.ALWAYS);
        HBox.setHgrow(button_decode, Priority.ALWAYS);

        top.getChildren().addAll(button_createJob, button_editJob, button_deleteSelectedJobs, button_deleteAllJobs);
        bottom.getChildren().addAll(button_encode, button_decode);
        panel.getChildren().addAll(top, listView_jobs, bottom);

        return panel;
    }

    /**
     * Constructs the right panel.
     *
     * @return
     *         The right panel.
     */
    private VBox setupRightPanel() {
        final HBox bottom = new HBox();
        final VBox panel = new VBox();

        HBox.setHgrow(panel, Priority.ALWAYS);
        VBox.setVgrow(textArea_output, Priority.ALWAYS);

        // Bottom - Set buttons to fill all available space:
        HBox.setHgrow(button_clearOutput, Priority.ALWAYS);
        HBox.setHgrow(button_editSettings, Priority.ALWAYS);

        bottom.getChildren().addAll(button_clearOutput, button_editSettings);
        panel.getChildren().addAll(textArea_output, bottom);

        return panel;
    }
}
