package com.valkryst.Schillsaver.display.model;

import com.valkryst.Schillsaver.display.controller.MainController;
import com.valkryst.Schillsaver.display.view.MainView;
import com.valkryst.VMVC.model.Model;
import lombok.NonNull;

public class MainModel extends Model<MainController, MainView> {
    @Override
    protected MainController createController() {
        return new MainController(this);
    }

    @Override
    protected MainView createView(final @NonNull MainController controller) {
        return new MainView(controller);
    }
}
