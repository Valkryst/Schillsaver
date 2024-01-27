package com.valkryst.Schillsaver.display.model;

import com.valkryst.Schillsaver.display.controller.TextOutputController;
import com.valkryst.Schillsaver.display.view.TextOutputView;
import com.valkryst.VMVC.model.Model;
import lombok.NonNull;

public class TextOutputModel extends Model<TextOutputController, TextOutputView> {
    @Override
    protected TextOutputController createController() {
        return new TextOutputController(this);
    }

    @Override
    protected TextOutputView createView(final @NonNull TextOutputController textOutputController) {
        return new TextOutputView(textOutputController);
    }
}
