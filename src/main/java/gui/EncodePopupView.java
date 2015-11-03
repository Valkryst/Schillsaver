package gui;

import component.*;
import file.ConfigHandler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class EncodePopupView extends JDialog {
    /** The button group of the "archive all files into a single archive before encoding" yes/no radio buttons. */
    private final ButtonGroup buttonGroup_singleArchive = new ButtonGroup();
    /** The radio button that says that each of the currently selected files should be archived as a single archive before encoding. */
    private final VRadioButton radioButton_singleArchive_yes = new VRadioButton("Yes");
    /** The radio button that says that each of the currently selected files should be archived individually before encoding them individually. */
    private final VRadioButton radioButton_singleArchive_no = new VRadioButton("No");

    /** The button group of the "archive just this file before encoding" yes/no radio buttons. */
    private final ButtonGroup buttonGroup_individualArchives = new ButtonGroup();
    /** The radio button that says that each file should be archived individually before encoding each of them individually. */
    private final VRadioButton radioButton_individualArchives_yes = new VRadioButton("Yes");
    /** The radio button that says that each file should not be archived individually before encoding each of them individually. */
    private final VRadioButton radioButton_individualArchives_no = new VRadioButton("No");

    /** The text field for the name of the resulting archive/encoded file to use if the "Yes" radio button is selected. */
    private final VTextField textField_archiveName = new VTextField(64, "Enter a name for the encoded file.", "The name to give the encoded file. Do not include an extension.");
    /** The button to select the folder to use when archiving/encoding the all-in-one archive if the "Yes" radio button is selected. */
    private final VButton button_selectOutputDirectory = new VButton("Select Output Folder", "Select the folder to output the all-in-one archive to.");

    /** The button to accept the choices on the screen and close the dialog. */
    private final VButton button_accept = new VButton("Accept", "Begin archiving and encoding the file(s).");
    /** The button to cancel the choices on the screen and close the dialog. */
    private final VButton button_cancel = new VButton("Cancel", "Close this window.");

    /** The name of the archive/encoded file to use if the "Yes" radio button is selected. */
    private String archiveName;
    /** The path of the archive/encoded file to use if the "Yes" radio button is selected. */
    private String archivePath;

    /** Whether or not the dialog was closed by the Accept button. */
    private boolean choicesAccepted = false;

    // todo JavaDoc
    public EncodePopupView(final JFrame frame, final EncodePopupController controller, final ConfigHandler configHandler) {
        super(frame, "Encode Settings", true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);

        textField_archiveName.setEditable(false);
        textField_archiveName.setForeground(VComponentGlobals.FOREGROUND_COLOR);
        textField_archiveName.setFocusable(false);

        // Setup Radio Buttons:
        radioButton_singleArchive_no.setSelected(true);

        buttonGroup_singleArchive.add(radioButton_singleArchive_yes);
        buttonGroup_singleArchive.add(radioButton_singleArchive_no);


        radioButton_individualArchives_no.setSelected(true);

        buttonGroup_individualArchives.add(radioButton_individualArchives_yes);
        buttonGroup_individualArchives.add(radioButton_individualArchives_no);

        // Setup Listeners:
        radioButton_singleArchive_yes.addActionListener(controller);
        radioButton_singleArchive_no.addActionListener(controller);
        radioButton_individualArchives_yes.addActionListener(controller);
        radioButton_individualArchives_no.addActionListener(controller);
        button_selectOutputDirectory.addActionListener(controller);
        button_accept.addActionListener(controller);
        button_cancel.addActionListener(controller);

        // Setup the Layout:
        final VSection section_top_top_top = new VSection();
        section_top_top_top.setSectionTitle("Archive all files in list to single archive before encoding?");
        section_top_top_top.setLayout(new MigLayout("align center center, gapy 4"));
        section_top_top_top.add(radioButton_singleArchive_yes);
        section_top_top_top.add(radioButton_singleArchive_no);

        final VSection section_top_top_bottom = new VSection();
        section_top_top_bottom.setSectionTitle("Archive each files in list to individual archives before encoding?");
        section_top_top_bottom.setLayout(new MigLayout("align center center, gapy 4"));
        section_top_top_bottom.add(radioButton_individualArchives_yes);
        section_top_top_bottom.add(radioButton_individualArchives_no);

        final VPanel panel_top_top = new VPanel();
        panel_top_top.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_top_top.add(section_top_top_top, "dock north");
        panel_top_top.add(section_top_top_bottom, "dock south");

        final VPanel panel_top_bottom = new VPanel();
        panel_top_bottom.setLayout(new MigLayout("align center center, gapy 4"));
        panel_top_bottom.add(textField_archiveName);
        panel_top_bottom.add(button_selectOutputDirectory);

        final VPanel panel_top = new VPanel();
        panel_top.setLayout(new MigLayout("fill, gapy 4, gapx 4"));
        panel_top.add(panel_top_top, "dock north");
        panel_top.add(panel_top_bottom, "dock south");

        final VPanel panel_bottom = new VPanel();
        panel_bottom.setLayout(new MigLayout("align center center, gapy 4"));
        panel_bottom.add(button_accept);
        panel_bottom.add(button_cancel);

        this.add(panel_top, BorderLayout.NORTH);
        this.add(panel_bottom, BorderLayout.SOUTH);
    }

    /** @return Whether or not the dialog was closed by the Accept button. */
    public boolean getChoicesAccepted(){
        return choicesAccepted;
    }

    ////////////////////////////////////////////////////////// Getters

    /** @return The radio button that says that each of the currently selected files should be archived as a single archive before encoding. */
    public VRadioButton getRadioButton_singleArchive_yes() {
        return radioButton_singleArchive_yes;
    }

    /** @return The radio button that says that each of the currently selected files should be archived individually before encoding them individually. */
    public VRadioButton getRadioButton_singleArchive_no() {
        return radioButton_singleArchive_no;
    }

    /** @return The radio button that says that each file should be archived individually before encoding each of them individually. */
    public VRadioButton getRadioButton_individualArchives_yes() {
        return radioButton_individualArchives_yes;
    }

    /** @return The radio button that says that each file should not be archived individually before encoding each of them individually. */
    public VRadioButton getRadioButton_individualArchives_no() {
        return radioButton_individualArchives_no;
    }

    /** @return The text field for the name of the resulting archive/encoded file to use if the "Yes" radio button is selected. */
    public VTextField getTextField_archiveName() {
        return textField_archiveName;
    }

    /** @return The button to accept the choices on the screen and close the dialog. */
    public VButton getButton_selectOutputDirectory() {
        return button_selectOutputDirectory;
    }

    /** @return The button to cancel the choices on the screen and close the dialog. */
    public VButton getButton_accept() {
        return button_accept;
    }

    /** @return The button to select the folder to use when archiving/encoding the all-in-one archive if the "Yes" radio button is selected. */
    public VButton getButton_cancel() {
        return button_cancel;
    }

    ////////////////////////////////////////////////////////// Setters

    // todo JavaDoc
    public void setChoicesAccepted(final boolean choicesAccepted) {
        this.choicesAccepted = choicesAccepted;
    }
}
