package view;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
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

    /**
     * Creates a button control with an icon on it.
     *
     * @param iconPath
     *          The path to the icon.
     *
     * @param width
     *          The width to resize the icon to.
     *
     * @param height
     *          The height to resize the icon to.
     */
    static Button createIconButton(final String iconPath, final int width, final int height) {
        final Button button = new Button();
        final Image image = new Image(iconPath, (double) width, (double) height, true, true);
        button.setGraphic(new ImageView(image));
        return button;
    }

    /**
     * Creates a GridPane with one row and two columns where each column
     * fills 50% of the width.
     *
     * @return
     *         The pane.
     */
    static GridPane getHalvedGridPane() {
        final GridPane pane = new GridPane();

        final ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);

        final ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);

        pane.getColumnConstraints().addAll(column1, column2);

        return pane;
    }
}
