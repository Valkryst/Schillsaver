package gui;

import handler.ConfigHandler;
import handler.FFMPEGHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainScreenController implements ActionListener {
    /** The frame in which to place the view is placed. */
    private final JFrame frame;

    /** The main screen's view. */
    private final MainScreenView view;
    /** The main screen's model. */
    private final MainScreenModel model;

    /** The object that handles settings for encoding, decoding, compression, and a number of other features. */
    private final ConfigHandler configHandler;

    /**
     * Construct a new main screen controller.
     * @param frame The frame in which to place the main screen view.
     * @param configHandler The object that handles settings for encoding, decoding, compression, and a number of other features.
     */
    public MainScreenController(final JFrame frame, final ConfigHandler configHandler) {
        this.frame = frame;
        this.configHandler = configHandler;

        model = new MainScreenModel();
        view = new MainScreenView(this, model);

        this.frame.setMinimumSize(view.MINIMUM_FRAME_SIZE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();

        // The button to open the handler selection dialog.
        if(source.equals(view.getButton_addFiles())) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDragEnabled(true);
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            fileChooser.setDialogTitle("File Slection");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            fileChooser.setApproveButtonText("Accept");
            fileChooser.setApproveButtonToolTipText("Accept the selected files and load them into the program.");

            int returnVal = fileChooser.showOpenDialog(frame);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                model.setSelectedFiles(fileChooser.getSelectedFiles());
                view.addFilesToCurrentlySelectedFiles(model);
            }
        }

        // The button to encode the currently selected handler(s).
        if(source.equals(view.getButton_encode())) {
            // Only allow files to be encoded if there are actually
            // files in the list of files.
            if(view.areThereSelectedFiles()) {
                final EncodePopupController popup = new EncodePopupController(frame, configHandler);

                if(popup.getView().getChoicesAccepted()) {
                    // Disable buttons while encoding:
                    view.disableButtons();

                    // Encode:
                    final MainScreenController temp = this;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FFMPEGHandler ffmpegHandler = new FFMPEGHandler();
                            ffmpegHandler.encodeVideoToDisk(model.getSelectedFiles(), temp, configHandler);
                        }
                    }).start();
                }
            }
        }

        // The button to decode the currently selected handler(s).
        if(source.equals(view.getButton_decode())) {
            // Only allow files to be decoded if there are actually
            // files in the list of files.
            if(view.areThereSelectedFiles()) {
                // Disable buttons while decoding:
                view.disableButtons();

                // Decode:
                final MainScreenController temp = this;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FFMPEGHandler ffmpegHandler = new FFMPEGHandler();
                        ffmpegHandler.decodeVideo(model.getSelectedFiles(), temp, configHandler);
                    }
                }).start();
            }
        }

        // The button to remove all files that are currently selected on the scrollpane_selectedFiles.
        if(source.equals(view.getButton_removeSelectedFiles())) {
            view.removeCurrentlySelectedFiles();
        }

        // The button to remove all files from the list.
        if(source.equals(view.getButton_clearAllFiles())) {
            view.clearCurrentlySelectedFile();
        }

        // The button to clear the output screen.
        if(source.equals(view.getButton_clearOutput())) {
            view.getTextArea_ffmpegOutput().setText("");
        }

        // The button to open the settings dialog.
        if(source.equals(view.getButton_editSettings())) {
            new SettingsScreenController(frame, configHandler);
        }
    }

    /** @return The main screen's view. */
    public MainScreenView getView() {
        return view;
    }
}
