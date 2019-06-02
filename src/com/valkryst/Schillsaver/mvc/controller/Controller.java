package com.valkryst.Schillsaver.mvc.controller;

import com.valkryst.Schillsaver.mvc.model.Model;
import com.valkryst.Schillsaver.mvc.view.View;
import lombok.Getter;
import lombok.NonNull;

public abstract class Controller {
    /** The model. */
    @Getter private final Model model;

    /** The view. */
    @Getter private final View view;

    /**
     * Constructs a new Controller.
     *
     * @param model
     *          The model.
     *
     * @param view
     *          The view.
     *
     * @throws NullPointerException
     *          If the model or view are null.
     */
    public Controller(final @NonNull Model model, final @NonNull View view) {
        this.model = model;
        this.view = view;
    }
}
