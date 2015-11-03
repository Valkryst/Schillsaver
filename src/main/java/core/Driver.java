package core;

import component.VComponentGlobals;
import file.ConfigHandler;
import gui.MainScreenController;
import gui.SettingsScreenController;
import misc.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

// todo Refactor this entire class. It's a trainwreck.
public class Driver {
    /** The current version of the program. Whenever a significant change is made, this should be changed along with the online file. */
    private static final String PROGRAM_VERSION = "3";

    public static void main(final String[] args) {
        // Check for new version:
        checkForUpdate();

        // Load config options or create new config file if none exists.
        final ConfigHandler configHandler = new ConfigHandler();

        if(configHandler.doesConfigFileExist()) {
            configHandler.loadConfigSettings();
        } else {
            // Run a search to find some of the required program paths
            // so the user doesn't have to do it themselves.
            // Then open the Settings Manager.
            configHandler.searchForDefaultProgramPaths();
            new SettingsScreenController(null, configHandler);
        }

        showSplashscreen(configHandler);

        startProgram(configHandler);
    }

    /**
     * Checks the website to see if there is a new version of the program.
     * If there is a new version, then a dialog is shown to the user to explain
     * the situation and how to update.
     */
    public static void checkForUpdate() {
        try {
            final URL url = new URL("http://valkryst.com/schillsaver/version.txt");
            Scanner scanner = new Scanner(url.openStream());
            final String newVersion = scanner.nextLine();
            scanner.close();

            if(!newVersion.equals(PROGRAM_VERSION)) {
                JOptionPane.showMessageDialog(null, "This program is out of date.\n" +
                                                    "Get the latest version at http://valkryst.com/schillsaver/Schillsaver.7z.\n\n" +
                                                    "Current Version - " + PROGRAM_VERSION + "\n" +
                                                    "New Version - " + newVersion,
                                                    "New Version Available",
                                                    JOptionPane.WARNING_MESSAGE);
            }
        }
        catch(IOException e) {
            Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_WARNING);
        }
    }

    /**
     * Show the splashscreen if it's enabled in the configuration settings
     * and if the splashscreen image can be found.
     * @param configHandler todo Javadoc
     */
    public static void showSplashscreen(final ConfigHandler configHandler) {
        if(configHandler.getShowSplashScreen()) {
            try {
                final ImageIcon image = new ImageIcon(configHandler.getSplashScreenFilePath());
                final JWindow window = new JWindow();
                window.getContentPane().add(new JLabel("", image, SwingConstants.CENTER));
                window.pack();
                window.setLocationRelativeTo(null);
                window.setVisible(true);

                Thread.sleep(configHandler.getSplashScreenDisplayTime());

                window.setVisible(false);
                window.dispose();
            } catch(final InterruptedException | NullPointerException e) {
                Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_WARNING);
            }
        }
    }

    /**
     * Creates a new frame with the MainScreenView showing.
     * @param configHandler todo JavaDoc
     */
    public static void startProgram(final ConfigHandler configHandler) {
        final JFrame frame = new JFrame();
        frame.setTitle("Schillsaver - Powered by /g/entoomen\u00a9\u00ae");
        frame.setBackground(VComponentGlobals.BACKGROUND_COLOR);
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("icon.png")));

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                final Dimension currentDimensions = frame.getSize();
                final Dimension minimumDimensions = frame.getMinimumSize();

                if(currentDimensions.width < minimumDimensions.width) {
                    currentDimensions.width = minimumDimensions.width;
                }

                if(currentDimensions.height < minimumDimensions.height) {
                    currentDimensions.height = minimumDimensions.height;
                }

                frame.setSize(currentDimensions);
            }
        });

        frame.add(new MainScreenController(frame, configHandler).getView());
        frame.setResizable(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
