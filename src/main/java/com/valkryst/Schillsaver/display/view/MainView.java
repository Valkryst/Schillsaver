package com.valkryst.Schillsaver.display.view;

import com.valkryst.Schillsaver.display.Display;
import com.valkryst.Schillsaver.display.controller.MainController;
import com.valkryst.Schillsaver.display.model.DecodeModel;
import com.valkryst.Schillsaver.display.model.EncodeModel;
import com.valkryst.Schillsaver.display.model.SettingsTabModel;
import com.valkryst.VMVC.view.View;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainView extends View<MainController> {
    /**
     * Constructs a new {@code MainView}.
     *
     * @param controller {@link MainController} associated with this view.
     */
    public MainView(final @NonNull MainController controller) {
        super(controller);

        final var tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Encode", new EncodeModel().createView());
        tabbedPane.addTab("Decode", new DecodeModel().createView());

        try {
            tabbedPane.addTab("Settings", new SettingsTabModel().createView());
        } catch (final IOException e) {
            Display.displayError(this, e);
        }

        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);
    }
}
