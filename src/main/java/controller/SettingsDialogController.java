package controller;

import handler.ConfigHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import view.SettingsDialogView;
import view.settings.ArchivalSettingsPane;
import view.settings.FfmpegSettingsPane;
import view.settings.MiscSettingsPane;

public class SettingsDialogController extends Stage implements EventHandler {
    // todo JavaDoc
    @Getter private final SettingsDialogView view;

    /**
     * The object that handles settings for encoding, decoding, compression, and a number of other features.
     */
    private final ConfigHandler configHandler;

    /**
     * Construct a new settings dialog controller.
     * @param configHandler The object that handles settings for encoding, decoding, compression, and a number of other features.
     */
    public SettingsDialogController(final ConfigHandler configHandler) {
        this.configHandler = configHandler;

        view = new SettingsDialogView(this, configHandler);

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
        if(source.equals(view.getButton_accept())) {
            // Check to see if all data is correct.
            // If it is, then save the settings.
            if(! view.getController_ffmpegSettings().areSettingsCorrect() && !view.getController_miscSettings().areSettingsCorrect()) {
                // Show any warnings about YouTube compatability:
                if(configHandler.isWarnUserIfSettingsMayNotWorkForYouTube()) {
                    view.getController_ffmpegSettings().displayWarningsAboutYouTubeCompatability();
                }

                final ArchivalSettingsPane pane_archival = view.getController_archivalSettings().getPane();
                final FfmpegSettingsPane pane_ffmpeg = view.getController_ffmpegSettings().getPane();
                final MiscSettingsPane pane_misc = view.getController_miscSettings().getPane();

                configHandler.setFfmpegPath(pane_ffmpeg.getField_ffmpegPath().getText());
                configHandler.setCompressionProgramPath(pane_archival.getField_compressionProgramPath().getText());
                configHandler.setEncodeFormat(pane_ffmpeg.getField_encodeFormat().getText());
                configHandler.setDecodeFormat(pane_ffmpeg.getField_decodeFormat().getText());
                configHandler.setEncodedVideoWidth(Integer.valueOf(pane_ffmpeg.getField_encodedVideoWidth().getText()));
                configHandler.setEncodedVideoHeight(Integer.valueOf(pane_ffmpeg.getField_encodedVideoHeight().getText()));
                configHandler.setEncodedFramerate(Integer.valueOf(pane_ffmpeg.getField_encodedFramerate().getText()));
                configHandler.setMacroBlockDimensions(Integer.valueOf(pane_ffmpeg.getField_macroBlockDimensions().getText()));
                configHandler.setUseFullyCustomFfmpegOptions(pane_ffmpeg.getRadioButton_useFullyCustomEncodingOptions_yes().isSelected());
                configHandler.setFullyCustomFfmpegEncodingOptions(pane_ffmpeg.getField_fullyCustomFfmpegEncodingOptions().getText());
                configHandler.setFullyCustomFfmpegDecodingOptions(pane_ffmpeg.getField_fullyCustomFfmpegDecodingptions().getText());
                configHandler.setEncodingLibrary(pane_ffmpeg.getField_encodingLibrary().getText());
                configHandler.setFfmpegLogLevel(pane_ffmpeg.getComboBox_ffmpegLogLevel().getSelectionModel().getSelectedItem());
                configHandler.setDeleteSourceFileWhenEncoding(pane_misc.getDeleteSourceFileWhenEncoding());
                configHandler.setDeleteSourceFileWhenDecoding(pane_misc.getDeleteSourceFileWhenDecoding());
                configHandler.setShowSplashScreen(pane_misc.getShowSplashScreen());
                configHandler.setSplashScreenFilePath(pane_misc.getField_splashScreenFilePath().getText());
                configHandler.setSplashScreenDisplayTime(Integer.valueOf(pane_misc.getField_splashScreenDisplayTime().getText()));
                configHandler.setCompressionCommands(pane_archival.getField_compressionCommands().getText());
                configHandler.setCheckForUpdates(pane_misc.getCheckForUpdatesOnStart());
                configHandler.createConfigFile();

                this.close();
            }
        }

        // The button to close the window without saving settings.
        if(source.equals(view.getButton_cancel())) {
            this.close();
        }
    }
}
