import com.valkryst.VMVC.AlertManager;
import com.valkryst.VMVC.SceneManager;
import com.valkryst.VMVC.Settings;
import controller.MainController;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import misc.BlockSize;
import misc.FrameDimension;
import misc.FrameRate;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Driver extends Application {
    public static void main(final String[] args) {
            launch();
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.getIcons().add(new Image("icons/Icon.png"));
        primaryStage.setTitle("Schillsaver - Powered by /g/entoomen\u00a9\u00ae");
        primaryStage.setMinWidth(512);
        primaryStage.setMinHeight(512);

        final HashMap<String, String> defaultSettings = new HashMap<>();
        defaultSettings.put("FFMPEG Executable Path", "");

        defaultSettings.put("Default Encoding Output Directory", "");
        defaultSettings.put("Default Decoding Output Directory", "");

        defaultSettings.put("Encoding Frame Dimensions", FrameDimension.P720.name());
        defaultSettings.put("Encoding Frame Rate", FrameRate.FPS30.name());
        defaultSettings.put("Encoding Block Size", BlockSize.S8.name());
        defaultSettings.put("Encoding Codec", "libx264");

        try {
            final SceneManager sceneManager = new SceneManager(primaryStage);
            final Settings settings = new Settings(defaultSettings);

            sceneManager.setInitialPane(new MainController(sceneManager, settings));

            // Deal with initial FFMPEG executable setup:
            if (settings.getStringSetting("FFMPEG Executable Path").isEmpty()) {
                AlertManager.showErrorAndWait("You must set the path to FFMPEG's executable.");

                final File ffmpegFile = selectFFMPEGExecutable(primaryStage);
                settings.setSetting("FFMPEG Executable Path", ffmpegFile.getAbsolutePath());
                settings.saveSettings();
            }
        } catch (final IOException e) {
            LogManager.getLogger().error(e);
        }
    }

    /**
     * Opens a file chooser for the user to select the FFMPEG executable.
     *
     * @param primaryStage
     *          The primary stage.
     *
     * @return
     *         The FFMPEG executable.
     */
    private File selectFFMPEGExecutable(final Stage primaryStage) {
        File ffmpegFile = null;

        while (ffmpegFile == null) {
            final var fileChooser = new FileChooser();
            fileChooser.setTitle("FFMPEG Executable Selection");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            ffmpegFile = fileChooser.showOpenDialog(primaryStage);

            if (ffmpegFile == null) {
                final String alertMessage = "No file was selected, would you like to try again?";
                final var alert = new Alert(Alert.AlertType.ERROR, alertMessage, ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if (alert.getResult().equals(ButtonType.NO)) {
                    System.exit(0);
                }
            }
        }

        return ffmpegFile;
    }
}
