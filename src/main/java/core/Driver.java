package core;

import controller.MainScreenController;
import eu.hansolo.enzo.notification.Notification;
import handler.ConfigHandler;
import handler.StatisticsHandler;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Driver extends Application {
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

        // Setup the primary stage:
        primaryStage.getIcons().add(new Image("icon.png"));

        // Setup Enzo:
        Notification.Notifier.setPopupLocation(primaryStage, Pos.BOTTOM_CENTER);

        // Add the frst scene to the primary stage:
        final Scene scene = new Scene(new MainScreenController(primaryStage, configHandler, statisticsHandler).getView());

        scene.getStylesheets().add("global.css");
        scene.getRoot().getStyleClass().add("main-root");

        primaryStage.setTitle("Schillsaver - Powered by /g/entoomen\u00a9\u00ae");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
