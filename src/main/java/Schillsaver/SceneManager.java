package Schillsaver;

import Schillsaver.mvc.controller.Controller;
import Schillsaver.mvc.controller.MainController;
import Schillsaver.mvc.model.MainModel;
import Schillsaver.mvc.view.MainView;
import Schillsaver.setting.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;

public class SceneManager extends Application {
    /** The singleton instance. */
    public static SceneManager INSTANCE;

    /** The stage. */
    @Getter private Stage stage;

    /** The previous scene's controller. */
    @Getter private Controller previousController;
    /** The current scene's controller. */
    @Getter private Controller currentController;

    /** The previous scene. */
    private Scene previousScene;
    /** The current scene. */
    private Scene currentScene;

    /**
     * Constructs a new SceneManager.
     *
     * @throws IllegalAccessException
     *          If this function is called at any point after "Application.launch(SceneManager.class);" starts
     *          the application.
     */
    public SceneManager() throws IllegalAccessException {
        if (INSTANCE != null) {
            throw new IllegalAccessException("You cannot construct an instance of the SceneManager class. Please use the getInstance() function.");
        }
    }

    @Override
    public void start(final Stage stage) {
        stage.getIcons().add(new Image("icons/Icon.png"));
        stage.setTitle("Schillsaver - Powered by /g/entoomen\u00a9\u00ae");
        stage.setMinWidth(512);
        stage.setMinHeight(512);
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        // Handle FFMPEG executable selection:
        final Settings settings = Settings.getInstance();

        if (settings.getStringSetting("FFMPEG Executable Path").isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must set the path to FFMPEG's executable.", ButtonType.OK);
            alert.showAndWait();

            File ffmpegFile = null;

            while (ffmpegFile == null) {
                final FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("FFMPEG Executable Selection");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                ffmpegFile = fileChooser.showOpenDialog(stage);

                if (ffmpegFile == null) {
                    alert = new Alert(Alert.AlertType.WARNING, "No file was selected, would you like to try again?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();

                    if (alert.getResult().equals(ButtonType.NO)) {
                        System.exit(0);
                    }
                }
            }

            settings.setSetting("FFMPEG Executable Path", ffmpegFile.getAbsolutePath());
            settings.save();
        }

        // Setup the scene manager's singleton instance.
        this.stage = stage;
        INSTANCE = this;

        // Display the main view.
        final MainModel model = new MainModel();
        final MainView view = new MainView(model);
        setInitialController(new MainController(model, view));
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static SceneManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the initial pane.
     *
     * @param controller
     *          The controller of the initial scene.
     *
     * @throws NullPointerException
     *           If the controller is null.
     */
    public void setInitialController(final @NonNull Controller controller) {
        previousController = controller;
        currentController = controller;

        final Scene scene = new Scene(controller.getView().getPane());
        scene.getStylesheets().add("global.css");
        scene.getRoot().getStyleClass().add("main-root");

        previousScene = scene;
        currentScene = scene;

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Swaps the current scene with a new scene, so that the new scene
     * is displayed.
     *
     * @param controller
     *          The controller of the new scene.
     *
     * @throws NullPointerException
     *           If the controller is null.
     */
    public void swapToNewScene(final @NonNull Controller controller) {
        previousController = currentController;
        currentController = controller;

        previousScene = currentScene;
        currentScene = new Scene(controller.getView().getPane());

        // Set new scene's pane to be the same size as the previous
        // pane's scene.
        final Pane previousPane = previousController.getView().getPane();
        final Pane currentPane = currentController.getView().getPane();

        currentPane.setPrefSize(previousPane.getWidth(), previousPane.getHeight());

        addStylesheet(currentScene);
        stage.setScene(currentScene);
    }

    /** Swaps the current and previous scenes, so that the previous scene is displayed. */
    public void swapToPreviousScene() {
        final Controller tempC = previousController;
        previousController = currentController;
        currentController = tempC;

        final Scene tempS = previousScene;
        previousScene = currentScene;
        currentScene = tempS;

        // Set new scene's pane to be the same size as the previous pane's scene.
        final Pane previousPane = previousController.getView().getPane();
        final Pane currentPane = currentController.getView().getPane();

        currentPane.setPrefSize(previousPane.getWidth(), previousPane.getHeight());

        stage.setScene(currentScene);
    }

    /**
     * Adds the global stylesheet to a scene.
     *
     * @param scene
     *          The scene.
     */
    private void addStylesheet(final Scene scene) {
        if (scene == null) {
            return;
        }

        if (scene.getStylesheets().size() == 0) {
            scene.getStylesheets().add("global.css");
            scene.getRoot().getStyleClass().add("main-root");
        }
    }
}
