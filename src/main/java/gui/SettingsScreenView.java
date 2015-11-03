package gui;

import component.*;
import file.ConfigHandler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsScreenView extends JDialog {
    /** The preferred size of the SettingsScreenView. */
    public final Dimension PREFERRED_FRAME_SIZE = new Dimension(670, 680);

    /** The text field for the absolute path to ffmpeg/ffmpeg.exe. */
    private final VTextField field_ffmpegPath = new VTextField(256, "ffmpegPath", "The absolute path to ffmpeg/ffmpeg.exe.");
    /** The button to open the file selection dialog for the ffmpeg executable. */
    private final VButton button_selectFile_ffmpegPath = new VButton("Select File", "Opens the file selection dialog to locate the ffmpeg executable.");

    /** The text field for the absolute path to the 7zip, or whichever compresssion program the user wants to use, executable. */
    private final VTextField field_compressionProgramPath = new VTextField(256, "compressionProgramPath", "The absolute path to 7zip/7zip.exe or whichever compression program is specified.");
    /** The button to open the file selection dialog for the 7zip, or whichever compression program the user wants to use, executable. */
    private final VButton button_selectFile_compressionProgramPath = new VButton("Select File", "Opens the file selection dialog to locate a compression program executable.");

    /** The text field for the format to encode to. */
    private final VTextField field_encodeFormat = new VTextField(4, "encodeFormat", "The format to encode to.");
    /** The text field for the format to decode to. */
    private final VTextField field_decodeFormat = new VTextField(4, "decodeFormat", "The format to decode to.</br></br>This should be the file that your archival program archives to.</br>With 7zip, this should be set to 7z.");
    /** The text field for the width, in pixels, of the encoded video. */
    private final VTextField field_encodedVideoWidth = new VTextField(4, "encodedVideoWidth", "The width, in pixels, of the encoded video.");
    /** The text field for the height, in pixels, of the encoded video. */
    private final VTextField field_encodedVideoHeight = new VTextField(4, "encodedVideoHeight", "The height, in pixels, of the encoded video.");
    /** The text field for the framerate of the encoded video. */
    private final VTextField field_encodedFramerate = new VTextField(3, "encodedFramerate", "The framerate of the encoded video. Ex ~ 30, 60, etc...");
    /** The text field for the width/height of each macroblock. */
    private final VTextField field_macroBlockDimensions = new VTextField(3, "macroBlockDimensions", "The width/height of each encoded macroblock.");
    /** The text field for the codec library to use when encoding/decoding the library. */
    private final VTextField field_encodingLibrary = new VTextField(10, "encodingLibrary", "The library to encode the video with.");
    /** The combobox to select the logging level that ffmpeg should use. */
    private final JComboBox<String> comboBox_ffmpegLogLevel = new JComboBox<>(ConfigHandler.FFMPEG_LOG_LEVELS);
    /** The text field for the base commands to use when compressing a file before encoding. */
    private final VTextField field_compressionCommands = new VTextField(256, "compressionCommands", "The base commands to use when compressing a file before encoding.");

    /** The button group of the yes/no radio buttons of the useFullyCustomEncodingOptions option. */
    private final ButtonGroup buttonGroup_useFullyCustomEncodingOptions = new ButtonGroup();
    /** The radio button that says to use the fully-custom ffmpeg en/decoding options. */
    private final VRadioButton vRadioButton_useFullyCustomEncodingOptions_yes = new VRadioButton("Yes");
    /** The radio button that says to not use the fully-custom ffmpeg en/decoding options. */
    private final VRadioButton vRadioButton_useFullyCustomEncodingOptions_no = new VRadioButton("No");
    /** The text field for the fully-custom ffmpeg encoding options. */
    private final VTextField field_fullyCustomFfmpegEncodingOptions = new VTextField(256, "fullyCustomFfmpegEncodingOptions", "The commands to use when encoding a file with ffmpeg.</br></br>If the fully-custom options are enabled, then all other ffmpeg options are ignored</br>and this string will be used as the only argument to ffmpeg when encoding.");
    /** The text field for the fully-custom ffmpeg decoding options. */
    private final VTextField field_fullyCustomFfmpegDecodingptions = new VTextField(256, "fullyCustomFfmpegDecodingOptions", "The commands to use when encoding a file with ffmpeg.</br></br>If the fully-custom options are enabled, then all other ffmpeg options are ignored</br>and this string will be used as the only argument to ffmpeg when encoding.");

    /** The button group of the yes/no radio buttons of the deleteOriginalFileWhenEncoding option. */
    private final ButtonGroup buttonGroup_deleteOriginalFileWhenEncoding = new ButtonGroup();
    /** The radio button that says to delete the original file when encoding finishes. */
    private final VRadioButton vRadioButton_deleteOriginalFileWhenEncoding_yes = new VRadioButton("Yes");
    /** The radio button that says to not delete the originak file when encoding finishes. */
    private final VRadioButton vRadioButton_deleteOriginalFileWhenEncoding_no = new VRadioButton("No");

    /** The button group of the yes/no radio buttons of the deleteOriginalFileWhenDecoding option. */
    private final ButtonGroup buttonGroup_deleteOriginalFileWhenDecoding = new ButtonGroup();
    /** The radio button that says to delete the original file when decoding finishes. */
    private final VRadioButton vRadioButton_deleteOriginalFileWhenDecoding_yes = new VRadioButton("Yes");
    /** The radio button that says not to delete the original file when decoding finishes. */
    private final VRadioButton vRadioButton_deleteOriginalFileWhenDecoding_no = new VRadioButton("No");

    /** The button group of the yes/no radio buttons of the showSplashScreen option. */
    private final ButtonGroup buttonGroup_showSplashScreen = new ButtonGroup();
    /** The radio button that says to display the splash screen on program startup. */
    private final VRadioButton vRadioButton_showSplashScreen_yes = new VRadioButton("Yes");
    /** The radio button that says not to display the splash screen on program startup. */
    private final VRadioButton vRadioButton_showSplashScreen_no = new VRadioButton("No");

    /** The text field for the absolute path of the splash screen. */
    private final VTextField field_splashScreenFilePath = new VTextField(256, "splashScreenFilePath", "The absolute path to the splash screen to display.");
    /** The button to open the file selection dialog to locate an image to use as the splash screen.. */
    private final VButton button_selectFile_splashScreenFilePath = new VButton("Select File", "Opens the file selection dialog to locate an image to use as the splash screen.");

    /** The text field for the amount of time, in milliseconds, to display the splach screen for. */
    private final VTextField field_splashScreenDisplayTime = new VTextField(5, "splashScreenDisplayTime", "The amount of time, in milliseconds, to display the splash screen.</br></br>1000 = 1 second");

    /** The button to close the window and save settings. */
    private final VButton button_accept = new VButton("Accept", "Accept and save the new settings.");
    /** The button to close the window without saving settings. */
    private final VButton button_cancel = new VButton("Cancel", "Close this window witout saving the settings.");

    // todo JavaDoc
    public SettingsScreenView(final SettingsScreenController controller, final ConfigHandler configHandler) {
        // Initalize Everything:
        setButtonGroups();
        setDefaultValues(configHandler);

        // Set Component Colors:
        comboBox_ffmpegLogLevel.setBackground(VComponentGlobals.BACKGROUND_COLOR);
        comboBox_ffmpegLogLevel.setForeground(VComponentGlobals.TEXT_COLOR);

        // Set Component Listners:
        button_selectFile_ffmpegPath.addActionListener(controller);
        button_selectFile_compressionProgramPath.addActionListener(controller);
        button_selectFile_splashScreenFilePath.addActionListener(controller);
        button_accept.addActionListener(controller);
        button_cancel.addActionListener(controller);

        this.addWindowListener(controller);

        // Setup the Layout:
        final VPanel panel_section_ffmpegOptions_top = new VPanel();
        panel_section_ffmpegOptions_top.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_section_ffmpegOptions_top.add(field_ffmpegPath);
        panel_section_ffmpegOptions_top.add(button_selectFile_ffmpegPath);

        final VPanel panel_section_ffmpegOptions_center = new VPanel();
        panel_section_ffmpegOptions_center.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_section_ffmpegOptions_center.add(field_encodeFormat);
        panel_section_ffmpegOptions_center.add(field_decodeFormat);
        panel_section_ffmpegOptions_center.add(field_encodedVideoWidth);
        panel_section_ffmpegOptions_center.add(field_encodedVideoHeight);
        panel_section_ffmpegOptions_center.add(field_encodedFramerate);

        final VPanel panel_section_ffmpegOptions_bottom = new VPanel();
        panel_section_ffmpegOptions_bottom.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_section_ffmpegOptions_bottom.add(field_macroBlockDimensions);
        panel_section_ffmpegOptions_bottom.add(field_encodingLibrary);
        panel_section_ffmpegOptions_bottom.add(comboBox_ffmpegLogLevel);

        final VSection section_ffmpegOptions = new VSection();
        section_ffmpegOptions.setSectionTitle("FFMPEG Options");
        section_ffmpegOptions.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        section_ffmpegOptions.add(panel_section_ffmpegOptions_top, "dock north");
        section_ffmpegOptions.add(panel_section_ffmpegOptions_center, "dock center");
        section_ffmpegOptions.add(panel_section_ffmpegOptions_bottom, "dock south");



        final VPanel panel_section_compressionOptions_top = new VPanel();
        panel_section_compressionOptions_top.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_section_compressionOptions_top.add(field_compressionProgramPath);
        panel_section_compressionOptions_top.add(button_selectFile_compressionProgramPath);

        final VPanel panel_section_compressionOptions_bottom = new VPanel();
        panel_section_compressionOptions_bottom.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_section_compressionOptions_bottom.add(field_compressionCommands);

        final VSection section_compressionOptions = new VSection();
        section_compressionOptions.setSectionTitle("Archive Options");
        section_compressionOptions.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        section_compressionOptions.add(panel_section_compressionOptions_top, "dock north");
        section_compressionOptions.add(panel_section_compressionOptions_bottom, "dock south");



        final VSection section_section_fullyCustomOptions_left = new VSection();
        section_section_fullyCustomOptions_left.setSectionTitle("Enable");
        section_section_fullyCustomOptions_left.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        section_section_fullyCustomOptions_left.add(vRadioButton_useFullyCustomEncodingOptions_yes);
        section_section_fullyCustomOptions_left.add(vRadioButton_useFullyCustomEncodingOptions_no);

        final VSection section_panel_section_fullyCustomOptions_right_top = new VSection();
        section_panel_section_fullyCustomOptions_right_top.setSectionTitle("FFMPEG - Encoding");
        section_panel_section_fullyCustomOptions_right_top.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        section_panel_section_fullyCustomOptions_right_top.add(field_fullyCustomFfmpegEncodingOptions);

        final VSection section_panel_section_fullyCustomOptions_right_bottom = new VSection();
        section_panel_section_fullyCustomOptions_right_bottom.setSectionTitle("FFMPEG - Decoding");
        section_panel_section_fullyCustomOptions_right_bottom.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        section_panel_section_fullyCustomOptions_right_bottom.add(field_fullyCustomFfmpegDecodingptions);

        final VPanel panel_section_fullyCustomOptions_right = new VPanel();
        panel_section_fullyCustomOptions_right.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_section_fullyCustomOptions_right.add(section_panel_section_fullyCustomOptions_right_top, "dock north");
        panel_section_fullyCustomOptions_right.add(section_panel_section_fullyCustomOptions_right_bottom, "dock south");

        final VSection section_fullyCustomOptions = new VSection();
        section_fullyCustomOptions.setSectionTitle("FFMPEG - Fully Custom Settings");
        section_fullyCustomOptions.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        section_fullyCustomOptions.add(section_section_fullyCustomOptions_left, "dock west");
        section_fullyCustomOptions.add(panel_section_fullyCustomOptions_right, "dock east");


        final VSection section_section_fileOptions_top = new VSection();
        section_section_fileOptions_top.setSectionTitle("Delete Original File When Encoding");
        section_section_fileOptions_top.setLayout(new MigLayout("gapy 4, gapx 4"));
        section_section_fileOptions_top.add(vRadioButton_deleteOriginalFileWhenEncoding_yes);
        section_section_fileOptions_top.add(vRadioButton_deleteOriginalFileWhenEncoding_no);

        final VSection section_section_fileOptions_bottom = new VSection();
        section_section_fileOptions_bottom.setSectionTitle("Delete Original File When Decoding");
        section_section_fileOptions_bottom.setLayout(new MigLayout("gapy 4, gapx 4"));
        section_section_fileOptions_bottom.add(vRadioButton_deleteOriginalFileWhenDecoding_yes);
        section_section_fileOptions_bottom.add(vRadioButton_deleteOriginalFileWhenDecoding_no);

        final VSection section_fileOptions = new VSection();
        section_fileOptions.setSectionTitle("Misc");
        section_fileOptions.setLayout(new MigLayout("gapy 4, gapx 4"));
        section_fileOptions.add(section_section_fileOptions_top, "w 50%, growy");
        section_fileOptions.add(section_section_fileOptions_bottom, "w 50%, growy");



        final VSection Section_section_splashScreenOptions_top = new VSection();
        Section_section_splashScreenOptions_top.setSectionTitle("Show Splash Screen");
        Section_section_splashScreenOptions_top.setLayout(new MigLayout("gapy 4, gapx 4"));
        Section_section_splashScreenOptions_top.add(vRadioButton_showSplashScreen_yes);
        Section_section_splashScreenOptions_top.add(vRadioButton_showSplashScreen_no);

        final VPanel panel_section_splashScreenOptions_bottom_top = new VPanel();
        panel_section_splashScreenOptions_bottom_top.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_section_splashScreenOptions_bottom_top.add(field_splashScreenFilePath);
        panel_section_splashScreenOptions_bottom_top.add(button_selectFile_splashScreenFilePath);

        final VPanel panel_section_splashScreenOptions_bottom_bottom = new VPanel();
        panel_section_splashScreenOptions_bottom_bottom.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_section_splashScreenOptions_bottom_bottom.add(field_splashScreenDisplayTime);

        final VPanel panel_section_splashScreenOptions_bottom = new VPanel();
        panel_section_splashScreenOptions_bottom.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_section_splashScreenOptions_bottom.add(panel_section_splashScreenOptions_bottom_top, "dock north");
        panel_section_splashScreenOptions_bottom.add(panel_section_splashScreenOptions_bottom_bottom, "dock south");


        final VSection section_splashScreenOptions = new VSection();
        section_splashScreenOptions.setSectionTitle("Splash Screen");
        section_splashScreenOptions.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        section_splashScreenOptions.add(Section_section_splashScreenOptions_top, "dock north");
        section_splashScreenOptions.add(panel_section_splashScreenOptions_bottom, "dock south");



        final VPanel panel_top_top = new VPanel();
        panel_top_top.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_top_top.add(section_ffmpegOptions, "dock north");
        panel_top_top.add(section_compressionOptions, "dock south");



        final VPanel panel_top_center = new VPanel();
        panel_top_center.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_top_center.add(section_fullyCustomOptions);



        final VPanel panel_top_bottom = new VPanel();
        panel_top_bottom.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_top_bottom.add(section_fileOptions, "dock north");
        panel_top_bottom.add(section_splashScreenOptions, "dock south");



        final VPanel panel_top = new VPanel();
        panel_top.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_top.add(panel_top_top, "dock north");
        panel_top.add(panel_top_center, "dock center");
        panel_top.add(panel_top_bottom, "dock south");



        final VPanel panel_bottom = new VPanel();
        panel_bottom.setLayout(new MigLayout("align center center, gapy 4, gapx 4"));
        panel_bottom.add(button_accept);
        panel_bottom.add(button_cancel);



        this.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        this.add(panel_top, "growx, growy");
        this.add(panel_bottom, "dock south");
    }

    /** Sets up which radio buttons belong to which group. */
    private void setButtonGroups() {
        buttonGroup_deleteOriginalFileWhenEncoding.add(vRadioButton_deleteOriginalFileWhenEncoding_yes);
        buttonGroup_deleteOriginalFileWhenEncoding.add(vRadioButton_deleteOriginalFileWhenEncoding_no);

        buttonGroup_deleteOriginalFileWhenDecoding.add(vRadioButton_deleteOriginalFileWhenDecoding_yes);
        buttonGroup_deleteOriginalFileWhenDecoding.add(vRadioButton_deleteOriginalFileWhenDecoding_no);

        buttonGroup_showSplashScreen.add(vRadioButton_showSplashScreen_yes);
        buttonGroup_showSplashScreen.add(vRadioButton_showSplashScreen_no);

        buttonGroup_useFullyCustomEncodingOptions.add(vRadioButton_useFullyCustomEncodingOptions_yes);
        buttonGroup_useFullyCustomEncodingOptions.add(vRadioButton_useFullyCustomEncodingOptions_no);
    }

    /**
     * Retrieves data from the specified configuration handler
     * and fills in whatever data is available.
     * @param configHandler todo JavaDoc
     */
    private void setDefaultValues(final ConfigHandler configHandler) {
        field_ffmpegPath.setText(configHandler.getFfmpegPath());
        field_compressionProgramPath.setText(configHandler.getCompressionProgramPath());
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
                comboBox_ffmpegLogLevel.setSelectedIndex(i);
                break;
            }
        }

        if(configHandler.getUseFullyCustomFfmpegOptions()) {
            vRadioButton_useFullyCustomEncodingOptions_yes.setSelected(true);
        } else {
            vRadioButton_useFullyCustomEncodingOptions_no.setSelected(true);
        }

        field_fullyCustomFfmpegEncodingOptions.setText(configHandler.getFullyCustomFfmpegEncodingOptions());

        field_fullyCustomFfmpegDecodingptions.setText(configHandler.getFullyCustomFfmpegDecodingOptions());

        if(configHandler.getDeleteOriginalFileWhenEncoding()) {
            vRadioButton_deleteOriginalFileWhenEncoding_yes.setSelected(true);
        } else {
            vRadioButton_deleteOriginalFileWhenEncoding_no.setSelected(true);
        }

        if(configHandler.getDeleteOriginalFileWhenDecoding()) {
            vRadioButton_deleteOriginalFileWhenDecoding_yes.setSelected(true);
        } else {
            vRadioButton_deleteOriginalFileWhenDecoding_no.setSelected(true);
        }

        if(configHandler.getShowSplashScreen()) {
            vRadioButton_showSplashScreen_yes.setSelected(true);
        } else {
            vRadioButton_showSplashScreen_no.setSelected(true);
        }

        field_splashScreenFilePath.setText(configHandler.getSplashScreenFilePath());
        field_splashScreenDisplayTime.setText(String.valueOf(configHandler.getSplashScreenDisplayTime()));
        field_compressionCommands.setText(configHandler.getCompressionCommands());
    }

    /**
     * Resets the error states and tooltips of all components that might
     * have their error states set in the checkForErrors() method.
     *
     * todo Add additional error state resets after checkForErrors() is expanded.
     */
    private void resetErrorStates() {
        field_ffmpegPath.setErrorState(false);
        field_compressionProgramPath.setErrorState(false);
        field_encodedVideoWidth.setErrorState(false);
        field_encodedVideoHeight.setErrorState(false);
        field_encodedFramerate.setErrorState(false);
        field_macroBlockDimensions.setErrorState(false);
        field_splashScreenDisplayTime.setErrorState(false);

        field_ffmpegPath.resetTooltip();
        field_compressionProgramPath.resetTooltip();
        field_encodedVideoWidth.resetTooltip();
        field_encodedVideoHeight.resetTooltip();
        field_encodedFramerate.resetTooltip();
        field_macroBlockDimensions.resetTooltip();
        field_splashScreenDisplayTime.resetTooltip();
    }

    /**
     * Checks every field and button group for input errors.
     * Checks every path field to see if anything has been entered.
     * Not everything that can be checked is checked, just the basics.
     *
     * todo Expand error checking to check if the ffmpeg/compressor paths are correct.
     * @return Whether or not any errors were found.
     */
    private boolean checkForErrors() {
        boolean errorFound = false;

        // Check to see that the ffmpeg and archive programs exist, but
        // don't bother checking if they're the correct files as the user
        // should be able to realize this themselves.
        if(Files.exists(Paths.get(field_ffmpegPath.getText())) == false) {
            field_ffmpegPath.setErrorState(true);
            field_ffmpegPath.appendToTooltip("Error - This does not point to an existing file.");
            errorFound = true;
        }

        if(Files.exists(Paths.get(field_compressionProgramPath.getText())) == false) {
            field_compressionProgramPath.setErrorState(true);
            field_compressionProgramPath.appendToTooltip("Error - This does not point to an existing file.");
            errorFound = true;
        }

        // Check to see that all number-fields are actually numbers.
        // Check to see that all number-fields contain acceptable numbers.
        // Set their error states to true if they don't.
        try {
            int temp = Integer.valueOf(field_encodedVideoWidth.getText());

            if(temp < 1) {
                throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
            }
        } catch(final NumberFormatException e) {
            field_encodedVideoWidth.setErrorState(true);
            field_encodedVideoWidth.appendToTooltip("Error - There is no integer entered here. Please enter an integer of 1 or greater.");
            errorFound = true;
        }

        try {
            int temp = Integer.valueOf(field_encodedVideoHeight.getText());

            if(temp < 1) {
                throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
            }
        } catch(final NumberFormatException e) {
            field_encodedVideoHeight.setErrorState(true);
            field_encodedVideoWidth.appendToTooltip("Error - There is no integer entered here. Please enter an integer of 1 or greater.");
            errorFound = true;
        }

        try {
            int temp = Integer.valueOf(field_encodedFramerate.getText());

            if(temp < 1) {
                throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
            }
        } catch(final NumberFormatException e) {
            field_encodedFramerate.setErrorState(true);
            field_encodedVideoWidth.appendToTooltip("Error - There is no integer entered here. Please enter an integer of 1 or greater.");
            errorFound = true;
        }

        try {
            int temp = Integer.valueOf(field_macroBlockDimensions.getText());

            if(temp < 1) {
                throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
            }
        } catch(final NumberFormatException e) {
            field_macroBlockDimensions.setErrorState(true);
            field_macroBlockDimensions.appendToTooltip("Error - There is no integer entered here. Please enter an integer of 1 or greater.");
            errorFound = true;
        }

        try {
            int temp = Integer.valueOf(field_splashScreenDisplayTime.getText());

            if(temp < 1) {
                throw new NumberFormatException(""); // Throw an empty exception to trigger error handling.
            }
        } catch(final NumberFormatException e) {
            field_splashScreenDisplayTime.setErrorState(true);
            field_encodedVideoWidth.appendToTooltip("Error - There is no integer entered here. Please enter an integer of 1 or greater.");
            errorFound = true;
        }

        return errorFound;
    }

    /**
     * Applies the current settings to the specified config handler,
     * then generates a new config file.
     *
     * Does nothing if errors are found in the settings.
     *
     * @param configHandler todo JavaDoc
     * @return Whether or not the settings were saved.
     */
    public boolean saveSettings(final ConfigHandler configHandler) {
        resetErrorStates();

        // If no errors are found in the settings, then save them.
        if(!checkForErrors()) {
            configHandler.setFfmpegPath(field_ffmpegPath.getText());
            configHandler.setCompressionProgramPath(field_compressionProgramPath.getText());
            configHandler.setEncodeFormat(field_encodeFormat.getText());
            configHandler.setDecodeFormat(field_decodeFormat.getText());
            configHandler.setEncodedVideoWidth(Integer.valueOf(field_encodedVideoWidth.getText()));
            configHandler.setEncodedVideoHeight(Integer.valueOf(field_encodedVideoHeight.getText()));
            configHandler.setEncodedFramerate(Integer.valueOf(field_encodedFramerate.getText()));
            configHandler.setMacroBlockDimensions(Integer.valueOf(field_macroBlockDimensions.getText()));
            configHandler.setEncodingLibrary(field_encodingLibrary.getText());
            configHandler.setFfmpegLogLevel((String) comboBox_ffmpegLogLevel.getSelectedItem()); // The combo box stores strings, so casting to a String is fine.
            configHandler.setDeleteOriginalFileWhenEncoding(vRadioButton_deleteOriginalFileWhenEncoding_yes.isSelected());
            configHandler.setDeleteOriginalFileWhenDecoding(vRadioButton_deleteOriginalFileWhenEncoding_yes.isSelected());

            // Don't allow the splash screen to be shown if the file it points to
            // doesn't exist.
            if(Files.exists(Paths.get(field_splashScreenFilePath.getText()))) {
                configHandler.setShowSplashScreen(vRadioButton_showSplashScreen_yes.isSelected());
            } else {
                configHandler.setShowSplashScreen(false);
            }

            configHandler.setSplashScreenFilePath(field_splashScreenFilePath.getText());
            configHandler.setSplashScreenDisplayTime(Integer.valueOf(field_splashScreenDisplayTime.getText()));
            configHandler.setCompressionCommands(field_compressionCommands.getText());
            configHandler.createConfigFile();

            return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////// Getters

    /** @return The text field for the absolute path to ffmpeg/ffmpeg.exe. */
    public VTextField getField_ffmpegPath() {
        return field_ffmpegPath;
    }

    /** @return The button to open the file selection dialog for the ffmpeg executable. */
    public VButton getButton_selectFile_ffmpegPath() {
        return button_selectFile_ffmpegPath;
    }

    /** @return The text field for the absolute path to the 7zip, or whichever compresssion program the user wants to use, executable. */
    public VTextField getField_compressionProgramPath() {
        return field_compressionProgramPath;
    }

    /** @return The button to open the file selection dialog for the 7zip, or whichever compression program the user wants to use, executable. */
    public VButton getButton_selectFile_compressionProgramPath() {
        return button_selectFile_compressionProgramPath;
    }

    /** @return The text field for the absolute path of the splash screen. */
    public VTextField getField_splashScreenFilePath() {
        return field_splashScreenFilePath;
    }

    /** @return The button to open the file selection dialog to locate an image to use as the splash screen.. */
    public VButton getButton_selectFile_splashScreenFilePath() {
        return button_selectFile_splashScreenFilePath;
    }

    /** @return The button to close the window and save settings. */
    public VButton getButton_accept() {
        return button_accept;
    }

    /** @return The button to close the window without saving settings. */
    public VButton getButton_cancel() {
        return button_cancel;
    }
}
