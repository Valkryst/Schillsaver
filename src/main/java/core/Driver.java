package core;

import controller.MainScreenController;
import handler.ConfigHandler;
import handler.StatisticsHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Driver extends Application{
    /** The current version of the program. Whenever a significant change is made, this should be changed along with the online handler. */
    private static final String PROGRAM_VERSION = "9";

    /** The logger used throughout the entire program whenever a log is to be written. */
    public static final Logger LOGGER = new Logger();;


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
        LOGGER.run();
    }

    /**
     * Checks the website to see if there is a new version of the program.
     * If there is a new version, then a dialog is shown to the user to explain
     * the situation and how to update.
     */
    public static void checkForUpdate() {
        try {
            final URL url = new URL("https://valkryst.com/schillsaver/version.txt");
            Scanner scanner = new Scanner(url.openStream());
            final String newVersion = scanner.nextLine();
            scanner.close();

            System.out.println(newVersion);

            if(!newVersion.equals(PROGRAM_VERSION)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("New Version Available");
                alert.setHeaderText("This program is out of date.");
                alert.setContentText("Get the latest version at http://valkryst.com/schillsaver/\n\n" +
                        "Current Version - " + PROGRAM_VERSION + "\n" +
                        "New Version - " + newVersion);
                alert.showAndWait();
            }
        }
        catch(IOException e) {
            LOGGER.addLog(Log.LOGTYPE_WARNING, e);
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
                LOGGER.addLog(Log.LOGTYPE_WARNING, e);
            }
        }
    }
}
