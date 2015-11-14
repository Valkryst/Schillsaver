package controller.settings;

import handler.ConfigHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.settings.FfmpegSettingsPane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FfmpegSettingsController implements EventHandler {
    // todo JavaDoc
    private final FfmpegSettingsPane pane;

    // todo JavaDoc
    private final Stage settingsStage;

    /** The object that handles settings for encoding, decoding, compression, and a number of other features. */
    private final ConfigHandler configHandler;

    public FfmpegSettingsController(final Stage settingsStage, final ConfigHandler configHandler) {
        this.settingsStage = settingsStage;
        this.configHandler = configHandler;
        pane = new FfmpegSettingsPane(settingsStage, this, configHandler);
    }

    @Override
    public void handle(Event event) {
        final Object source = event.getSource();

        if(source.equals(pane.getButton_selectFile_ffmpegPath())) {
            final FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("FFMPEG Executable Selection");
            final File selectedFile = fileChooser.showOpenDialog(settingsStage);

            if(selectedFile != null) {
                pane.getField_ffmpegPath().setText(selectedFile.getAbsolutePath());
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
        pane.getField_ffmpegPath().getStylesheets().remove("field_error.css");
        pane.getField_ffmpegPath().getStylesheets().add("global.css");
        pane.getField_ffmpegPath().setTooltip(new Tooltip("The absolute path to ffmpeg/ffmpeg.exe."));

        pane.getField_encodeFormat().getStylesheets().remove("field_error.css");
        pane.getField_encodeFormat().getStylesheets().add("global.css");
        pane.getField_encodeFormat().setTooltip(new Tooltip("The format to encode to."));

        pane.getField_decodeFormat().getStylesheets().remove("field_error.css");
        pane.getField_decodeFormat().getStylesheets().add("global.css");
        pane.getField_decodeFormat().setTooltip(new Tooltip("The format to decode to.\n\nThis should be the handler that your archival program archives to.\nWith 7zip, this should be set to 7z."));

        pane.getField_encodedVideoWidth().getStylesheets().remove("field_error.css");
        pane.getField_encodedVideoWidth().getStylesheets().add("global.css");
        pane.getField_encodedVideoWidth().setTooltip(new Tooltip("The width, in pixels, of the encoded video."));

        pane.getField_encodedVideoHeight().getStylesheets().remove("field_error.css");
        pane.getField_encodedVideoHeight().getStylesheets().add("global.css");
        pane.getField_encodedVideoHeight().setTooltip(new Tooltip("The height, in pixels, of the encoded video."));

        pane.getField_encodedFramerate().getStylesheets().remove("field_error.css");
        pane.getField_encodedFramerate().getStylesheets().add("global.css");
        pane.getField_encodedFramerate().setTooltip(new Tooltip("The framerate of the encoded video. Ex ~ 30, 60, etc..."));

        pane.getField_macroBlockDimensions().getStylesheets().remove("field_error.css");
        pane.getField_macroBlockDimensions().getStylesheets().add("global.css");
        pane.getField_macroBlockDimensions().setTooltip(new Tooltip("The width/height of each encoded macroblock."));

        pane.getField_encodingLibrary().getStylesheets().remove("field_error.css");
        pane.getField_encodingLibrary().getStylesheets().add("global.css");
        pane.getField_encodingLibrary().setTooltip(new Tooltip("The library to encode the video with."));

        pane.getField_fullyCustomFfmpegEncodingOptions().getStylesheets().remove("field_error.css");
        pane.getField_fullyCustomFfmpegEncodingOptions().getStylesheets().add("global.css");
        pane.getField_fullyCustomFfmpegEncodingOptions().setTooltip(new Tooltip("The commands to use when encoding a file with ffmpeg.\n\n" +
                                                                               "If the fully-custom options are enabled, then all other ffmpeg options are ignored\n" +
                                                                               "and this string will be used as the only argument to ffmpeg when encoding."));

        pane.getField_fullyCustomFfmpegDecodingptions().getStylesheets().remove("field_error.css");
        pane.getField_fullyCustomFfmpegDecodingptions().getStylesheets().add("global.css");
        pane.getField_fullyCustomFfmpegDecodingptions().setTooltip(new Tooltip("The commands to use when encoding a file with ffmpeg.\n\n" +
                                                                              "If the fully-custom options are enabled, then all other ffmpeg options are ignored\n" +
                                                                              "and this string will be used as the only argument to ffmpeg when encoding."));


        // Check to see if the path to ffmpeg actually leads to something.
        if(pane.getField_ffmpegPath().getText().isEmpty() || Files.exists(Paths.get(pane.getField_ffmpegPath().getText())) == false) {
            pane.getField_ffmpegPath().getStylesheets().remove("global.css");
            pane.getField_ffmpegPath().getStylesheets().add("field_error.css");

            final Tooltip currentTooltip = pane.getField_ffmpegPath().getTooltip();
            final String errorText = "Error - You must set the location of FFMPEG.";
            pane.getField_ffmpegPath().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

            wasErrorFound = true;
        }

        // If the fully-custom ffmpeg options are enabled, then ensure that both fields are filled out.
        // Else check all of the other settings.
        if(pane.getRadioButton_useFullyCustomEncodingOptions_yes().isSelected()) {
            if(! pane.getField_fullyCustomFfmpegEncodingOptions().getText().isEmpty()) {
                pane.getField_fullyCustomFfmpegEncodingOptions().getStylesheets().remove("global.css");
                pane.getField_fullyCustomFfmpegEncodingOptions().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_fullyCustomFfmpegEncodingOptions().getTooltip();
                final String errorText = "Error - Because you have enabled the fully-custom FFMPEG settings you must enter your own encoding commands\nRead the readme for more information.";
                pane.getField_fullyCustomFfmpegEncodingOptions().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

                wasErrorFound = true;
            }

            if(! pane.getField_fullyCustomFfmpegDecodingptions().getText().isEmpty()) {
                pane.getField_fullyCustomFfmpegDecodingptions().getStylesheets().remove("global.css");
                pane.getField_fullyCustomFfmpegDecodingptions().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_fullyCustomFfmpegDecodingptions().getTooltip();
                final String errorText = "Error - Because you have enabled the fully-custom FFMPEG settings you must enter your own decoding commands\nRead the readme for more information.";
                pane.getField_fullyCustomFfmpegDecodingptions().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

                wasErrorFound = true;
            }
        } else {
            // Check to see if encode, decode, encoding library fields have been set.
            if(pane.getField_encodeFormat().getText().isEmpty()) {
                pane.getField_encodeFormat().getStylesheets().remove("global.css");
                pane.getField_encodeFormat().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_encodeFormat().getTooltip();
                final String errorText = "Error - You must enter an encode format.";
                pane.getField_encodeFormat().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));
            }

            if(pane.getField_decodeFormat().getText().isEmpty()) {
                pane.getField_decodeFormat().getStylesheets().remove("global.css");
                pane.getField_decodeFormat().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_decodeFormat().getTooltip();
                final String errorText = "Error - You must enter an decode format.";
                pane.getField_decodeFormat().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));
            }

            if(pane.getField_encodingLibrary().getText().isEmpty()) {
                pane.getField_encodingLibrary().getStylesheets().remove("global.css");
                pane.getField_encodingLibrary().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_encodingLibrary().getTooltip();
                final String errorText = "Error - You must enter an encoding library.";
                pane.getField_encodingLibrary().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));
            }

            // Check to see that all number-fields are actually numbers.
            // Check to see that all number-fields contain acceptable numbers.
            // Set their error states to true if they don't.
            try {
                int temp = Integer.valueOf(pane.getField_encodedVideoWidth().getText());

                if(temp < 1) {
                    throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
                }
            } catch(final NumberFormatException e) {
                pane.getField_encodedVideoWidth().getStylesheets().remove("global.css");
                pane.getField_encodedVideoWidth().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_encodedVideoWidth().getTooltip();
                final String errorText = "Error - There is no integer entered here. Please enter an integer of 1 or greater.";
                pane.getField_encodedVideoWidth().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

                wasErrorFound = true;
            }

            try {
                int temp = Integer.valueOf(pane.getField_encodedVideoHeight().getText());

                if(temp < 1) {
                    throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
                }
            } catch(final NumberFormatException e) {
                pane.getField_encodedVideoHeight().getStylesheets().remove("global.css");
                pane.getField_encodedVideoHeight().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_encodedVideoHeight().getTooltip();
                final String errorText = "Error - There is no integer entered here. Please enter an integer of 1 or greater.";
                pane.getField_encodedVideoHeight().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

                wasErrorFound = true;
            }

            try {
                int temp = Integer.valueOf(pane.getField_encodedFramerate().getText());

                if(temp < 1) {
                    throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
                }
            } catch(final NumberFormatException e) {
                pane.getField_encodedFramerate().getStylesheets().remove("global.css");
                pane.getField_encodedFramerate().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_encodedFramerate().getTooltip();
                final String errorText = "Error - There is no integer entered here. Please enter an integer of 1 or greater.";
                pane.getField_encodedFramerate().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

                wasErrorFound = true;
            }

            try {
                int temp = Integer.valueOf(pane.getField_macroBlockDimensions().getText());

                if(temp < 1) {
                    throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
                }
            } catch(final NumberFormatException e) {
                pane.getField_macroBlockDimensions().getStylesheets().remove("global.css");
                pane.getField_macroBlockDimensions().getStylesheets().add("field_error.css");

                final Tooltip currentTooltip = pane.getField_macroBlockDimensions().getTooltip();
                final String errorText = "Error - There is no integer entered here. Please enter an integer of 1 or greater.";
                pane.getField_macroBlockDimensions().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

                wasErrorFound = true;
            }
        }


        return wasErrorFound;
    }

    /**
     * Analyzes a portion of the settings to determine if the resulting video
     * might not work correctly with YouTube.
     */
    public void displayWarningsAboutYouTubeCompatability() {
        // Ensure that settings are acceptable first:
        if(!areSettingsCorrect()) {
            final StringBuilder stringBuilder = new StringBuilder();

            // Check for supported resolutions:
            int videoHeight = Integer.valueOf(pane.getField_encodedVideoHeight().getText());
            switch(Integer.valueOf(pane.getField_encodedVideoWidth().getText())) {
                case 426: { // 240p
                    if(videoHeight != 240) {
                        stringBuilder.append("The video width of 426 must be used with a video height of 240 to respect YouTube's 16:9 ratio.\n");
                    }
                    break;
                }
                case 640: { // 360p
                    if(videoHeight != 360) {
                        stringBuilder.append("The video width of 640 must be used with a video height of 360 to respect YouTube's 16:9 ratio.\n");
                    }
                    break;
                }
                case 854: { // 480p
                    if(videoHeight != 480) {
                        stringBuilder.append("The video width of 854 must be used with a video height of 480 to respect YouTube's 16:9 ratio.\n");
                    }
                    break;
                }
                case 1280: { // 720p
                    if(videoHeight != 720) {
                        stringBuilder.append("The video width of 1280 must be used with a video height of 720 to respect YouTube's 16:9 ratio.\n");
                    }
                    break;
                }
                case 1920: { // 1080p
                    if(videoHeight != 1080) {
                        stringBuilder.append("The video width of 1920 must be used with a video height of 1080 to respect YouTube's 16:9 ratio.\n");
                    }
                    break;
                }
                case 2560: { // 1440p
                    if(videoHeight != 1440) {
                        stringBuilder.append("The video width of 2560 must be used with a video height of 1440 to respect YouTube's 16:9 ratio.\n");
                    }
                    break;
                }
                case 3840: { // 2160p
                    if(videoHeight != 2160) {
                        stringBuilder.append("The video width of 3840 must be used with a video height of 2160 to respect YouTube's 16:9 ratio.\n");
                    }
                    break;
                }
                default: {
                    stringBuilder.append("The width and height you have set do not conform to YouTube's 16:9 ratio.\n");
                }
            }

            // Check if the framerate is supported:
            int framerate = Integer.valueOf(pane.getField_encodedFramerate().getText());

            if(framerate != 24 && framerate != 25 && framerate != 30 && framerate != 48 && framerate != 50 && framerate != 60) {
                stringBuilder.append("The framerate you have set is not in the accepted framerates of 24, 25, 30, 48, 50, or 60 that YouTube supports.\n");
            }

            // Show popup:
            if(stringBuilder.toString().isEmpty() == false) {
                final Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("YouTube Compatability Warning(s)");
                alert.setContentText(stringBuilder.toString());
                alert.showAndWait();
            }
        }
    }

    // todo JavaDoc
    public FfmpegSettingsPane getPane() {
        return pane;
    }
}
