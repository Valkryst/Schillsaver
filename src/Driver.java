import com.valkryst.VMVC.SceneManager;
import com.valkryst.VMVC.Settings;
import controller.MainController;
import javafx.application.Application;
import javafx.stage.Stage;
import misc.BlockSize;
import misc.FrameDimension;
import misc.FrameRate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

public class Driver extends Application {
    public static void main(final String[] args) {
            launch();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final HashMap<String, String> defaultSettings = new HashMap<>();
        defaultSettings.put("FFMPEG Executable Path", "/usr/bin/ffmpeg");

        defaultSettings.put("Total Encoding Threads", String.valueOf(1));
        defaultSettings.put("Total Decoding Threads", String.valueOf(1));

        defaultSettings.put("Encoding Frame Dimensions", FrameDimension.P720.name());
        defaultSettings.put("Encoding Frame Rate", FrameRate.FPS30.name());
        defaultSettings.put("Encoding Block Size", BlockSize.S8.name());
        defaultSettings.put("Encoding Codec", "libx264");

        try {
            final SceneManager sceneManager = new SceneManager(primaryStage);
            final Settings settings = new Settings(defaultSettings);

            sceneManager.setInitialPane(new MainController(sceneManager, settings));
        } catch (final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);
        }
    }
}
