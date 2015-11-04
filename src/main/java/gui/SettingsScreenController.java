package gui;


import handler.ConfigHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

public class SettingsScreenController implements ActionListener, WindowListener {
    /** The frame in which to place the view is placed. */
    private final JFrame frame;

    /** The settings screen's view. */
    private final SettingsScreenView view;

    // todo JavaDoc
    private final ConfigHandler configHandler;

    /**
     * Construct a new settings screen controller.
     *
     * Assumes that, if frame is null, the config handler does not yet exist
     * and that the SettingsScreen is being opened for initial setup.
     * Under this circumstance, the program will be forcibly closed if
     * the user does not click the "Accept" button on the view.
     *
     * @param frame The frame in which to place the settings screen view.
     * @param configHandler todo Javadoc
     */
    public SettingsScreenController(final JFrame frame, final ConfigHandler configHandler) {
        this.frame = frame;
        this.configHandler = configHandler;

        // todo Reorganize the code below. It's a mess.

        view = new SettingsScreenView(this, configHandler);

        view.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("icon.png")));

        view.setPreferredSize(view.PREFERRED_FRAME_SIZE);

        // Set location on screen:
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int height = (screenSize.height/2) - (view.PREFERRED_FRAME_SIZE.height/2);
        final int width = (screenSize.width/2) - (view.PREFERRED_FRAME_SIZE.width/2);

        view.setLocation(width, height);

        view.setTitle("Settings Configuration");

        view.setModal(true);

        view.pack();
        view.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();

        if(source.equals(view.getButton_selectFile_ffmpegPath())) { // The button to select the path of FFMPEG.
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDragEnabled(false);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            fileChooser.setDialogTitle("Directory Slection");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            fileChooser.setApproveButtonText("Accept");
            fileChooser.setApproveButtonToolTipText("Accept the selected directory.");

            int returnVal = fileChooser.showOpenDialog(view);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                view.getField_ffmpegPath().setText(fileChooser.getSelectedFile().getPath());
            }
        }

        // The button to select the path of the archival program.
        if(source.equals(view.getButton_selectFile_compressionProgramPath())) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDragEnabled(false);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            fileChooser.setDialogTitle("Directory Slection");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            fileChooser.setApproveButtonText("Accept");
            fileChooser.setApproveButtonToolTipText("Accept the selected directory.");

            int returnVal = fileChooser.showOpenDialog(view);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                view.getField_compressionProgramPath().setText(fileChooser.getSelectedFile().getPath());;
            }
        }

        // The button to select the path of the splash screen image.
        if(source.equals(view.getButton_selectFile_splashScreenFilePath())) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDragEnabled(false);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", ImageIO.getReaderFileSuffixes()));

            fileChooser.setDialogTitle("Directory Slection");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            fileChooser.setApproveButtonText("Accept");
            fileChooser.setApproveButtonToolTipText("Accept the selected directory.");

            int returnVal = fileChooser.showOpenDialog(view);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                view.getField_splashScreenFilePath().setText(fileChooser.getSelectedFile().getPath());
            }
        }

        if(source.equals(view.getButton_accept())) { // The button to close the settings menu while saving.
            if(view.saveSettings(configHandler)) {
                view.setVisible(false);
                view.dispose();
            }
        }

        if(source.equals(view.getButton_cancel())) { // The button to close the settings menu without saving.
            if(frame == null) {
                view.setVisible(false);
                view.dispose();
            } else {
                view.setVisible(false);
                view.dispose();
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    // Invoked when the user attempts to close the window from the window's system menu.
    @Override
    public void windowClosing(WindowEvent e) {
        if(frame == null) {
            System.exit(0);
        } else {
            view.setVisible(false);
            view.dispose();
        }
    }

    // Invoked when a window has been closed as the result of calling dispose on the window.
    @Override
    public void windowClosed(WindowEvent e) {
        if(frame == null) {
            view.setVisible(false);
            view.dispose();
        } else {
            view.setVisible(false);
            view.dispose();
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    /** @return The main screen's view. */
    public SettingsScreenView getView() {
        return view;
    }
}
