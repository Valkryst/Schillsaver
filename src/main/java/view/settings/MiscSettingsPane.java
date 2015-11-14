package view.settings;

import controller.settings.MiscSettingsController;
import handler.ConfigHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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

    /** The toggle group of the yes/no radio buttons of the showSplashScreen option. */
    private final ToggleGroup toggleGroup_showSplashScreen = new ToggleGroup();
    /** The radio button that says to display the splash screen on program startup. */
    private final RadioButton radioButton_showSplashScreen_yes = new RadioButton("Yes");
    /** The radio button that says not to display the splash screen on program startup. */
    private final RadioButton radioButton_showSplashScreen_no = new RadioButton("No");

    /** The text field for the absolute path of the splash screen. */
    private final TextField field_splashScreenFilePath = new TextField();
    /** The button to open the handler selection dialog to locate an image to use as the splash screen. */
    private final Button button_selectFile_splashScreenFilePath = new Button("Select File");

    /** The text field for the amount of time, in milliseconds, to display the splach screen for. */
    private final TextField field_splashScreenDisplayTime = new TextField();

    /** The toggle group of the yes/no radio buttons of the enableUpdateCheck option. */
    private final ToggleGroup toggleGroup_enableUpdateCheck = new ToggleGroup();
    /** The radio button that says to check for updates on program startup. */
    private final RadioButton radioButton_enableUpdateCheck_yes = new RadioButton("Yes");
    /** The radio button that says not to check for updates on program startup. */
    private final RadioButton radioButton_enableUpdateCheck_no = new RadioButton("No");

    /** The toggle group of the yes/no radio buttons of the enableWarnUserIfSettingsMayNotWorkForYouTube option. */
    private final ToggleGroup toggleGroup_enableWarnUserIfSettingsMayNotWorkForYouTube = new ToggleGroup();
    /** The radio button that says to warn the user if their settings may not work with YouTube. */
    private final RadioButton radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes = new RadioButton("Yes");
    /** The radio button that says not to warn the user if their settings may not work with YouTube. */
    private final RadioButton radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no = new RadioButton("No");

    public MiscSettingsPane(final Stage settingsStage, final MiscSettingsController controller, final ConfigHandler configHandler) {
        // Set Field Prompt Text:
        field_splashScreenFilePath.setPromptText("splashScreenFilePath");
        field_splashScreenDisplayTime.setPromptText("splashScreenDisplayTime");

        // Setup Toggle Groups:
        radioButton_deleteSourceFileWhenEncoding_yes.setToggleGroup(toggleGroup_deleteSourceFileWhenEncoding);
        radioButton_deleteSourceFileWhenEncoding_no.setToggleGroup(toggleGroup_deleteSourceFileWhenEncoding);

        radioButton_deleteSourceFileWhenDecoding_yes.setToggleGroup(toggleGroup_deleteSourceFileWhenDecoding);
        radioButton_deleteSourceFileWhenDecoding_no.setToggleGroup(toggleGroup_deleteSourceFileWhenDecoding);

        radioButton_showSplashScreen_yes.setToggleGroup(toggleGroup_showSplashScreen);
        radioButton_showSplashScreen_no.setToggleGroup(toggleGroup_showSplashScreen);

        radioButton_enableUpdateCheck_yes.setToggleGroup(toggleGroup_enableUpdateCheck);
        radioButton_enableUpdateCheck_no.setToggleGroup(toggleGroup_enableUpdateCheck);

        radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes.setToggleGroup(toggleGroup_enableWarnUserIfSettingsMayNotWorkForYouTube);
        radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no.setToggleGroup(toggleGroup_enableWarnUserIfSettingsMayNotWorkForYouTube);

        // Set Default Values:
        if(configHandler.getDeleteSourceFileWhenEncoding()) {
            radioButton_deleteSourceFileWhenEncoding_yes.setSelected(true);
        } else {
            radioButton_deleteSourceFileWhenEncoding_no.setSelected(true);
        }

        if(configHandler.getDeleteSourceFileWhenDecoding()) {
            radioButton_deleteSourceFileWhenDecoding_yes.setSelected(true);
        } else {
            radioButton_deleteSourceFileWhenDecoding_no.setSelected(true);
        }

        if(configHandler.getShowSplashScreen()) {
            radioButton_showSplashScreen_yes.setSelected(true);
        } else {
            radioButton_showSplashScreen_no.setSelected(true);
        }

        field_splashScreenFilePath.setText(configHandler.getSplashScreenFilePath());
        field_splashScreenDisplayTime.setText(String.valueOf(configHandler.getSplashScreenDisplayTime()));

        if(configHandler.getCheckForUpdatesOnStart()) {
            radioButton_enableUpdateCheck_yes.setSelected(true);
        } else {
            radioButton_enableUpdateCheck_no.setSelected(true);
        }

        if(configHandler.getWarnUserIfSettingsMayNotWorkForYouTube()) {
            radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes.setSelected(true);
        } else {
            radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no.setSelected(true);
        }

        // Set Component Tooltips:
        field_splashScreenFilePath.setTooltip(new Tooltip("The absolute path to the splash screen to display."));
        button_selectFile_splashScreenFilePath.setTooltip(new Tooltip("Opens the handler selection dialog to locate an image to use as the splash screen."));

        field_splashScreenDisplayTime.setTooltip(new Tooltip("The amount of time, in milliseconds, to display the splash screen.</br></br>1000 = 1 second"));

        // Set Component Listeners:
        button_selectFile_splashScreenFilePath.setOnAction(controller);

        // Setup the Layout:
        final HBox content_pane_deleteSourceFileWhenEncoding = new HBox(10);
        content_pane_deleteSourceFileWhenEncoding.setAlignment(Pos.CENTER);
        content_pane_deleteSourceFileWhenEncoding.getChildren().addAll(radioButton_deleteSourceFileWhenEncoding_yes, radioButton_deleteSourceFileWhenEncoding_no);

        final TitledPane pane_deleteSourceFileWhenEncoding = new TitledPane();
        pane_deleteSourceFileWhenEncoding.setText("Delete Source File After Encoding");
        pane_deleteSourceFileWhenEncoding.setCollapsible(false);
        pane_deleteSourceFileWhenEncoding.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_deleteSourceFileWhenEncoding.setContent(content_pane_deleteSourceFileWhenEncoding);



        final HBox content_pane_deleteSourceFileWhenDecoding = new HBox(10);
        content_pane_deleteSourceFileWhenDecoding.setAlignment(Pos.CENTER);
        content_pane_deleteSourceFileWhenDecoding.getChildren().addAll(radioButton_deleteSourceFileWhenDecoding_yes, radioButton_deleteSourceFileWhenDecoding_no);

        final TitledPane pane_deleteSourceFileWhenDecoding = new TitledPane();
        pane_deleteSourceFileWhenDecoding.setText("Delete Source File After Decoding");
        pane_deleteSourceFileWhenDecoding.setCollapsible(false);
        pane_deleteSourceFileWhenDecoding.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_deleteSourceFileWhenDecoding.setContent(content_pane_deleteSourceFileWhenDecoding);



        final HBox content_pane_showSplashScreen  = new HBox(10);
        content_pane_showSplashScreen.setAlignment(Pos.CENTER);
        content_pane_showSplashScreen.getChildren().addAll(radioButton_showSplashScreen_yes, radioButton_showSplashScreen_no);

        final TitledPane pane_showSplashScreen = new TitledPane();
        pane_showSplashScreen.setText("Show Splash Screen");
        pane_showSplashScreen.setCollapsible(false);
        pane_showSplashScreen.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_showSplashScreen.setContent(content_pane_showSplashScreen);



        final HBox panel_options_right_top = new HBox(10);
        HBox.setHgrow(field_splashScreenFilePath, Priority.ALWAYS);
        panel_options_right_top.getChildren().addAll(field_splashScreenFilePath, button_selectFile_splashScreenFilePath);

        final VBox panel_options_right = new VBox(4);
        HBox.setHgrow(field_splashScreenDisplayTime, Priority.ALWAYS);
        panel_options_right.getChildren().addAll(panel_options_right_top, field_splashScreenDisplayTime);

        final HBox panel_options = new HBox(10);
        panel_options.getChildren().addAll(pane_showSplashScreen, panel_options_right);



        final HBox content_pane_enableUpdateCheck = new HBox(10);
        content_pane_enableUpdateCheck.setAlignment(Pos.CENTER);
        content_pane_enableUpdateCheck.getChildren().addAll(radioButton_enableUpdateCheck_yes, radioButton_enableUpdateCheck_no);

        final TitledPane pane_enableUpdateCheck = new TitledPane();
        pane_enableUpdateCheck.setText("Check for Updates on Start");
        pane_enableUpdateCheck.setCollapsible(false);
        pane_enableUpdateCheck.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_enableUpdateCheck.setContent(content_pane_enableUpdateCheck);



        final HBox content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube = new HBox(10);
        content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setAlignment(Pos.CENTER);
        content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube.getChildren().addAll(radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes, radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_no);

        final TitledPane pane_enableWarnUserIfSettingsMayNotWorkForYouTube = new TitledPane();
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setText("Warn if Settings may not work on YouTube");
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setCollapsible(false);
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
        pane_enableWarnUserIfSettingsMayNotWorkForYouTube.setContent(content_pane_enableWarnUserIfSettingsMayNotWorkForYouTube);



        final HBox panel_top = new HBox(10);
        panel_top.getChildren().addAll(pane_deleteSourceFileWhenEncoding, pane_deleteSourceFileWhenDecoding, pane_showSplashScreen, panel_options);



        final HBox panel_bottom = new HBox(10);
        panel_bottom.setAlignment(Pos.CENTER);
        panel_bottom.getChildren().addAll(pane_enableUpdateCheck, pane_enableWarnUserIfSettingsMayNotWorkForYouTube);



        final VBox panel = new VBox(4);
        panel.getChildren().addAll(panel_top, panel_bottom);



        this.setText(TAB_NAME);
        this.setCollapsible(false);
        this.heightProperty().addListener(new ChangeListener<Number>() { // Ensures that the scene will rezize when the pane is collapsed.
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                settingsStage.sizeToScene();
            }
        });
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

    /** @return Whether or not to show the splash screen on program startup. */
    public boolean getShowSplashScreen() {
        return radioButton_showSplashScreen_yes.isSelected();
    }

    /** @return The text field for the absolute path of the splash screen. */
    public TextField getField_splashScreenFilePath() {
        return field_splashScreenFilePath;
    }

    /** @return The button to open the handler selection dialog to locate an image to use as the splash screen. */
    public Button getButton_selectFile_splashScreenFilePath() {
        return button_selectFile_splashScreenFilePath;
    }

    /** @return The text field for the amount of time, in milliseconds, to display the splach screen for. */
    public TextField getField_splashScreenDisplayTime() {
        return field_splashScreenDisplayTime;
    }

    /** @return Whether or not to check for program updates on program start. */
    public boolean getCheckForUpdatesOnStart() {
        return radioButton_enableUpdateCheck_yes.isSelected();
    }

    /** @return Whether or not to warn the user if their settings may not work with YouTube. */
    public boolean getWarnIfSettingsMayNotWorkWithYouTube() {
        return radioButton_enableWarnUserIfSettingsMayNotWorkForYouTube_yes.isSelected();
    }
}
