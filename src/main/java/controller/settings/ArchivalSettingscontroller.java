package controller.settings;

import handler.ConfigHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.settings.ArchivalSettingsPane;

import java.io.File;

public class ArchivalSettingsController implements EventHandler {
    // todo JavaDoc
    private final ArchivalSettingsPane pane;

    // todo JavaDoc
    private final Stage settingsStage;

    /** The object that handles settings for encoding, decoding, compression, and a number of other features. */
    private final ConfigHandler configHandler;

    public ArchivalSettingsController(final Stage settingsStage, final ConfigHandler configHandler) {
        this.settingsStage = settingsStage;
        this.configHandler = configHandler;
        pane = new ArchivalSettingsPane(settingsStage, this, configHandler);
    }

    @Override
    public void handle(Event event) {
        final Object source = event.getSource();

        if(source.equals(pane.getButton_selectFile_compressionProgramPath())) {
            final FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("7zip Executable Selection");
            final File selectedFile = fileChooser.showOpenDialog(settingsStage);

            if(selectedFile != null) {
                pane.getField_compressionProgramPath().setText(selectedFile.getAbsolutePath());
            }
        }
    }

    public ArchivalSettingsPane getPane() {
        return pane;
    }
}
