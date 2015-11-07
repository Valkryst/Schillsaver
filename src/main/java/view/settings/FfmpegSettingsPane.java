package view.settings;

import controller.settings.FfmpegSettingsController;
import handler.ConfigHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class FfmpegSettingsPane extends TitledPane {
    /** The name of the Tab. */
    private static final String TAB_NAME = "FFMPEG";

    /** The text field for the absolute path to ffmpeg/ffmpeg.exe. */
    private final TextField field_ffmpegPath = new TextField("ffmpegPath");
    /** The button to open the handler selection dialog for the ffmpeg executable. */
    private final Button button_selectFile_ffmpegPath = new Button("Select File");

    /** The text field for the format to encode to. */
    private final TextField field_encodeFormat = new TextField("encodeFormat");
    /** The text field for the format to decode to. */
    private final TextField field_decodeFormat = new TextField("decodeFormat");
    /** The text field for the width, in pixels, of the encoded video. */
    private final TextField field_encodedVideoWidth = new TextField("encodedVideoWidth");
    /** The text field for the height, in pixels, of the encoded video. */
    private final TextField field_encodedVideoHeight = new TextField("encodedVideoHeight");
    /** The text field for the framerate of the encoded video. */
    private final TextField field_encodedFramerate = new TextField("encodedFramerate");
    /** The text field for the width/height of each macroblock. */
    private final TextField field_macroBlockDimensions = new TextField("macroBlockDimensions");
    /** The text field for the codec library to use when encoding/decoding the library. */
    private final TextField field_encodingLibrary = new TextField("encodingLibrary");
    /** The combobox to select the logging level that ffmpeg should use. */
    private final ComboBox<String> comboBox_ffmpegLogLevel = new ComboBox<>(FXCollections.observableArrayList(ConfigHandler.FFMPEG_LOG_LEVELS));

    /** The toggle group of the yes/no radio buttons of the useFullyCustomEncodingOptions option. */
    private final ToggleGroup toggleGroup_useFullyCustomEncodingOptions = new ToggleGroup();
    /** The radio button that says to use the fully-custom ffmpeg en/decoding options. */
    private final RadioButton radioButton_useFullyCustomEncodingOptions_yes = new RadioButton("Yes");
    /** The radio button that says to not use the fully-custom ffmpeg en/decoding options. */
    private final RadioButton radioButton_useFullyCustomEncodingOptions_no = new RadioButton("No");
    /** The text field for the fully-custom ffmpeg encoding options. */
    private final TextField field_fullyCustomFfmpegEncodingOptions = new TextField("fullyCustomFfmpegEncodingOptions");
    /** The text field for the fully-custom ffmpeg decoding options. */
    private final TextField field_fullyCustomFfmpegDecodingptions = new TextField("fullyCustomFfmpegDecodingOptions");



    public FfmpegSettingsPane(final Stage settingsStage, final FfmpegSettingsController controller, final ConfigHandler configHandler) {
        // Setup Toggle Groups:
        toggleGroup_useFullyCustomEncodingOptions.getToggles().addAll(radioButton_useFullyCustomEncodingOptions_yes, radioButton_useFullyCustomEncodingOptions_no);

        // Set Default Values:
        field_ffmpegPath.setText(configHandler.getFfmpegPath());
        field_encodeFormat.setText(configHandler.getEncodeFormat());
        field_decodeFormat.setText(configHandler.getDecodeFormat());
        field_encodedVideoWidth.setText(String.valueOf(configHandler.getEncodedVideoWidth()));
        field_encodedVideoHeight.setText(String.valueOf(configHandler.getEncodedVideoHeight()));
        field_encodedFramerate.setText(String.valueOf(configHandler.getEncodedFramerate()));
        field_macroBlockDimensions.setText(String.valueOf(configHandler.getMacroBlockDimensions()));
        field_encodingLibrary.setText(configHandler.getEncodingLibrary());

        // todo If there's a better way to do this, then do it.
        for(int i = 0 ; i < ConfigHandler.FFMPEG_LOG_LEVELS.length ; i++) {
            if(ConfigHandler.FFMPEG_LOG_LEVELS[i].equals(configHandler.getFfmpegLogLevel())) {
                comboBox_ffmpegLogLevel.getSelectionModel().select(i);
                break;
            }
        }

        if(configHandler.getUseFullyCustomFfmpegOptions()) {
            radioButton_useFullyCustomEncodingOptions_yes.setSelected(true);
        } else {
            radioButton_useFullyCustomEncodingOptions_no.setSelected(true);
        }

        field_fullyCustomFfmpegEncodingOptions.setText(configHandler.getFullyCustomFfmpegEncodingOptions());
        field_fullyCustomFfmpegDecodingptions.setText(configHandler.getFullyCustomFfmpegDecodingOptions());


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

        final TitledPane pane_basicOptions = new TitledPane();
        pane_basicOptions.setText("Basic Options");
        pane_basicOptions.setCollapsible(false);
        pane_basicOptions.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_basicOptions.setContent(panel_top);



        final HBox panel_bottom_left = new HBox(10);
        panel_bottom_left.setAlignment(Pos.CENTER);
        panel_bottom_left.getChildren().addAll(radioButton_useFullyCustomEncodingOptions_yes, radioButton_useFullyCustomEncodingOptions_no);

        final TitledPane pane_enableAdvancedOptions = new TitledPane();
        pane_enableAdvancedOptions.setText("Enable Advanced Options");
        pane_enableAdvancedOptions.setCollapsible(false);
        pane_enableAdvancedOptions.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_enableAdvancedOptions.setContent(panel_bottom_left);

        final VBox panel_bottom_right= new VBox(4);
        HBox.setHgrow(field_fullyCustomFfmpegEncodingOptions, Priority.ALWAYS);
        HBox.setHgrow(field_fullyCustomFfmpegDecodingptions, Priority.ALWAYS);
        HBox.setHgrow(panel_bottom_right, Priority.ALWAYS);
        panel_bottom_right.getChildren().addAll(field_fullyCustomFfmpegEncodingOptions, field_fullyCustomFfmpegDecodingptions);

        final HBox panel_bottom = new HBox(10);
        panel_bottom.getChildren().addAll(pane_enableAdvancedOptions, panel_bottom_right);

        final TitledPane pane_advancedOptions = new TitledPane();
        HBox.setHgrow(pane_advancedOptions, Priority.ALWAYS);
        pane_advancedOptions.setText("Advanced Options");
        pane_advancedOptions.setCollapsible(false);
        pane_advancedOptions.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_advancedOptions.setContent(panel_bottom);



        final VBox panel = new VBox(4);
        panel.getChildren().addAll(pane_basicOptions, pane_advancedOptions);



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

    /** @return The text field for the absolute path to ffmpeg/ffmpeg.exe. */
    public TextField getField_ffmpegPath() {
        return field_ffmpegPath;
    }

    /** @return The button to open the handler selection dialog for the ffmpeg executable. */
    public Button getButton_selectFile_ffmpegPath() {
        return button_selectFile_ffmpegPath;
    }

    /** @return The text field for the format to encode to. */
    public TextField getField_encodeFormat() {
        return field_encodeFormat;
    }

    /** @return The text field for the format to decode to. */
    public TextField getField_decodeFormat() {
        return field_decodeFormat;
    }

    /** @return The text field for the width, in pixels, of the encoded video. */
    public TextField getField_encodedVideoWidth() {
        return field_encodedVideoWidth;
    }

    /** @return The text field for the height, in pixels, of the encoded video. */
    public TextField getField_encodedVideoHeight() {
        return field_encodedVideoHeight;
    }

    /** @return The text field for the framerate of the encoded video. */
    public TextField getField_encodedFramerate() {
        return field_encodedFramerate;
    }

    /** @return The text field for the codec library to use when encoding/decoding the library. */
    public TextField getField_macroBlockDimensions() {
        return field_macroBlockDimensions;
    }

    /** @return The combobox to select the logging level that ffmpeg should use. */
    public TextField getField_encodingLibrary() {
        return field_encodingLibrary;
    }

    /** @return The combobox to select the logging level that ffmpeg should use. */
    public ComboBox<String> getComboBox_ffmpegLogLevel() {
        return comboBox_ffmpegLogLevel;
    }

    /** @return The radio button that says to use the fully-custom ffmpeg en/decoding options. */
    public RadioButton getRadioButton_useFullyCustomEncodingOptions_yes() {
        return radioButton_useFullyCustomEncodingOptions_yes;
    }

    /** @return The radio button that says to not use the fully-custom ffmpeg en/decoding options. */
    public RadioButton getRadioButton_useFullyCustomEncodingOptions_no() {
        return radioButton_useFullyCustomEncodingOptions_no;
    }

    /** @return The text field for the fully-custom ffmpeg encoding options. */
    public TextField getField_fullyCustomFfmpegEncodingOptions() {
        return field_fullyCustomFfmpegEncodingOptions;
    }

    /** @return The text field for the fully-custom ffmpeg decoding options. */
    public TextField getField_fullyCustomFfmpegDecodingptions() {
        return field_fullyCustomFfmpegDecodingptions;
    }
}
