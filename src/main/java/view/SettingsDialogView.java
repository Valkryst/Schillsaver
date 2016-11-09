package view;

import controller.SettingsDialogController;
import controller.settings.ArchivalSettingsController;
import controller.settings.FfmpegSettingsController;
import handler.ConfigHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import view.settings.MiscSettingsPane;

public class SettingsDialogView extends VBox {
    /** The controller for the archival settings section. */
    @Getter private final ArchivalSettingsController controller_archivalSettings;
    /** The controller for the FFMPEG settings section. */
    @Getter private final FfmpegSettingsController controller_ffmpegSettings;
    /** The pane for the misc. settings section. */
    @Getter private final MiscSettingsPane pane_miscSettings;

    /** The button to close the window and save settings. */
    @Getter private final Button button_accept = new Button("Accept");
    /** The button to close the window without saving settings. */
    @Getter private final Button button_cancel = new Button("Cancel");

    // todo JavaDoc
    public SettingsDialogView(final Stage settingsStage, final SettingsDialogController controller, final ConfigHandler configHandler) {
        // Initialize Variables:
        controller_archivalSettings = new ArchivalSettingsController(controller, configHandler);
        controller_ffmpegSettings = new FfmpegSettingsController(controller, configHandler);
        pane_miscSettings = new MiscSettingsPane(settingsStage, configHandler);

        // Set Component Tooltips:
        button_accept.setTooltip(new Tooltip("Accept and save the new settings."));
        button_cancel.setTooltip(new Tooltip("Close this window witout saving the settings."));

        // Set Component Listners:
        button_accept.setOnAction(controller);
        button_cancel.setOnAction(controller);

        // Setup the Layout
        final VBox panel_top = new VBox();
        final HBox panel_bottom = new HBox();

        // Bottom - Settings:
        VBox.setVgrow(panel_bottom, Priority.ALWAYS);

        // Bottom - Set buttons to fill all available space:
        HBox.setHgrow(button_accept, Priority.ALWAYS);
        HBox.setHgrow(button_cancel, Priority.ALWAYS);
        VBox.setVgrow(button_accept, Priority.ALWAYS);
        VBox.setVgrow(button_cancel, Priority.ALWAYS);


        panel_bottom.setAlignment(Pos.CENTER);

        panel_top.getChildren().addAll(controller_archivalSettings.getPane(), controller_ffmpegSettings.getPane(), pane_miscSettings);
        panel_bottom.getChildren().addAll(button_accept, button_cancel);

        this.getChildren().addAll(panel_top, panel_bottom);
    }
}
