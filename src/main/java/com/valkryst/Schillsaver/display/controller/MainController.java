package com.valkryst.Schillsaver.display.controller;

import com.valkryst.Schillsaver.display.model.MainModel;
import com.valkryst.VMVC.controller.Controller;
import lombok.NonNull;

public class MainController extends Controller<MainModel> {
    /**
     * Constructs a new {@code MainController}.
     *
     * @param model {@link MainModel} associated with this controller.
     */
    public MainController(final @NonNull MainModel model) {
        super(model);
    }
}
