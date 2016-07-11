package controller.settings;

import handler.ConfigHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import view.settings.MiscSettingsPane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MiscSettingsController implements EventHandler {
    // todo JavaDoc
    @Getter private final MiscSettingsPane pane;

    // todo JavaDoc
    private final Stage settingsStage;

    /** The object that handles settings for encoding, decoding, compression, and a number of other features. */
    private final ConfigHandler configHandler;

    public MiscSettingsController(final Stage settingsStage, final ConfigHandler configHandler) {
        this.settingsStage = settingsStage;
        this.configHandler = configHandler;
        pane = new MiscSettingsPane(settingsStage, this, configHandler);
    }

    @Override
    public void handle(Event event) {
        final Object source = event.getSource();

        if(source.equals(pane.getButton_selectFile_splashScreenFilePath())) {
            final FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Splashscreen Image Selection");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.bmp", "*.wbmp"));
            final File selectedFile = fileChooser.showOpenDialog(settingsStage);

            if(selectedFile != null) {
                pane.getField_splashScreenFilePath().setText(selectedFile.getAbsolutePath());
            }
        }
    }

    /**
     * Resets the error state of all components that are checked.
     *
     * Checks over a number of the values entered in the view to see if they fit
     * within a set of accepted parameters.
     *
     * If any component contains a value that is unacceptable, then it's error
     * state it set.
     * @return Whether or not the settings are correct.
     */
    public boolean areSettingsCorrect() {
        boolean wasErrorFound = false;

        // Reset the error states and tooltips of any components
        // that can have an error state set.
        // Set the password fields back to their normal look:
        pane.getField_splashScreenFilePath().getStylesheets().remove("field_error.css");
        pane.getField_splashScreenFilePath().getStylesheets().add("global.css");
        pane.getField_splashScreenFilePath().setTooltip(new Tooltip("The amount of time, in milliseconds, to display the splash screen.</br></br>1000 = 1 second"));

        pane.getField_splashScreenDisplayTime().getStylesheets().remove("field_error.css");
        pane.getField_splashScreenDisplayTime().getStylesheets().add("global.css");
        pane.getField_splashScreenDisplayTime().setTooltip(new Tooltip("The absolute path to the splash screen to display."));

        // Check to see if the splash screen's file path actually leads to a file.
        // Only check if the splash screen is actually enabled.
        if(pane.getShowSplashScreen()) {
            final File file = new File(pane.getField_splashScreenFilePath().getText());

            if(pane.getField_splashScreenFilePath().getText().isEmpty() ||  Files.exists(Paths.get(file.toURI())) == false) {
                pane.getField_splashScreenFilePath().getStylesheets().remove("global.css");
                pane.getField_splashScreenFilePath().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_splashScreenFilePath().getTooltip();
                final String errorText = "Error - You need to enter a path to an existing image file.";
                pane.getField_splashScreenFilePath().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

                wasErrorFound = true;
            }
        }

        // Check to see if the splash screen's display time is actually set
        // as an integer.
        try {
            int temp = Integer.valueOf(pane.getField_splashScreenDisplayTime().getText());

            if(temp < 1) {
                throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
            }
        } catch(final NumberFormatException e) {
            pane.getField_splashScreenDisplayTime().getStylesheets().remove("global.css");
            pane.getField_splashScreenDisplayTime().getStylesheets().add("field_error.css");

            final Tooltip currentTooltip = pane.getField_splashScreenDisplayTime().getTooltip();
            final String errorText = "Error - There is no integer entered here. Please enter an integer of 1 or greater.";
            pane.getField_splashScreenDisplayTime().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

            wasErrorFound = true;
        }

        return wasErrorFound;
    }
}
