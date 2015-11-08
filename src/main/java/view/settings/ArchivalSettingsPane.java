package view.settings;

import controller.settings.ArchivalSettingsController;
import handler.ConfigHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ArchivalSettingsPane extends TitledPane {
    /** The name of the Tab. */
    private static final String TAB_NAME = "Archival";

    /** The text field for the absolute path to the 7zip, or whichever compresssion program the user wants to use, executable. */
    private final TextField field_compressionProgramPath = new TextField();
    /** The button to open the file selection dialog for the 7zip, or whichever compression program the user wants to use, executable. */
    private final Button button_selectFile_compressionProgramPath = new Button("Select File");

    /** The text field for the base commands to use when compressing a file before encoding. */
    private final TextField field_compressionCommands = new TextField();

    public ArchivalSettingsPane(final Stage settingsStage, final ArchivalSettingsController controller, final ConfigHandler configHandler) {
        // Set Field Prompt Text:
        field_compressionProgramPath.setPromptText("compressionProgramPath");
        field_compressionCommands.setPromptText("compressionCommands");

        // Set Default Values:
        field_compressionProgramPath.setText(configHandler.getCompressionProgramPath());
        field_compressionCommands.setText(configHandler.getCompressionCommands());

        // Set Component Tooltips:
        field_compressionProgramPath.setTooltip(new Tooltip("The absolute path to 7zip/7zip.exe or whichever compression program is specified."));
        button_selectFile_compressionProgramPath.setTooltip(new Tooltip("Opens the file selection dialog to locate a compression program executable."));

        field_compressionCommands.setTooltip(new Tooltip("The base commands to use when compressing a file before encoding."));

        // Set Component Listeners:
        button_selectFile_compressionProgramPath.setOnAction(controller);

        // Setup the Layout:
        final HBox panel_top = new HBox(10);
        HBox.setHgrow(field_compressionProgramPath, Priority.ALWAYS);
        panel_top.getChildren().addAll(field_compressionProgramPath, button_selectFile_compressionProgramPath);


        final VBox panel = new VBox(4);
        HBox.setHgrow(field_compressionCommands, Priority.ALWAYS);
        panel.getChildren().addAll(panel_top, field_compressionCommands);


        this.setText(TAB_NAME);
        this.setCollapsible(false);
        this.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        this.setContent(panel);
    }

    ////////////////////////////////////////////////////////// Getters

    /** @return The text field for the absolute path to the 7zip, or whichever compresssion program the user wants to use, executable. */
    public TextField getField_compressionProgramPath() {
        return field_compressionProgramPath;
    }

    /** @return The button to open the file selection dialog for the 7zip, or whichever compression program the user wants to use, executable. */
    public Button getButton_selectFile_compressionProgramPath() {
        return button_selectFile_compressionProgramPath;
    }

    /** @return The text field for the base commands to use when compressing a file before encoding. */
    public TextField getField_compressionCommands() {
        return getField_compressionProgramPath();
    }
}
