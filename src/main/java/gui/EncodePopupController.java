package gui;

import component.VComponentGlobals;
import file.ConfigHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class EncodePopupController implements ActionListener {
    /** The frame from which the dialog is displayed */
    private final JFrame frame;

    /** The encode popup screen's view. */
    private final EncodePopupView view;

    /** The object that handles settings for encoding, decoding, compression, and a number of other features. */
    private final ConfigHandler configHandler;

    /**
     * Construct a new encode popup screen controller.
     * @param frame The frame from which the dialog is displayed.
     * @param configHandler The object that handles settings for encoding, decoding, compression, and a number of other features.
     */
    public EncodePopupController(final JFrame frame, final ConfigHandler configHandler) {
        this.frame = frame;
        this.configHandler = configHandler;

        view = new EncodePopupView(frame, this, configHandler);

        view.setLocationRelativeTo(frame);

        view.pack();
        view.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();

        if(source.equals(view.getRadioButton_singleArchive_yes())) {
            // Ensure that both the single and individual archive options aren't
            // selected at the same time.
            configHandler.setCombineIntoIndividualArchives(false);
            view.getRadioButton_individualArchives_no().setSelected(true);

            // Allow the user to edit the archive name/path.
            configHandler.setCombineAllFilesIntoSingleArchive(true);
            view.getTextField_archiveName().setEditable(true);
            view.getTextField_archiveName().setForeground(VComponentGlobals.TEXT_COLOR);
            view.getTextField_archiveName().setFocusable(true);
        }

        if(source.equals(view.getRadioButton_singleArchive_no())) {
            view.getTextField_archiveName().setEditable(false);
            view.getTextField_archiveName().setForeground(VComponentGlobals.FOREGROUND_COLOR);
            view.getTextField_archiveName().setFocusable(false);
            configHandler.setCombineAllFilesIntoSingleArchive(false);
        }

        if(source.equals(view.getRadioButton_individualArchives_yes())) {
            // Ensure that both the single and individual archive options aren't
            // selected at the same time.
            configHandler.setCombineIntoIndividualArchives(true);
            view.getRadioButton_singleArchive_no().setSelected(true);

            // Allow the user to edit the archive name/path.
            configHandler.setCombineAllFilesIntoSingleArchive(false);
        }

        if(source.equals(view.getRadioButton_singleArchive_no())) {
            configHandler.setCombineIntoIndividualArchives(false);
        }

        if(source.equals(view.getButton_selectOutputDirectory())) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDragEnabled(false);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            fileChooser.setDialogTitle("Directory Slection");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            fileChooser.setApproveButtonText("Accept");
            fileChooser.setApproveButtonToolTipText("Accept the selected directory.");

            int returnVal = fileChooser.showOpenDialog(frame);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                configHandler.setEncodedFilePath(fileChooser.getSelectedFile().getPath() + "/");
            }
        }

        if(source.equals(view.getButton_accept())) {

            configHandler.setEncodedFilePath(configHandler.getEncodedFilePath() + view.getTextField_archiveName().getText());

            view.setChoicesAccepted(true);
            view.setVisible(false);
            view.dispose();
        }

        if(source.equals(view.getButton_cancel())) {
            view.setVisible(false);
            view.dispose();
        }
    }

    /** @return The encode popup screen's view. */
    public EncodePopupView getView() {
        return view;
    }
}
