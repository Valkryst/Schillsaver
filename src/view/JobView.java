package view;

import com.valkryst.VMVC.Settings;
import com.valkryst.VMVC.view.View;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.Getter;

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
     * @param settings
     *          The program settings.
     */
    public JobView(final Settings settings) {
        initializeComponents(settings);
        setComponentTooltips();

        final Pane fileSelectionArea = createFileSelectionArea();
        final Pane fileDetailsArea = createJobDetailsArea();
        final Pane bottomMenuBar = createBottomMenuBar();

        final VBox vBox = new VBox();
        vBox.getChildren().addAll(fileSelectionArea, fileDetailsArea);

        super.pane = new VBox();
        super.pane.setMinSize(512, 512);
        super.pane.getChildren().addAll(vBox, bottomMenuBar);
    }

    /**
     * Initializes the components.
     *
     * @param settings
     *          The program settings.
     */
    private void initializeComponents(final Settings settings) {
        button_addFiles = new Button("Add Files");
        button_removeSelectedFiles = new Button("Remove Selected Files");
        button_selectOutputFolder = new Button("Select Output Folder");
        button_accept = createIconButton("icons/Accept.png", 16, 16);
        button_cancel = createIconButton("icons/Cancel.png", 16, 16);

        textField_jobName = new TextField();
        textField_jobName.setPromptText("Job Name");

        textField_outputFolder = new TextField();
        textField_outputFolder.setPromptText("Output Folder Path");

        fileList = new ListView<>();
        fileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        comboBox_jobType  = new ComboBox<>(FXCollections.observableArrayList("Encode", "Decode"));
        comboBox_jobType.getSelectionModel().select("Encode");
        textField_outputFolder.setText(settings.getStringSetting("Default Encoding Output Directory"));
        comboBox_jobType.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            boolean canChange = textField_outputFolder.getText().isEmpty();
            canChange |= textField_outputFolder.getText().equals(settings.getStringSetting("Default Encoding Output Directory"));
            canChange |= textField_outputFolder.getText().equals(settings.getStringSetting("Default Decoding Output Directory"));

            if (canChange == false) {
                return;
            }

            if (newValue.equals("Encode")) {
                textField_outputFolder.setText(settings.getStringSetting("Default Encoding Output Directory"));
            } else {
                textField_outputFolder.setText(settings.getStringSetting("Default Decoding Output Directory"));
            }
        });
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

        setTooltip(comboBox_jobType, "The type of the job.");
    }

    /**
     * Creates the file selection area.
     *
     * @return
     *         The file selection area.
     */
    private Pane createFileSelectionArea() {
        final GridPane buttonPane = createHorizontalGridPane(button_addFiles, button_removeSelectedFiles);

        // Create the Pane:
        final VBox pane = new VBox();
        pane.getChildren().addAll(buttonPane, fileList);

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
        final HBox typeNamePane = new HBox();
        typeNamePane.getChildren().addAll(comboBox_jobType, textField_jobName);

        HBox.setHgrow(textField_jobName, Priority.ALWAYS);

        // Add output folder field/button side-by-side:
        final HBox outputPane = new HBox();
        outputPane.getChildren().addAll(textField_outputFolder, button_selectOutputFolder);

        HBox.setHgrow(textField_outputFolder, Priority.ALWAYS);

        // Add panes to a VBox
        final VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 0, 10, 0));
        vBox.getChildren().addAll(typeNamePane, outputPane);

        HBox.setHgrow(vBox, Priority.ALWAYS);
        VBox.setVgrow(vBox, Priority.ALWAYS);

        return vBox;
    }

    /**
     * Creates the bottom menu bar.
     *
     * @return
     *         The bottom menu bar.
     */
    private Pane createBottomMenuBar() {
        return createHorizontalGridPane(button_accept, button_cancel);
    }
}
