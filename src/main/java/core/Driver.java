package core;

import controller.MainScreenController;
import handler.ConfigHandler;
import handler.StatisticsHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import misc.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Driver extends Application{
    /** The current version of the program. Whenever a significant change is made, this should be changed along with the online handler. */
    private static final String PROGRAM_VERSION = "5";


    public static void main(final String[] args) {
        launch();
    }

    @Override
    public void init() {
        // Do something before the application starts.
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final ConfigHandler configHandler = new ConfigHandler();
        final StatisticsHandler statisticsHandler = new StatisticsHandler();

        // Load Config File:
        configHandler.loadConfigSettings();

        // Check for Updates:
        if(configHandler.getCheckForUpdatesOnStart()) {
            checkForUpdate();
        }

        // Show Splash Screen:
        if(configHandler.getShowSplashScreen()) {
            showSplashscreen(configHandler);
        }

        // Setup the primary stage:
        primaryStage.getIcons().add(new Image("icon.png"));

        // Add the frst scene to the primary stage:
        final Scene scene = new Scene(new MainScreenController(primaryStage, configHandler, statisticsHandler).getView());

        scene.getStylesheets().add("global.css");
        scene.getRoot().getStyleClass().add("main-root");

        primaryStage.setTitle("Schillsaver - Powered by /g/entoomen\u00a9\u00ae");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Do something before the application stops.
    }

    /**
     * Checks the website to see if there is a new version of the program.
     * If there is a new version, then a dialog is shown to the user to explain
     * the situation and how to update.
     */
    public static void checkForUpdate() {
        // todo Refactor to use JavaFX instead of Swing.
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
}
