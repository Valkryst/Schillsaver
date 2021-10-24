package Schillsaver.mvc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;

public class JFXHelper {
    // Prevent instances from being created.
    private JFXHelper() {}

    /**
     * Sets the tooltip of a JavaFX control.
     *
     * @param control
     *          The control.
     *
     * @param message
     *          The message.
     *
     * @throws NullPointerException
     *           If the control or message is null.
     *
     * @throws IllegalArgumentException
     *           If the message is empty.
     */
    public static void setTooltip(final @NonNull Control control, final @NonNull String message) {
        Objects.requireNonNull(control);
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
     *
     * @throws NullPointerException
     *           If the iconPath is null.
     *
     * @throws IllegalArgumentException
     *           If the iconPath is empty.
     */
    public static Button createIconButton(final @NonNull String iconPath, final int width, final int height) {
        if (iconPath.isEmpty()) {
            throw new IllegalArgumentException("The icon path cannot be empty.");
        }

        final Button button = new Button();
        final Image image = new Image(iconPath, (double) width, (double) height, true, true);
        button.setGraphic(new ImageView(image));
        return button;
    }

    /**
     * Creates a combo box with zero or more options.
     * Null options are ignored.
     *
     * @param options
     *          The options to add to the combo box.
     *
     * @return
     *          The combo box.
     */
    public static ComboBox<String> createComboBox(final String ... options) {
        final ObservableList<String> list = FXCollections.observableArrayList();

        for (final String option : options) {
            if (option != null) {
                list.add(option);
            }
        }

        return new ComboBox<>(list);
    }

    /**
     * Creates a combo box with zero or more options.
     * Null options are ignored.
     *
     * @param options
     *          The options to add to the combo box.
     *
     * @return
     *          The combo box.
     */
    public static ComboBox<String> createComboBox(final List<String> options) {
        options.removeIf(Objects::isNull);
        return new ComboBox<>(FXCollections.observableArrayList(options));
    }

    /**
     * Creates a GridPane with only one row, but where there are as many equal sized columns as there are
     * controls passed to the function.
     *
     * Ex:
     *      If you pass in two controls, then there will be one row with two columns where each column uses
     *      50% of the width.
     *
     * Ex:
     *      If you pass in four controls, then there will be one row with four columns where each column uses
     *      25% of the width.
     *
     * @param controls
     *          The controls.
     *
     * @return
     *          The pane.
     */
    public static GridPane createHorizontalGridPane(final Control... controls) {
        if (controls.length == 0) {
            return new GridPane();
        }

        final GridPane pane = new GridPane();
        final double sectionWidth = 100.0 / controls.length;

        for (final Control ignored : controls) {
            final ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(sectionWidth);

            pane.getColumnConstraints().add(constraints);
        }

        for (int i = 0 ; i < controls.length ; i++) {
            pane.add(controls[i], i, 0);
        }

        return pane;
    }
}
