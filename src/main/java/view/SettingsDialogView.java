package view;

import controller.SettingsDialogController;
import controller.settings.ArchivalSettingsController;
import controller.settings.FfmpegSettingsController;
import controller.settings.MiscSettingsController;
import handler.ConfigHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingsDialogView extends VBox {
    // todo JavaDoc
    private final ArchivalSettingsController controller_archivalSettings;
    // todo JavaDoc
    private final FfmpegSettingsController controller_ffmpegSettings;
    // todo JavaDoc
    private final MiscSettingsController controller_miscSettings;

    /** The button to close the window and save settings. */
    private final Button button_accept = new Button("Accept");
    /** The button to close the window without saving settings. */
    private final Button button_cancel = new Button("Cancel");

    // todo JavaDoc
    public SettingsDialogView(final SettingsDialogController controller, final ConfigHandler configHandler) {
        // Initalize Variables:
        controller_archivalSettings = new ArchivalSettingsController(controller, configHandler);
        controller_ffmpegSettings = new FfmpegSettingsController(controller, configHandler);
        controller_miscSettings = new MiscSettingsController(controller, configHandler);

        // Set Component Tooltips:
        button_accept.setTooltip(new Tooltip("Accept and save the new settings."));
        button_cancel.setTooltip(new Tooltip("Close this window witout saving the settings."));

        // Set Component Listners:
        button_accept.setOnAction(controller);
        button_cancel.setOnAction(controller);

        // Setup the Layout
        final VBox panel_top = new VBox(4);
        panel_top.getChildren().addAll(controller_archivalSettings.getPane(), controller_ffmpegSettings.getPane(), controller_miscSettings.getPane());

        final HBox panel_bottom = new HBox(10);
        panel_bottom.setAlignment(Pos.CENTER);
        panel_bottom.getChildren().addAll(button_accept, button_cancel);

        this.setSpacing(4);
        this.getChildren().addAll(panel_top, panel_bottom);
    }

    ////////////////////////////////////////////////////////// Getters

    // todo JavaDoc
    public ArchivalSettingsController getController_archivalSettings() {
        return controller_archivalSettings;
    }

    // todo JavaDoc
    public FfmpegSettingsController getController_ffmpegSettings() {
        return controller_ffmpegSettings;
    }

    // todo JavaDoc
    public MiscSettingsController getController_miscSettings() {
        return controller_miscSettings;
    }

    /** @return The button to close the window and save settings. */
    public Button getButton_accept() {
        return button_accept;
    }

    /** @return The button to close the window without saving settings. */
    public Button getButton_cancel() {
        return button_cancel;
    }
}
