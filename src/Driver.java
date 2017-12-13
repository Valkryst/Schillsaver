import com.valkryst.VMVC.Application;
import com.valkryst.VMVC.Settings;
import misc.BlockSize;
import misc.FrameDimension;
import misc.FrameRate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

public class Driver {
    public static void main(final String[] args) {
        final HashMap<String, String> defaultSettings = new HashMap<>();
        defaultSettings.put("Total Encoding Threads", String.valueOf(1));
        defaultSettings.put("Total Decoding Threads", String.valueOf(1));

        defaultSettings.put("Encoding Frame Dimensions", FrameDimension.P720.name());
        defaultSettings.put("Encoding Frame Rate", FrameRate.FPS30.name());
        defaultSettings.put("Encoding Block Size", BlockSize.S8.name());
        defaultSettings.put("Encoding Codec", "libx264");

        try {
            final Settings settings = new Settings(defaultSettings);

            final Application application = new Application(settings);
            application.launch();
        } catch (final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);
        }
    }
}
