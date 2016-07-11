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
import lombok.Getter;

public class SettingsDialogView extends VBox {
    /** The controller for the archival settings section. */
    @Getter private final ArchivalSettingsController controller_archivalSettings;
    /** The controller for the FFMPEG settings section. */
    @Getter private final FfmpegSettingsController controller_ffmpegSettings;
    /** The cntroller for the misc. settings section. */
    @Getter private final MiscSettingsController controller_miscSettings;

    /** The button to close the window and save settings. */
    @Getter private final Button button_accept = new Button("Accept");
    /** The button to close the window without saving settings. */
    @Getter private final Button button_cancel = new Button("Cancel");

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
}
