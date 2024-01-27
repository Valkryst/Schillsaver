package Schillsaver.display.model;

import Schillsaver.display.controller.DecodeController;
import Schillsaver.display.view.DecodeView;
import com.valkryst.VMVC.model.Model;
import lombok.NonNull;

public class DecodeModel extends Model<DecodeController, DecodeView> {
    @Override
    protected DecodeController createController() {
        return new DecodeController(this);
    }

    @Override
    protected DecodeView createView(final @NonNull DecodeController controller) {
        return new DecodeView(controller);
    }
}
