package com.valkryst.Schillsaver.display.model;

import com.valkryst.Schillsaver.display.controller.DecodeController;
import com.valkryst.Schillsaver.display.view.DecodeView;
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
