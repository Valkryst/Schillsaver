package controller.settings;

import handler.ConfigHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import view.settings.FfmpegSettingsPane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class FfmpegSettingsController implements EventHandler {
    // todo JavaDoc
    @Getter private final FfmpegSettingsPane pane;

    // todo JavaDoc
    private final Stage settingsStage;

    public FfmpegSettingsController(final Stage settingsStage, final ConfigHandler configHandler) {
        this.settingsStage = settingsStage;
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
        removeErrorCSSStyle(pane.getField_ffmpegPath());
        pane.getField_ffmpegPath().setTooltip(new Tooltip("The absolute path to ffmpeg/ffmpeg.exe."));

        removeErrorCSSStyle(pane.getField_encodeFormat());
        pane.getField_encodeFormat().setTooltip(new Tooltip("The format to encode to."));

        removeErrorCSSStyle(pane.getField_decodeFormat());
        pane.getField_decodeFormat().setTooltip(new Tooltip("The format to decode to.\n\nThis should be the handler that your archival program archives to.\nWith 7zip, this should be set to 7z."));

        removeErrorCSSStyle(pane.getField_encodedVideoWidth());
        pane.getField_encodedVideoWidth().setTooltip(new Tooltip("The width, in pixels, of the encoded video."));

        removeErrorCSSStyle(pane.getField_encodedVideoHeight());
        pane.getField_encodedVideoHeight().setTooltip(new Tooltip("The height, in pixels, of the encoded video."));

        removeErrorCSSStyle(pane.getField_encodedFramerate());
        pane.getField_encodedFramerate().setTooltip(new Tooltip("The framerate of the encoded video. Ex ~ 30, 60, etc..."));

        removeErrorCSSStyle(pane.getField_macroBlockDimensions());
        pane.getField_macroBlockDimensions().setTooltip(new Tooltip("The width/height of each encoded macroblock."));

        removeErrorCSSStyle(pane.getField_encodingLibrary());
        pane.getField_encodingLibrary().setTooltip(new Tooltip("The library to encode the video with."));

        removeErrorCSSStyle(pane.getField_fullyCustomFfmpegEncodingOptions());
        pane.getField_fullyCustomFfmpegEncodingOptions().setTooltip(new Tooltip("The commands to use when encoding a file with ffmpeg.\n\n" +
                                                                               "If the fully-custom options are enabled, then all other ffmpeg options are ignored\n" +
                                                                               "and this string will be used as the only argument to ffmpeg when encoding."));

        removeErrorCSSStyle(pane.getField_fullyCustomFfmpegDecodingptions());
        pane.getField_fullyCustomFfmpegDecodingptions().setTooltip(new Tooltip("The commands to use when encoding a file with ffmpeg.\n\n" +
                                                                              "If the fully-custom options are enabled, then all other ffmpeg options are ignored\n" +
                                                                              "and this string will be used as the only argument to ffmpeg when encoding."));


        // Check to see if the path to ffmpeg actually leads to something.
        if(pane.getField_ffmpegPath().getText().isEmpty() || Files.exists(Paths.get(pane.getField_ffmpegPath().getText())) == false) {
            applyErrorCSSStyle(pane.getField_ffmpegPath());

            final Tooltip currentTooltip = pane.getField_ffmpegPath().getTooltip();
            final String errorText = "Error - You must set the location of FFMPEG.";
            pane.getField_ffmpegPath().setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorText));

            wasErrorFound = true;
        }

        // If the fully-custom ffmpeg options are enabled, then ensure that both fields are filled out.
        // Else check all of the other settings.
        if(pane.getRadioButton_useFullyCustomEncodingOptions_yes().isSelected()) {
            if(! pane.getField_fullyCustomFfmpegEncodingOptions().getText().isEmpty()) {
                final String errorMessage = "Error - Because you have enabled the fully-custom FFMPEG settings you must " +
                                            "enter your own encoding commands\nRead the readme for more information.";

                applyErrorCSSStyle(pane.getField_fullyCustomFfmpegEncodingOptions());
                appendErrorMessageToTooltip(pane.getField_fullyCustomFfmpegEncodingOptions(), errorMessage);
                wasErrorFound = true;
            }

            if(! pane.getField_fullyCustomFfmpegDecodingptions().getText().isEmpty()) {
                final String errorMessage = "Error - Because you have enabled the fully-custom FFMPEG settings you must " +
                                            "enter your own decoding commands\nRead the readme for more information.";

                applyErrorCSSStyle(pane.getField_fullyCustomFfmpegDecodingptions());
                appendErrorMessageToTooltip(pane.getField_fullyCustomFfmpegDecodingptions(), errorMessage);
                wasErrorFound = true;
            }
        } else {
            // Check to see if encode, decode, encoding library fields have been set.
            if(pane.getField_encodeFormat().getText().isEmpty()) {
                applyErrorCSSStyle(pane.getField_encodeFormat());
                appendErrorMessageToTooltip(pane.getField_encodeFormat(), "Error - You must enter an encode format.");
            }

            if(pane.getField_decodeFormat().getText().isEmpty()) {
                applyErrorCSSStyle(pane.getField_decodeFormat());
                appendErrorMessageToTooltip(pane.getField_encodeFormat(), "Error - You must enter an decode format.");
            }

            if(pane.getField_encodingLibrary().getText().isEmpty()) {
                applyErrorCSSStyle(pane.getField_encodingLibrary());
                appendErrorMessageToTooltip(pane.getField_encodeFormat(), "Error - You must enter an encoding library.");
            }

            // Check to see that all number-fields are positive numbers and set their error
            // tooltips and CSS styles if they don't.
            final String errorMessage = "Error - There is no integer entered here. Please enter an integer of 1 or greater.";


            if (containsPositiveInteger(pane.getField_encodedVideoWidth()) == false) {
                applyErrorCSSStyle(pane.getField_encodedVideoWidth());
                appendErrorMessageToTooltip(pane.getField_encodedVideoWidth(), errorMessage);

                wasErrorFound = true;
            }


            if (containsPositiveInteger(pane.getField_encodedVideoHeight()) == false) {
                applyErrorCSSStyle(pane.getField_encodedVideoHeight());
                appendErrorMessageToTooltip(pane.getField_encodedVideoHeight(), errorMessage);

                wasErrorFound = true;
            }

            if (containsPositiveInteger(pane.getField_encodedFramerate()) == false) {
                applyErrorCSSStyle(pane.getField_encodedFramerate());
                appendErrorMessageToTooltip(pane.getField_encodedFramerate(), errorMessage);

                wasErrorFound = true;
            }


            if (containsPositiveInteger(pane.getField_macroBlockDimensions()) == false) {
                applyErrorCSSStyle(pane.getField_macroBlockDimensions());
                appendErrorMessageToTooltip(pane.getField_macroBlockDimensions(), errorMessage);
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
                alert.setHeaderText("YouTube Compatibility Warning(s)");
                alert.setContentText(stringBuilder.toString());
                alert.showAndWait();
            }
        }
    }

    /**
     * Determines whether or not the specified TextField contains a positive
     * integer.
     *
     * @param textField
     *         The TextField whose contents are to be checked.
     *
     * @return
     *         Whether or not the specified TextField contains a positive
     *         integer.
     */
    private boolean containsPositiveInteger(final TextField textField) {
        return Pattern.matches("^[1-9]+[0-9]*$", textField.getText());
    }

    /**
     * Removes the normal CSS styling and applies the error CSS styling to the
     * specified TextField.
     *
     * @param textField
     *         The TextField to apply the error CSS styling to.
     */
    private void applyErrorCSSStyle(final TextField textField) {
        textField.getStylesheets().remove("global.css");
        textField.getStylesheets().add("field_error.css");
    }

    /**
     * Removes the error CSS styling and applies the normal CSS styling to the
     * specified TextField.
     *
     * @param textField
     *         The TextField to apply the normal CSS styling to.
     */
    private void removeErrorCSSStyle(final TextField textField) {
        textField.getStylesheets().remove("field_error.css");
        textField.getStylesheets().add("global.css");
    }

    /**
     * Appends the specified error message to the Tooltip of the specified
     * TextField.
     *
     * @param textField
     *         The TextField whose Tooltip is to be appended to.
     *
     * @param errorMessage
     *         The error message to append to the Tooltip.
     */
    private void appendErrorMessageToTooltip(final TextField textField, final String errorMessage) {
        final Tooltip currentTooltip = textField.getTooltip();
        textField.setTooltip(new Tooltip(currentTooltip.getText() + "\n" + errorMessage));
    }
}
