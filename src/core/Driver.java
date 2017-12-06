package core;

import configuration.Settings;
import controller.MainController;
import eu.hansolo.enzo.notification.Notification;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Driver extends Application {
    /** The primary stage. */
    private Stage primaryStage;

    /** The previous scene. */
    private Scene previousScene;
    /** The current scene. */
    private Scene currentScene;

    public static void main(final String[] args) {
        launch();
    }

    @Override
    public void init() {}

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Load Config File:
        final Settings settings = new Settings();
        settings.loadFromFile();

        // Setup the primary stage:
        primaryStage.getIcons().add(new Image("icon.png"));

        // Setup Enzo:
        Notification.Notifier.setPopupLocation(primaryStage, Pos.BOTTOM_CENTER);

        // Add the first scene to the primary stage:
        final Scene scene = new Scene(new MainController(primaryStage).getView());

        primaryStage.setTitle("Schillsaver - Powered by /g/entoomen\u00a9\u00ae");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Swaps the current scene with a new scene, so that the new scene
     * is displayed.
     *
     * @param newScene
     *          The new scene.
     */
    public void swapToNewScene(final Scene newScene) {
        previousScene = currentScene;
        currentScene = newScene;

        if (currentScene.getStylesheets().size() == 0) {
            newScene.getStylesheets().add("global.css");
            newScene.getRoot().getStyleClass().add("main-root");
        }

        primaryStage.setScene(currentScene);
    }

    /**
     * Swaps the current and previous scenes, so that the previous scene
     * is displayed.
     */
    public void swapToPreviousScene() {
        final Scene temp = previousScene;
        previousScene = currentScene;
        currentScene = temp;

        primaryStage.setScene(currentScene);
    }
}
