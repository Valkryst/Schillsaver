package core;

import configuration.Settings;
import controller.Controller;
import controller.MainController;
import eu.hansolo.enzo.notification.Notification;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;

public class Driver extends Application {
    /** The primary stage. */
    @Getter private Stage primaryStage;

    /** The previous scene's controller. */
    @Getter private Controller previousController;
    /** The current scene's controller. */
    @Getter private Controller currentController;

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
        currentController = new MainController(this);
        currentScene = new Scene(currentController.getView().getPane());
        addStylesheet(currentScene);

        primaryStage.setTitle("Schillsaver - Powered by /g/entoomen\u00a9\u00ae");
        primaryStage.setScene(currentScene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        MainController mainController = null;

        if (previousController instanceof MainController) {
            mainController = (MainController) previousController;
        }

        if (currentController instanceof MainController) {
            mainController = (MainController) currentController;
        }

        if (mainController != null) {
            mainController.saveJobsToFile();
        }
    }

    /**
     * Swaps the current scene with a new scene, so that the new scene
     * is displayed.
     *
     * @param controller
     *          The controller of the new scene.
     */
    public void swapToNewScene(final Controller controller) {
        previousController = currentController;
        currentController = controller;

        previousScene = currentScene;
        currentScene = new Scene(controller.getView().getPane());

        addStylesheet(currentScene);

        // Set new scene's pane to be the same size as the previous
        // pane's scene.
        final Pane previousPane = previousController.getView().getPane();
        final Pane currentPane = currentController.getView().getPane();

        currentPane.setPrefSize(previousPane.getWidth(), previousPane.getHeight());

        primaryStage.setScene(currentScene);
    }

    /**
     * Swaps the current and previous scenes, so that the previous scene
     * is displayed.
     */
    public void swapToPreviousScene() {
        final Controller tempC = previousController;
        previousController = currentController;
        currentController = tempC;

        final Scene tempS = previousScene;
        previousScene = currentScene;
        currentScene = tempS;

        // Set new scene's pane to be the same size as the previous
        // pane's scene.
        final Pane previousPane = previousController.getView().getPane();
        final Pane currentPane = currentController.getView().getPane();

        currentPane.setPrefSize(previousPane.getWidth(), previousPane.getHeight());

        primaryStage.setScene(currentScene);
    }

    /**
     * Adds the global stylesheet to a scene.
     *
     * @param scene
     *          The scene.
     */
    private void addStylesheet(final Scene scene) {
        if (scene.getStylesheets().size() == 0) {
            scene.getStylesheets().add("global.css");
            scene.getRoot().getStyleClass().add("main-root");
        }
    }
}
