package com.valkryst.Schillsaver.display.controller;

import com.valkryst.Schillsaver.display.model.TextOutputModel;
import com.valkryst.VMVC.controller.Controller;
import lombok.NonNull;

public class TextOutputController extends Controller<TextOutputModel> {
    /**
     * Constructs a new {@code TextOutputController}.
     *
     * @param model {@link TextOutputModel} associated with this controller.
     */
    public TextOutputController(final @NonNull TextOutputModel model) {
        super(model);
    }
}
