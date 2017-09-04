package view.settings;

import configuration.Settings;
import controller.settings.FfmpegSettingsController;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;


public class FfmpegSettingsPane extends VBox {
    private static final String[] FFMPEG_LOG_LEVELS = {"quiet", "panic", "fatal", "error", "warning", "info", "verbose", "debug", "trace"};

    /** The text field for the absolute path to ffmpeg/ffmpeg.exe. */
    @Getter private final TextField field_ffmpegPath = new TextField();
    /** The button to open the handler selection dialog for the ffmpeg executable. */
    @Getter private final Button button_selectFile_ffmpegPath = new Button("Select File");

    /** The text field for the format to encode to. */
    @Getter private final TextField field_encodeFormat = new TextField();
    /** The text field for the format to decode to. */
    @Getter private final TextField field_decodeFormat = new TextField();
    /** The text field for the width, in pixels, of the encoded video. */
    @Getter private final TextField field_encodedVideoWidth = new TextField();
    /** The text field for the height, in pixels, of the encoded video. */
    @Getter private final TextField field_encodedVideoHeight = new TextField();
    /** The text field for the framerate of the encoded video. */
    @Getter private final TextField field_encodedFramerate = new TextField();
    /** The text field for the width/height of each macroblock. */
    @Getter private final TextField field_macroBlockDimensions = new TextField();
    /** The text field for the codec library to use when encoding/decoding the library. */
    @Getter private final TextField field_encodingLibrary = new TextField();
    /** The combobox to select the logging level that ffmpeg should use. */
    @Getter private final ComboBox<String> comboBox_ffmpegLogLevel = new ComboBox<>(FXCollections.observableArrayList(FFMPEG_LOG_LEVELS));

    /** The toggle group of the yes/no radio buttons of the useFullyCustomEncodingOptions option. */
    @Getter private final ToggleGroup toggleGroup_useFullyCustomEncodingOptions = new ToggleGroup();
    /** The radio button that says to use the fully-custom ffmpeg en/decoding options. */
    @Getter private final RadioButton radioButton_useFullyCustomEncodingOptions_yes = new RadioButton("Yes");
    /** The radio button that says to not use the fully-custom ffmpeg en/decoding options. */
    @Getter private final RadioButton radioButton_useFullyCustomEncodingOptions_no = new RadioButton("No");
    /** The text field for the fully-custom ffmpeg encoding options. */
    @Getter private final TextField field_fullyCustomFfmpegEncodingOptions = new TextField();
    /** The text field for the fully-custom ffmpeg decoding options. */
    @Getter private final TextField field_fullyCustomFfmpegDecodingptions = new TextField();



    public FfmpegSettingsPane(final Stage settingsStage, final FfmpegSettingsController controller, final Settings settings) {
        // Set Field Prompt Text:
        field_ffmpegPath.setPromptText("FFMPEG Executable Path");
        field_encodeFormat.setPromptText("Encode File Extension");
        field_decodeFormat.setPromptText("Decode File Extension");
        field_encodedVideoWidth.setPromptText("Encoded Video Width");
        field_encodedVideoHeight.setPromptText("Encoded Video Height");
        field_encodedFramerate.setPromptText("Encoded Framerate");
        field_macroBlockDimensions.setPromptText("Macro Block Dimensions");
        field_encodingLibrary.setPromptText("Encoding Library");
        field_fullyCustomFfmpegEncodingOptions.setPromptText("Fully Custom FFMPEG Encoding Options");
        field_fullyCustomFfmpegDecodingptions.setPromptText("Fully Custom FFMPEG Decoding Options");

        // Setup Toggle Groups:
        toggleGroup_useFullyCustomEncodingOptions.getToggles().addAll(radioButton_useFullyCustomEncodingOptions_yes, radioButton_useFullyCustomEncodingOptions_no);

        // Set Default Values:
        field_ffmpegPath.setText(settings.getStringSetting("FFMPEG Path"));
        field_encodeFormat.setText(settings.getStringSetting("Enc Format"));
        field_decodeFormat.setText(settings.getStringSetting("Dec Format"));
        field_encodedVideoWidth.setText(settings.getStringSetting("Enc Vid Width"));
        field_encodedVideoHeight.setText(settings.getStringSetting("Enc Vid Height"));
        field_encodedFramerate.setText(settings.getStringSetting("Enc Vid Framerate"));
        field_macroBlockDimensions.setText(settings.getStringSetting("Enc Vid Macro Block Dimensions"));
        field_encodingLibrary.setText(settings.getStringSetting("Enc Library"));

        // todo If there's a better way to do this, then do it.
        for(int i = 0 ; i < FFMPEG_LOG_LEVELS.length ; i++) {
            if(FFMPEG_LOG_LEVELS[i].equals(settings.getStringSetting("FFMPEG Log Level"))) {
                comboBox_ffmpegLogLevel.getSelectionModel().select(i);
                break;
            }
        }

        if (settings.getBooleanSetting("Use Custom FFMPEG Options")) {
            radioButton_useFullyCustomEncodingOptions_yes.setSelected(true);
        } else {
            radioButton_useFullyCustomEncodingOptions_no.setSelected(true);
        }

        field_fullyCustomFfmpegEncodingOptions.setText(settings.getStringSetting("Custom FFMPEG Enc Options"));
        field_fullyCustomFfmpegDecodingptions.setText(settings.getStringSetting("Custom FFMPEG Dec Options"));


        // Set Component Tooltips:
        field_ffmpegPath.setTooltip(new Tooltip("The absolute path to ffmpeg/ffmpeg.exe."));
        button_selectFile_ffmpegPath.setTooltip(new Tooltip("Opens the handler selection dialog to locate the ffmpeg executable."));
        field_encodeFormat.setTooltip(new Tooltip("The format to encode to."));
        field_decodeFormat.setTooltip(new Tooltip("The format to decode to.\n\nThis should be the handler that your archival program archives to.\nWith 7zip, this should be set to 7z."));
        field_encodedVideoWidth.setTooltip(new Tooltip("The width, in pixels, of the encoded video."));
        field_encodedVideoHeight.setTooltip(new Tooltip("The height, in pixels, of the encoded video."));
        field_encodedFramerate.setTooltip(new Tooltip("The framerate of the encoded video. Ex ~ 30, 60, etc..."));
        field_macroBlockDimensions.setTooltip(new Tooltip("The width/height of each encoded macroblock."));
        field_encodingLibrary.setTooltip(new Tooltip("The library to encode the video with."));
        comboBox_ffmpegLogLevel.setTooltip(new Tooltip("The logging level of FFMPEG."));
        field_fullyCustomFfmpegEncodingOptions.setTooltip(new Tooltip("The commands to use when encoding a file with ffmpeg.\n\n" +
                                                                      "If the fully-custom options are enabled, then all other ffmpeg options are ignored\n" +
                                                                      "and this string will be used as the only argument to ffmpeg when encoding."));
        field_fullyCustomFfmpegDecodingptions.setTooltip(new Tooltip("The commands to use when encoding a file with ffmpeg.\n\n" +
                                                                     "If the fully-custom options are enabled, then all other ffmpeg options are ignored\n" +
                                                                     "and this string will be used as the only argument to ffmpeg when encoding."));

        // Set Component Listeners:
        button_selectFile_ffmpegPath.setOnAction(controller);

        // Setup the Layout:
        final HBox panel_top_top = new HBox(10);
        HBox.setHgrow(field_ffmpegPath, Priority.ALWAYS);
        panel_top_top.getChildren().addAll(field_ffmpegPath, button_selectFile_ffmpegPath);

        final HBox panel_top_center = new HBox(10);
        panel_top_center.getChildren().addAll(field_encodeFormat, field_decodeFormat, field_encodedVideoWidth, field_encodedVideoHeight);

        final HBox panel_top_bottom = new HBox(10);
        HBox.setHgrow(comboBox_ffmpegLogLevel, Priority.ALWAYS);
        panel_top_bottom.getChildren().addAll(field_encodedFramerate, field_macroBlockDimensions, field_encodingLibrary, comboBox_ffmpegLogLevel);

        final VBox panel_top = new VBox(4);
        panel_top.getChildren().addAll(panel_top_top, panel_top_center, panel_top_bottom);

        final VBox pane_basicOptions = new VBox(4);
        pane_basicOptions.getChildren().addAll(new Label("Basic FFMPEG Options"), panel_top);



        final HBox panel_bottom_left = new HBox(10);
        panel_bottom_left.setAlignment(Pos.CENTER);
        panel_bottom_left.getChildren().addAll(radioButton_useFullyCustomEncodingOptions_yes, radioButton_useFullyCustomEncodingOptions_no);

        final TitledPane pane_enableAdvancedOptions = new TitledPane();
        pane_enableAdvancedOptions.setText("Enable Advanced Options");
        pane_enableAdvancedOptions.setCollapsible(false);
        pane_enableAdvancedOptions.heightProperty().addListener((observable, oldValue, newValue) -> settingsStage.sizeToScene());
        pane_enableAdvancedOptions.setContent(panel_bottom_left);

        final VBox panel_bottom_right= new VBox(4);
        HBox.setHgrow(field_fullyCustomFfmpegEncodingOptions, Priority.ALWAYS);
        HBox.setHgrow(field_fullyCustomFfmpegDecodingptions, Priority.ALWAYS);
        HBox.setHgrow(panel_bottom_right, Priority.ALWAYS);
        panel_bottom_right.getChildren().addAll(field_fullyCustomFfmpegEncodingOptions, field_fullyCustomFfmpegDecodingptions);

        final HBox panel_bottom = new HBox(10);
        panel_bottom.getChildren().addAll(pane_enableAdvancedOptions, panel_bottom_right);

        final VBox pane_advancedOptions = new VBox(4);
        HBox.setHgrow(pane_advancedOptions, Priority.ALWAYS);
        pane_advancedOptions.getChildren().addAll(new Label("Advanced FFMPEG Options"), panel_bottom);



        this.setSpacing(12);
        this.getChildren().addAll(pane_basicOptions, pane_advancedOptions);
    }
}
