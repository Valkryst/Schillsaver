package view.settings;

import handler.ConfigHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MiscSettingsPane extends VBox {
    /** The label to define the Misc Settings section.. */
    private final Label label_paneName = new Label("Misc Settings");

    /** The toggle group of the yes/no radio buttons of the enableWarnUserIfSettingsMayNotWorkForYouTube option. */
    private final ToggleGroup toggleGroup_enableWarnUserIfSettingsMayNotWorkForYouTube = new ToggleGroup();
    /** The radio button that says to warn the user if their settings may not work with YouTube. */
    private final RadioButton radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes = new RadioButton("Yes");
    /** The radio button that says not to warn the user if their settings may not work with YouTube. */
    private final RadioButton radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no = new RadioButton("No");

    public MiscSettingsPane(final Stage settingsStage, final ConfigHandler configHandler) {
        // Setup Toggle Groups:

        radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes.setToggleGroup(toggleGroup_enableWarnUserIfSettingsMayNotWorkForYouTube);
        radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no.setToggleGroup(toggleGroup_enableWarnUserIfSettingsMayNotWorkForYouTube);

        // Set Default Values:

        if(configHandler.isWarnUserIfSettingsMayNotWorkForYouTube()) {
            radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes.setSelected(true);
        } else {
            radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no.setSelected(true);
        }

        // Setup the Layout:
        final HBox content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube = new HBox(10);
        content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setAlignment(Pos.CENTER);
        content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube.getChildren().addAll(radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes, radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no);

        final TitledPane pane_enableWarnUserIfSettingsMayNotWorkForYouTube = new TitledPane();
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setText("Warn if Settings may not work on YouTube");
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setCollapsible(false);
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.heightProperty().addListener((observable, oldValue, newValue) -> settingsStage.sizeToScene());
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setContent(content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube);



        this.setSpacing(4);
        this.getChildren().addAll(label_paneName, pane_enableWarnUserIfSettingsMayNotWorkForYouTube);
    }

    /** @return Whether or not to warn the user if settings may not work for YouTube. */
    public boolean getWarnUserIfSettingsMayNotWorkForYouTube() {
        return radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes.isSelected();
    }
}
