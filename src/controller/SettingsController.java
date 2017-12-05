package controller;

import configuration.Settings;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import view.SettingsView;
import view.settings.ArchivalSettingsPane;
import view.settings.FfmpegSettingsPane;
import view.settings.MiscSettingsPane;

public class SettingsController extends Stage implements EventHandler {
    // todo JavaDoc
    @Getter private final SettingsView view;

    /**
     * The object that handles settings for encoding, decoding, compression, and a number of other features.
     */
    private final Settings settings;

    /**
     * Construct a new settings dialog controller.
     * @param settings The object that handles settings for encoding, decoding, compression, and a number of other features.
     */
    public SettingsController(final Stage settingsStage, final Settings settings) {
        this.settings = settings;

        view = new SettingsView(settingsStage, this, this.settings);

        // Setup Stage:
        final Scene scene = new Scene(view);
        scene.getStylesheets().add("global.css");
        scene.getRoot().getStyleClass().add("main-root");

        this.setTitle("Settings Manager");
        this.initModality(Modality.APPLICATION_MODAL);
        this.getIcons().add(new Image("icon.png"));
        this.setResizable(false);
        this.setScene(scene);
    }

    @Override
    public void handle(Event event) {
        final Object source = event.getSource();

        // The button to close the window and save settings.
        if (source.equals(view.getButton_accept())) {
            // Check to see if all data is correct.
            // If it is, then save the settings.
            if (! view.getController_ffmpegSettings().areSettingsCorrect()) {
                // Show any warnings about YouTube compatability:
                if (settings.getBooleanSetting("Warn If Settings Possibly Incompatible With YouTube")) {
                    view.getController_ffmpegSettings().displayWarningsAboutYouTubeCompatability();
                }

                final ArchivalSettingsPane pane_archival = view.getController_archivalSettings().getPane();
                final FfmpegSettingsPane pane_ffmpeg = view.getController_ffmpegSettings().getPane();
                final MiscSettingsPane pane_misc = view.getPane_miscSettings();

                settings.setSetting("FFMPEG Path", pane_ffmpeg.getField_ffmpegPath().getText());
                settings.setSetting("Compression Program Path", pane_archival.getField_compressionProgramPath().getText());

                settings.setSetting("Enc Format", pane_ffmpeg.getField_encodeFormat().getText());
                settings.setSetting("Dec Format", pane_ffmpeg.getField_decodeFormat().getText());

                settings.setSetting("Enc Vid Width", pane_ffmpeg.getField_encodedVideoWidth().getText());
                settings.setSetting("Enc Vid Height", pane_ffmpeg.getField_encodedVideoHeight().getText());
                settings.setSetting("Enc Vid Framerate", pane_ffmpeg.getField_encodedFramerate().getText());
                settings.setSetting("Enc Vid Macro Block Dimensions", pane_ffmpeg.getField_macroBlockDimensions().getText());
                settings.setSetting("Enc Library", pane_ffmpeg.getField_encodingLibrary().getText());

                settings.setSetting("FFMPEG Log Level", pane_ffmpeg.getComboBox_ffmpegLogLevel().getSelectionModel().getSelectedItem());

                settings.setSetting("Use Custom FFMPEG Options", pane_ffmpeg.getRadioButton_useFullyCustomEncodingOptions_yes().isSelected());
                settings.setSetting("Custom FFMPEG Enc Options", pane_ffmpeg.getField_fullyCustomFfmpegEncodingOptions().getText());
                settings.setSetting("Custom FFMPEG Dec Options", pane_ffmpeg.getField_fullyCustomFfmpegDecodingptions().getText());

                settings.setSetting("Delete Source File When Enc", false);
                settings.setSetting("Delete Source File When Dec", false);

                settings.setSetting("Compression Commands", pane_archival.getField_compressionCommands().getText());
                settings.setSetting("Compression Output Extension", pane_archival.getField_archiveOutputExtension().getText());

                settings.setSetting("Warn If Settings Possibly Incompatible With YouTube", pane_misc.getWarnUserIfSettingsMayNotWorkForYouTube());

                this.close();
            }
        }

        // The button to close the window without saving settings.
        if(source.equals(view.getButton_cancel())) {
            this.close();
        }
    }
}
