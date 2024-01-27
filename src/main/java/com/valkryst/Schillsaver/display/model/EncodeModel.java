package Schillsaver.display.model;

import Schillsaver.display.controller.EncodeController;
import Schillsaver.display.view.EncodeView;
import com.valkryst.VMVC.model.Model;
import lombok.NonNull;

public class EncodeModel extends Model<EncodeController, EncodeView> {
    @Override
    protected EncodeController createController() {
        return new EncodeController(this);
    }

    @Override
    protected EncodeView createView(final @NonNull EncodeController controller) {
        return new EncodeView(controller);
    }
}
