package view.settings;

import handler.ConfigHandler;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MiscSettingsPane extends TitledPane {
    /** The name of the Tab. */
    private static final String TAB_NAME = "Misc";

    /** The toggle group of the yes/no radio buttons of the deleteSourceFileWhenEncoding option. */
    private final ToggleGroup toggleGroup_deleteSourceFileWhenEncoding = new ToggleGroup();
    /** The radio button that says to delete the original handler when encoding finishes. */
    private final RadioButton radioButton_deleteSourceFileWhenEncoding_yes = new RadioButton("Yes");
    /** The radio button that says to not delete the originak handler when encoding finishes. */
    private final RadioButton radioButton_deleteSourceFileWhenEncoding_no = new RadioButton("No");

    /** The button toggle of the yes/no radio buttons of the deleteSourceFileWhenDecoding option. */
    private final ToggleGroup toggleGroup_deleteSourceFileWhenDecoding = new ToggleGroup();
    /** The radio button that says to delete the original handler when decoding finishes. */
    private final RadioButton radioButton_deleteSourceFileWhenDecoding_yes = new RadioButton("Yes");
    /** The radio button that says not to delete the original handler when decoding finishes. */
    private final RadioButton radioButton_deleteSourceFileWhenDecoding_no = new RadioButton("No");

    /** The toggle group of the yes/no radio buttons of the enableWarnUserIfSettingsMayNotWorkForYouTube option. */
    private final ToggleGroup toggleGroup_enableWarnUserIfSettingsMayNotWorkForYouTube = new ToggleGroup();
    /** The radio button that says to warn the user if their settings may not work with YouTube. */
    private final RadioButton radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes = new RadioButton("Yes");
    /** The radio button that says not to warn the user if their settings may not work with YouTube. */
    private final RadioButton radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no = new RadioButton("No");

    public MiscSettingsPane(final Stage settingsStage, final ConfigHandler configHandler) {
        // Setup Toggle Groups:
        radioButton_deleteSourceFileWhenEncoding_yes.setToggleGroup(toggleGroup_deleteSourceFileWhenEncoding);
        radioButton_deleteSourceFileWhenEncoding_no.setToggleGroup(toggleGroup_deleteSourceFileWhenEncoding);

        radioButton_deleteSourceFileWhenDecoding_yes.setToggleGroup(toggleGroup_deleteSourceFileWhenDecoding);
        radioButton_deleteSourceFileWhenDecoding_no.setToggleGroup(toggleGroup_deleteSourceFileWhenDecoding);

        radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes.setToggleGroup(toggleGroup_enableWarnUserIfSettingsMayNotWorkForYouTube);
        radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no.setToggleGroup(toggleGroup_enableWarnUserIfSettingsMayNotWorkForYouTube);

        // Set Default Values:
        if(configHandler.isDeleteSourceFileWhenEncoding()) {
            radioButton_deleteSourceFileWhenEncoding_yes.setSelected(true);
        } else {
            radioButton_deleteSourceFileWhenEncoding_no.setSelected(true);
        }

        if(configHandler.isDeleteSourceFileWhenDecoding()) {
            radioButton_deleteSourceFileWhenDecoding_yes.setSelected(true);
        } else {
            radioButton_deleteSourceFileWhenDecoding_no.setSelected(true);
        }

        if(configHandler.isWarnUserIfSettingsMayNotWorkForYouTube()) {
            radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes.setSelected(true);
        } else {
            radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no.setSelected(true);
        }

        // Setup the Layout:
        final HBox content_pane_deleteSourceFileWhenEncoding = new HBox(10);
        content_pane_deleteSourceFileWhenEncoding.setAlignment(Pos.CENTER);
        content_pane_deleteSourceFileWhenEncoding.getChildren().addAll(radioButton_deleteSourceFileWhenEncoding_yes, radioButton_deleteSourceFileWhenEncoding_no);

        final TitledPane pane_deleteSourceFileWhenEncoding = new TitledPane();
        pane_deleteSourceFileWhenEncoding.setText("Delete Source File After Encoding");
        pane_deleteSourceFileWhenEncoding.setCollapsible(false);
        pane_deleteSourceFileWhenEncoding.heightProperty().addListener((observable, oldValue, newValue) -> settingsStage.sizeToScene());
        pane_deleteSourceFileWhenEncoding.setContent(content_pane_deleteSourceFileWhenEncoding);



        final HBox content_pane_deleteSourceFileWhenDecoding = new HBox(10);
        content_pane_deleteSourceFileWhenDecoding.setAlignment(Pos.CENTER);
        content_pane_deleteSourceFileWhenDecoding.getChildren().addAll(radioButton_deleteSourceFileWhenDecoding_yes, radioButton_deleteSourceFileWhenDecoding_no);

        final TitledPane pane_deleteSourceFileWhenDecoding = new TitledPane();
        pane_deleteSourceFileWhenDecoding.setText("Delete Source File After Decoding");
        pane_deleteSourceFileWhenDecoding.setCollapsible(false);
        pane_deleteSourceFileWhenDecoding.heightProperty().addListener((observable, oldValue, newValue) -> settingsStage.sizeToScene());
        pane_deleteSourceFileWhenDecoding.setContent(content_pane_deleteSourceFileWhenDecoding);



        final HBox content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube = new HBox(10);
        content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setAlignment(Pos.CENTER);
        content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube.getChildren().addAll(radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes, radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no);

        final TitledPane pane_enableWarnUserIfSettingsMayNotWorkForYouTube = new TitledPane();
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setText("Warn if Settings may not work on YouTube");
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setCollapsible(false);
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.heightProperty().addListener((observable, oldValue, newValue) -> settingsStage.sizeToScene());
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setContent(content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube);



        final HBox panel = new HBox(10);
        panel.getChildren().addAll(pane_deleteSourceFileWhenEncoding, pane_deleteSourceFileWhenDecoding, pane_enableWarnUserIfSettingsMayNotWorkForYouTube);



        this.setText(TAB_NAME);
        this.setCollapsible(false);
        this.heightProperty().addListener((observable, oldValue, newValue) -> settingsStage.sizeToScene());
        this.setContent(panel);
    }

    ////////////////////////////////////////////////////////// Getters

    /** @return Whether or not to delete the source file after encoding is finished. */
    public boolean getDeleteSourceFileWhenEncoding() {
        return radioButton_deleteSourceFileWhenEncoding_yes.isSelected();
    }

    /** @return Whether or not to delete the source file after decoding is finished. */
    public boolean getDeleteSourceFileWhenDecoding() {
        return radioButton_deleteSourceFileWhenDecoding_yes.isSelected();
    }
}
