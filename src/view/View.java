package view;

import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.util.Objects;

public class View {
    /** The pane of the view. */
    @Getter protected Pane pane;

    /**
     * Sets the tooltip of a JavaFX control.
     *
     * @param control
     *          The control.
     *
     * @param message
     *          The message.
     *
     * @throws java.lang.NullPointerException
     *           If the message is null.
     *
     * @throws java.lang.IllegalArgumentException
     *           If the message is empty.
     */
    static void setTooltip(final Control control, final String message) {
        Objects.requireNonNull(message);

        if (message.isEmpty()) {
            throw new IllegalArgumentException("The message cannot be empty.");
        }

        control.setTooltip(new Tooltip(message));
    }
}
