package com.valkryst.Schillsaver.display.view;

import com.valkryst.Schillsaver.setting.FrameRate;
import com.valkryst.Schillsaver.setting.FrameResolution;
import com.valkryst.Schillsaver.display.Display;
import com.valkryst.Schillsaver.display.controller.SettingsTabController;
import com.valkryst.Schillsaver.setting.BlockSize;
import com.valkryst.Schillsaver.setting.SwingTheme;
import com.valkryst.VMVC.view.View;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class SettingsTabView extends View<SettingsTabController> {
    /**
     * Constructs a new {@code SettingsTabView}.
     *
     * @param controller The controller associated with this view.
     */
    public SettingsTabView(final @NonNull SettingsTabController controller) {
        super(controller);

        setLayout(new GridBagLayout());
        final var c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // Swing theme label and combo box.
        final var themeLabel = new JLabel("Theme:");
        c.gridx = 0;
        c.gridy = 0;
        add(themeLabel, c);

        final var themeComboBox = createSwingThemeComboBox(controller);
        c.gridx++;
        add(themeComboBox, c);

        // Encoder Settings

        // FFMPEG executable file path label and button
        final var ffmpegLabel = new JLabel("FFMPEG executable file path:");
        c.gridx = 0;
        c.gridy++;
        add(ffmpegLabel, c);

        final var ffmpegField = new JTextField();
        ffmpegField.setEditable(false);
        ffmpegField.setText(controller.getFfmpegPath().toString());
        ffmpegField.setToolTipText(controller.getFfmpegPath().toString());
        c.gridx++;
        add(ffmpegField, c);

        c.gridx++;
        add(createFfmpegExecutableSelectionButton(controller, ffmpegField), c);

        // Output folder path label and button
        final var outputFolderLabel = new JLabel("Output folder path:");
        c.gridx = 0;
        c.gridy++;
        add(outputFolderLabel, c);

        final var outputFolderField = new JTextField();
        outputFolderField.setEditable(false);
        outputFolderField.setText(controller.getOutputFolderPath().toString());
        outputFolderField.setToolTipText(controller.getOutputFolderPath().toString());
        c.gridx++;
        add(outputFolderField, c);

        c.gridx++;
        add(createOutputFolderSelectionButton(controller, outputFolderField), c);

        // Block size label and combo box.
        final var blockSizeLabel = new JLabel("Block size:");
        c.gridx = 0;
        c.gridy++;
        add(blockSizeLabel, c);

        final var blockSizeComboBox = createBlockSizeComboBox(controller);
        c.gridx++;
        add(blockSizeComboBox, c);

        // Encoder Settings Panel
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(createEncoderSettingsPanel(), c);
    }

    /**
     * Creates a panel containing the encoder settings.
     *
     * @return The panel.
     */
    private JPanel createEncoderSettingsPanel() {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Encoder Settings"));
        panel.setLayout(new GridBagLayout());

        final var c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5,5);
        c.weightx = 1;

        // Codec label and field
        final var codecLabel = new JLabel("Codec:");
        c.gridx = 0;
        c.gridy++;
        panel.add(codecLabel, c);

        final var codecField = new JTextField();
        codecField.setEnabled(false);
        codecField.setEditable(false); // todo Should the user be allowed to change this?
        codecField.setText(controller.getCodec());
        codecField.setToolTipText("This is the codec used to encode the video. You should not need to change this.");
        c.gridx = 1;
        panel.add(codecField, c);

        // Frame rate label and combo box.
        final var framerateLabel = new JLabel("Frame rate:");
        c.gridx = 0;
        c.gridy++;
        panel.add(framerateLabel, c);

        final var framerateComboBox = createFramerateComboBox(controller);
        c.gridx = 1;
        panel.add(framerateComboBox, c);

        // Resolution label and combo box.
        final var resolutionLabel = new JLabel("Resolution:");
        c.gridx = 0;
        c.gridy++;
        panel.add(resolutionLabel, c);

        final var resolutionComboBox = createResolutionComboBox(controller);
        c.gridx = 1;
        panel.add(resolutionComboBox, c);

        return panel;
    }

    /**
     * Creates a button for selecting the FFMPEG executable.
     *
     * @param controller The controller.
     *
     * @return The button.
     */
    private JButton createFfmpegExecutableSelectionButton(final @NonNull SettingsTabController controller, final @NonNull JTextField textField) {
        final var button = new JButton("Browse");
        button.addActionListener(e -> {
            final var path = showFileChooser(controller.getFfmpegPath());

            if (path != null) {
                try {
                    textField.setText(path.toString());
                    textField.setToolTipText(path.toString());

                    controller.setFfmpegPath(path);
                } catch (final IOException ex) {
                    Display.displayError(button.getParent(), ex);
                }
            }
        });

        return button;
    }

    /**
     * Creates a button for selecting the output folder.
     *
     * @param controller The controller.
     * @return The button.
     */
    private JButton createOutputFolderSelectionButton(final @NonNull SettingsTabController controller, final @NonNull JTextField textField) {
        final var button = new JButton("Browse");
        button.addActionListener(e -> {
            final var path = showFolderChooser(controller.getOutputFolderPath());

            if (path != null) {
                try {
                    textField.setText(path.toString());
                    textField.setToolTipText(path.toString());

                    controller.setOutputFolderPath(path);
                } catch (final IOException ex) {
                    Display.displayError(button.getParent(), ex);
                }
            }
        });

        return button;
    }

    /**
     * Creates a combo box for selecting the framerate.
     *
     * @param controller The controller.
     * @return The combo box.
     */
    private JComboBox<FrameRate> createFramerateComboBox(final @NonNull SettingsTabController controller) {
        final var comboBox = new JComboBox<>(FrameRate.values());
        comboBox.setSelectedItem(controller.getFramerate());
        comboBox.setToolTipText("This is the output framerate of the encoded video.");

        comboBox.addActionListener(e -> {
            try {
                controller.setFramerate((FrameRate) Objects.requireNonNull(comboBox.getSelectedItem()));
            } catch (final IOException | NullPointerException ex) {
                Display.displayError(comboBox.getParent(), ex);
            }
        });

        return comboBox;
    }

    /**
     * Creates a combo box for selecting the resolution.
     *
     * @param controller The controller.
     * @return The combo box.
     */
    private JComboBox<FrameResolution> createResolutionComboBox(final @NonNull SettingsTabController controller) {
        final var comboBox = new JComboBox<>(FrameResolution.values());
        comboBox.setSelectedItem(controller.getResolution());
        comboBox.setToolTipText("This is the output resolution of the encoded video.");

        comboBox.addActionListener(e -> {
            try {
                controller.setResolution((FrameResolution) Objects.requireNonNull(comboBox.getSelectedItem()));
            } catch (final IOException | NullPointerException ex) {
                Display.displayError(comboBox.getParent(), ex);
            }
        });

        return comboBox;
    }

    /**
     * Creates a combo box for selecting the block size.
     *
     * @param controller The controller.
     * @return The combo box.
     */
    public JComboBox<BlockSize> createBlockSizeComboBox(final @NonNull SettingsTabController controller) {
        final var comboBox = new JComboBox<>(BlockSize.values());
        comboBox.setSelectedItem(controller.getBlockSize());
        comboBox.setToolTipText("This is the size of the black and white squares used to encode data in the video.");

        comboBox.addActionListener(e -> {
            try {
                controller.setBlockSize((BlockSize) Objects.requireNonNull(comboBox.getSelectedItem()));
            } catch (final IOException | NullPointerException ex) {
                Display.displayError(comboBox.getParent(), ex);
            }
        });

        return comboBox;
    }

    /**
     * Creates a combo box for selecting the swing theme.
     *
     * @param controller The controller.
     * @return The combo box.
     */
    public JComboBox<SwingTheme> createSwingThemeComboBox(final @NonNull SettingsTabController controller) {
        final var comboBox = new JComboBox<>(SwingTheme.values());
        comboBox.setSelectedItem(controller.getSwingTheme());
        comboBox.setToolTipText("This is the theme used to display the UI.");

        comboBox.addActionListener(e -> {
            try {
                controller.setSwingTheme((SwingTheme) Objects.requireNonNull(comboBox.getSelectedItem()));
                Display.displayInfo(comboBox.getParent(), "Restart the application for the theme to take effect.");
            } catch (final IOException | NullPointerException ex) {
                Display.displayError(comboBox.getParent(), ex);
            }
        });

        return comboBox;
    }

    /**
     * Shows a file chooser dialog.
     *
     * @param currentDirectory The current directory.
     * @return The selected file, or null if the user cancelled the dialog.
     */
    private Path showFileChooser(final @NonNull Path currentDirectory) {
        final var fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(currentDirectory.toFile());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        final var returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().toPath();
        }

        return null;
    }

    /**
     * Shows a folder chooser dialog.
     *
     * @param currentDirectory The current directory.
     * @return The selected folder, or null if the user cancelled the dialog.
     */
    private Path showFolderChooser(final @NonNull Path currentDirectory) {
        final var fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(currentDirectory.toFile());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        final var returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().toPath();
        }

        return null;
    }
}
