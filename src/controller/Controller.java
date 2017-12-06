package controller;

import core.Driver;
import lombok.Getter;
import model.Model;
import view.View;

import java.util.Objects;

public class Controller<M extends Model, V extends View> {
    /** The driver. */
    protected final Driver driver;

    /** The model. */
    protected final M model;
    /** The view. */
    @Getter protected final V view;

    /**
     * Constructs a new Controller.
     *
     * @param driver
     *          The driver.
     *
     * @param model
     *          The model.
     *
     * @param view
     *          The view.
     *
     * @throws java.lang.NullPointerException
     *          If the model or view are null.
     */
    public Controller(final Driver driver, final M model, final V view) {
        Objects.requireNonNull(driver);
        Objects.requireNonNull(model);
        Objects.requireNonNull(view);

        this.driver = driver;
        this.model = model;
        this.view = view;
    }
}
