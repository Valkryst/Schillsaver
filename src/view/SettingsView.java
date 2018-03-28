package view;

import com.valkryst.VIcons.VIconType;
import com.valkryst.VMVC.Settings;
import com.valkryst.VMVC.view.View;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;
import misc.BlockSize;
import misc.FrameDimension;
import misc.FrameRate;

import java.util.ArrayList;
import java.util.List;

public class SettingsView extends View {
    @Getter private Button button_selectFfmpegExecutablePath;
    @Getter private Button button_selectDefaultEncodingFolder;
    @Getter private Button button_selectDefaultDecodingFolder;
    @Getter private Button button_accept;
    @Getter private Button button_cancel;

    @Getter private TextField textField_ffmpegExecutablePath;
    @Getter private TextField textField_defaultEncodingFolder;
    @Getter private TextField textField_defaultDecodingFolder;
    @Getter private TextField textField_codec;

    @Getter private ComboBox<String> comboBox_frameDimensions;
    @Getter private ComboBox<String> comboBox_frameRate;
    @Getter private ComboBox<String> comboBox_blockSize;

    /**
     * Constructs a new SettingsView.
     *
     * @param settings
     *          The program settings.
     *
     * @throws NullPointerException
     *         If the settings is null.
     */
    public SettingsView(final @NonNull Settings settings) {
        initializeComponents(settings);
        setComponentTooltips();

        final var hBox = new HBox();
        HBox.setHgrow(textField_codec, Priority.ALWAYS);
        hBox.getChildren().addAll(textField_codec, comboBox_frameDimensions, comboBox_frameRate, comboBox_blockSize);

        final var vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getChildren().add(createControlRow(button_selectFfmpegExecutablePath, textField_ffmpegExecutablePath));
        vBox.getChildren().add(createControlRow(button_selectDefaultEncodingFolder, textField_defaultEncodingFolder));
        vBox.getChildren().add(createControlRow(button_selectDefaultDecodingFolder, textField_defaultDecodingFolder));
        vBox.getChildren().add(hBox);
        vBox.getChildren().add(createBottomMenuBar());

        this.pane = vBox;
        this.pane.setMinSize(720, 123);
    }

    /** Initializes the components. */
    private void initializeComponents(final @NonNull Settings settings) {
        button_selectFfmpegExecutablePath = new Button("Select FFMPEG Executable Path");
        button_selectDefaultEncodingFolder = new Button("Select Default Encoding Folder");
        button_selectDefaultDecodingFolder = new Button("Select Default Decoding Folder");
        button_accept = createIconButton(VIconType.BUTTON_ACCEPT.getFilePath(), 16, 16);
        button_cancel = createIconButton(VIconType.BUTTON_CANCEL.getFilePath(), 16, 16);

        textField_ffmpegExecutablePath = new TextField(settings.getStringSetting("FFMPEG Executable Path"));
        textField_ffmpegExecutablePath.setPromptText("FFMPEG Executable Path");
        textField_ffmpegExecutablePath.setDisable(true);

        textField_defaultEncodingFolder = new TextField(settings.getStringSetting("Default Encoding Output Directory"));
        textField_defaultEncodingFolder.setPromptText("Default Encoding Folder");
        textField_defaultEncodingFolder.setDisable(true);

        textField_defaultDecodingFolder = new TextField(settings.getStringSetting("Default Decoding Output Directory"));
        textField_defaultDecodingFolder.setPromptText("Default Decoding Folder");
        textField_defaultDecodingFolder.setDisable(true);

        textField_codec = new TextField(settings.getStringSetting("Encoding Codec"));
        textField_codec.setPromptText("Encoding Codec");

        // Setup Frame Dimensions Combo Box:
        final List<String> frameDimensions = new ArrayList<>();

        for (final FrameDimension dimension : FrameDimension.values()) {
            frameDimensions.add(dimension.name());
        }
        comboBox_frameDimensions = new ComboBox<>(FXCollections.observableArrayList(frameDimensions));
        comboBox_frameDimensions.getSelectionModel().select(settings.getStringSetting("Encoding Frame Dimensions"));

        // Setup Frame Rate Combo Box:
        final List<String> frameRates = new ArrayList<>();

        for (final FrameRate rate : FrameRate.values()) {
            frameRates.add(rate.name());
        }
        comboBox_frameRate = new ComboBox<>(FXCollections.observableArrayList(frameRates));
        comboBox_frameRate.getSelectionModel().select(settings.getStringSetting("Encoding Frame Rate"));

        // Setup Block Size Combo Box:
        final List<String> blockSizes = new ArrayList<>();

        for (final BlockSize size : BlockSize.values()) {
            blockSizes.add(size.name());
        }
        comboBox_blockSize = new ComboBox<>(FXCollections.observableArrayList(blockSizes));
        comboBox_blockSize.getSelectionModel().select(settings.getStringSetting("Encoding Block Size"));
    }

    /** Sets the tooltips of the components. */
    private void setComponentTooltips() {
        setTooltip(button_selectFfmpegExecutablePath, "Opens a file selection dialog to select the FFMPEG executable.");
        setTooltip(button_selectDefaultEncodingFolder, "Opens a folder selection dialog to select the default encoding folder that all encode jobs will output to.");
        setTooltip(button_selectDefaultDecodingFolder, "Opens a folder selection dialog to select the default decoding folder that all decode jobs will output to.");

        setTooltip(textField_ffmpegExecutablePath, "The path to the FFMPEG executable.");
        setTooltip(textField_defaultEncodingFolder, "The path to the default encoding folder that all encode jobs will output to.");
        setTooltip(textField_defaultDecodingFolder, "The path to the default decoding folder that all decode jobs will output to.");
        setTooltip(textField_codec, "The codec to use when encoding and decoding.");

        setTooltip(comboBox_frameRate, "The frame rate to use when encoding.");
        setTooltip(comboBox_blockSize, "The block size to use when encoding and decoding.");

        // Build the Frame Dimensions tooltip:
        final StringBuilder frameDimensionsTooltip = new StringBuilder();
        frameDimensionsTooltip.append("The frame dimensions to use when encoding.");

        for (final var frameDimension : FrameDimension.values()) {
            frameDimensionsTooltip.append("\n    ").append(frameDimension);
        }

        setTooltip(comboBox_frameDimensions, frameDimensionsTooltip.toString());
    }

    private Pane createControlRow(final @NonNull Button button, final @NonNull TextField field) {
        final HBox hBox = new HBox();
        VBox.setVgrow(hBox, Priority.ALWAYS);

        HBox.setHgrow(button, Priority.NEVER);
        HBox.setHgrow(field, Priority.ALWAYS);

        hBox.getChildren().add(button);
        hBox.getChildren().add(field);

        button.setMinWidth(256);

        return hBox;
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
