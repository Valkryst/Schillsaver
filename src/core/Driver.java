package core;

import configuration.Settings;
import controller.MainScreenController;
import eu.hansolo.enzo.notification.Notification;
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
    public void init() {}

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load Config File:
        final Settings settings = new Settings();
        settings.loadFromFile();

        // Setup the primary stage:
        primaryStage.getIcons().add(new Image("icon.png"));

        // Setup Enzo:
        Notification.Notifier.setPopupLocation(primaryStage, Pos.BOTTOM_CENTER);

        // Add the first scene to the primary stage:
        final Scene scene = new Scene(new MainScreenController(primaryStage, settings).getView());

        scene.getStylesheets().add("global.css");
        scene.getRoot().getStyleClass().add("main-root");

        primaryStage.setTitle("Schillsaver - Powered by /g/entoomen\u00a9\u00ae");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
