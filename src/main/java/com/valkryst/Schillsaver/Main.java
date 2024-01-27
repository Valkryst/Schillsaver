package com.valkryst.Schillsaver;

import com.valkryst.Schillsaver.display.Display;
import com.valkryst.Schillsaver.display.model.MainModel;
import com.valkryst.Schillsaver.display.model.SettingsTabModel;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final var settings = new SettingsTabModel();
        switch (settings.getSwingTheme()) {
            case DARK:
                FlatDraculaIJTheme.setup();
                break;
            case LIGHT:
                FlatLightFlatIJTheme.setup();
                break;
        }

        SwingUtilities.invokeLater(() -> {
            // Ensure tooltips stay visible for 60 seconds.
            ToolTipManager.sharedInstance().setDismissDelay(60000);

            Display.getInstance().setContentPane(new MainModel().createView());
        });
    }
}
