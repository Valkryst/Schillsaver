package gui;

import component.VButton;
import component.VComponentGlobals;
import component.VPanel;
import component.VTextArea;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainScreenView extends VPanel {
    /** The minimum size of the frame that holds the view. */
    public final Dimension MINIMUM_FRAME_SIZE = new Dimension(512, 512);

    /** The text area for the ouput of FFMPEG from the most recent encode or decode job. */
    private VTextArea textArea_ffmpegOutput = new VTextArea("", true, null);

    /** The list model of all currently selected files by their handler names. */
    private DefaultListModel<String> listModel_selectedFiles = new DefaultListModel<>();
    /** The list of all currently selected files by their handler names. */
    private JList<String> list_selectedFiles = new JList<>();
    /** The scrollpane to show the currently selected files. */
    private JScrollPane scrollpane_selectedFiles = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    /** The button to open the handler selection dialog. */
    private final VButton button_addFiles = new VButton("Add File(s)", "Select and add the files you wish to encode.");
    /** The button to remove all files that are currently selected on the scrollpane_selectedFiles. */
    private final VButton button_removeSelectedFiles = new VButton("Remove File(s)", "Removes all files that are currently selected on the below list.");
    /** The button to remove all files from the list. */
    private final VButton button_clearAllFiles = new VButton("Clear List", "Clears the list of all files.");

    /** The button to clear the output screen. */
    private final VButton button_clearOutput = new VButton("Clear Output", "Clears the output screen.");

    /** The button to open the settings dialog. */
    private final VButton button_editSettings = new VButton("Edit Settings", "Open the settings menu.");

    /** The button to encode the currently selected handler(s). */
    private final VButton button_encode = new VButton("Encode", "Encodes the selected handler(s).");
    /** The button to decode the currently selected handler(s). */
    private final VButton button_decode = new VButton("Decode", "Decodes the selected handler(s).</br></br>No checking is done to see if the files have ever been encoded,</br>so it's up to you to ensure you're decoding the correct files.");

    /**
     * Constructs a new main screen view.
     * @param controller The object that handles settings for encoding, decoding, compression, and a number of other features.
     * @param model todo JavaDoc
     */
    public MainScreenView(final MainScreenController controller, final MainScreenModel model) {
        addFilesToCurrentlySelectedFiles(model);

        list_selectedFiles.setFocusable(true);
        list_selectedFiles.setDragEnabled(true);

        textArea_ffmpegOutput.setEditable(false);
        textArea_ffmpegOutput.setWrapStyleWord(false);
        textArea_ffmpegOutput.setLineWrap(false);
        textArea_ffmpegOutput.setAutoscrolls(true);

        // Set Component Colors:
        scrollpane_selectedFiles.setBorder(VComponentGlobals.BORDER_NORMAL);

        // Set Component Listeners:
        button_addFiles.addActionListener(controller);
        button_encode.addActionListener(controller);
        button_decode.addActionListener(controller);
        button_removeSelectedFiles.addActionListener(controller);
        button_clearAllFiles.addActionListener(controller);
        button_clearOutput.addActionListener(controller);
        button_editSettings.addActionListener(controller);

        // Setup the Layout:
        final VPanel panel_left_buttons = new VPanel();
        panel_left_buttons.setLayout(new MigLayout("align center center, gapy 4"));
        panel_left_buttons.add(button_encode);
        panel_left_buttons.add(button_decode);

        final VPanel panel_left_bottom = new VPanel();
        panel_left_bottom.setLayout(new MigLayout("align center center, gapy 4"));
        panel_left_bottom.add(panel_left_buttons, "dock north");

        final VPanel panel_left_center = new VPanel();
        panel_left_center.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_left_center.add(scrollpane_selectedFiles, "growx, growy");

        final VPanel panel_left_top = new VPanel();
        panel_left_top.setLayout(new MigLayout("align center center, gapy 4"));
        panel_left_top.add(button_addFiles);
        panel_left_top.add(button_removeSelectedFiles);
        panel_left_top.add(button_clearAllFiles);

        final VPanel panel_left = new VPanel();
        panel_left.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_left.add(panel_left_top, "dock north");
        panel_left.add(panel_left_center, "growx, growy");
        panel_left.add(panel_left_bottom, "dock south");

        final VPanel panel_right_top = new VPanel();
        panel_right_top.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_right_top.add(textArea_ffmpegOutput.setupScrolling(true, true), "growx, growy");

        final VPanel panel_right_bottom_left = new VPanel();
        panel_right_bottom_left.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_right_bottom_left.add(button_clearOutput);

        final VPanel panel_right_bottom_right = new VPanel();
        panel_right_bottom_right.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_right_bottom_right.add(button_editSettings);

        final VPanel panel_right_bottom = new VPanel();
        panel_right_bottom.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_right_bottom.add(panel_right_bottom_left, "dock west");
        panel_right_bottom.add(panel_right_bottom_right, "dock east");

        final VPanel panel_right = new VPanel();
        panel_right.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_right.add(panel_right_top, "growx, growy");
        panel_right.add(panel_right_bottom, "dock south");

        this.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        this.add(panel_left, "growx, growy, w 30%");
        this.add(panel_right, "growx, growy, w 70%");
    }

    /**
     * Creates a new scrollpane filled with the names of every handler to be encoded.
     * @param model The main screen's model.
     */
    public void addFilesToCurrentlySelectedFiles(final MainScreenModel model) {
        // Initalize and populate the list:
        for(final File f : model.getSelectedFiles()) {
            listModel_selectedFiles.addElement(f.getName());
        }

        // Add the list:
        list_selectedFiles = new JList<>(listModel_selectedFiles);
        scrollpane_selectedFiles.getViewport().setView(list_selectedFiles);
    }

    /**
     * Clears the list of currently selected files.
     */
    public void clearCurrentlySelectedFile() {
        listModel_selectedFiles.removeAllElements();

        // Add the list:
        list_selectedFiles = new JList<>(listModel_selectedFiles);
        scrollpane_selectedFiles.getViewport().setView(list_selectedFiles);
    }

    /**
     * Removes the currently selected indices from the list.
     */
    public void removeCurrentlySelectedFiles() {
        // todo Rethink and refactor this method so that you don't need to use listModel_new.
        DefaultListModel<String> listModel_new = new DefaultListModel<>();
        int[] selectedIndices = list_selectedFiles.getSelectedIndices();

        for(int i = 0 ; i < listModel_selectedFiles.size() ; i++) {
            boolean addElement = true;

            // Check if the current element is one of the elements to be removed.
            for(int j = 0 ; j < selectedIndices.length ; j++) {
                if(i == selectedIndices[j]) {
                    addElement = false;
                }
            }

            if(addElement) {
                listModel_new.addElement(listModel_selectedFiles.getElementAt(i));
            }
        }

        // Add the list:
        listModel_selectedFiles = listModel_new;
        list_selectedFiles = new JList<>(listModel_selectedFiles);
        scrollpane_selectedFiles.getViewport().setView(list_selectedFiles);
    }

    /** @return Whether or not there are selected files in the list. */
    public boolean areThereSelectedFiles() {
        return listModel_selectedFiles.size() > 0;
    }

    /**
     * Disables all of the buttons on the GUI.
     * This should be called before encoding or decoding begins.
     */
    public void disableButtons() {
        button_addFiles.setEnabled(false);
        button_encode.setEnabled(false);
        button_decode.setEnabled(false);
        button_removeSelectedFiles.setEnabled(false);
        button_clearAllFiles.setEnabled(false);
        button_clearOutput.setEnabled(false);
        button_editSettings.setEnabled(false);

        button_addFiles.setFocusable(false);
        button_encode.setFocusable(false);
        button_decode.setFocusable(false);
        button_removeSelectedFiles.setFocusable(false);
        button_clearAllFiles.setFocusable(false);
        button_clearOutput.setFocusable(false);
        button_editSettings.setFocusable(false);
    }

    /**
     * Enables all of the buttons on the GUI.
     * This should be called after encoding or decoding has finished.
     */
    public void enableButtons() {
        button_addFiles.setEnabled(true);
        button_encode.setEnabled(true);
        button_decode.setEnabled(true);
        button_removeSelectedFiles.setEnabled(true);
        button_clearAllFiles.setEnabled(true);
        button_clearOutput.setEnabled(true);
        button_editSettings.setEnabled(true);

        button_addFiles.setFocusable(true);
        button_encode.setFocusable(true);
        button_decode.setFocusable(true);
        button_removeSelectedFiles.setFocusable(true);
        button_clearAllFiles.setFocusable(true);
        button_clearOutput.setFocusable(true);
        button_editSettings.setFocusable(true);
    }

    ////////////////////////////////////////////////////////// Getters

    /** @return The text area for the ouput of FFMPEG from the most recent encode or decode job. */
    public VTextArea getTextArea_ffmpegOutput() {
        return textArea_ffmpegOutput;
    }

    /** @return The button to open the handler selection dialog. */
    public VButton getButton_addFiles() {
        return button_addFiles;
    }

    /** @return The button to encode the currently selected handler(s). */
    public VButton getButton_encode() {
        return button_encode;
    }

    /** @return The button to decode the currently selected handler(s). */
    public VButton getButton_decode() {
        return button_decode;
    }

    /** @return The button to remove all files that are currently selected on the scrollpane_selectedFiles. */
    public VButton getButton_removeSelectedFiles() {
        return button_removeSelectedFiles;
    }

    /** @return The button to remove all files from the list. */
    public VButton getButton_clearAllFiles() {
        return button_clearAllFiles;
    }

    /** @return The button to clearn the output screen. */
    public VButton getButton_clearOutput() {
        return button_clearOutput;
    }

    /** @return The button to open the settings dialog. */
    public VButton getButton_editSettings() {
        return button_editSettings;
    }
}
