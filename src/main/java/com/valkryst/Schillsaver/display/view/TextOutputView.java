package com.valkryst.Schillsaver.display.view;

import com.valkryst.Schillsaver.display.Display;
import com.valkryst.Schillsaver.display.controller.TextOutputController;
import com.valkryst.JCopyButton.JCopyButton;
import com.valkryst.VMVC.view.View;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.function.Consumer;

public class TextOutputView extends View<TextOutputController> {
    private final JTextArea textArea = createTextArea();

    private final JCopyButton copyButton = new JCopyButton(textArea);

    private final JButton saveButton = createSaveButton();

    /**
     * Constructs a new {@code TextOutputView}.
     *
     * @param controller {@link TextOutputController} associated with this view.
     */
    public TextOutputView(final @NonNull TextOutputController controller) {
        super(controller);

        copyButton.setEnabled(false);
        copyButton.setToolTipText("Copy the output messages to the clipboard.");

        this.setLayout(new BorderLayout());

        final var scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);

        this.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        final var panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel.add(copyButton);
        panel.add(saveButton);
        return panel;
    }

    /**
     * Creates a new {@link JButton} which opens a {@link JFileChooser} to save the {@code textArea}'s contents to a
     * file.
     *
     * @return The button.
     */
    private JButton createSaveButton() {
        final var button = new JButton("Save");
        button.setEnabled(false);
        button.setToolTipText("Save the output messages to a text file.");
        button.addActionListener(e -> {
            final var fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setDialogTitle("Save Output");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            final var result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                var file = fileChooser.getSelectedFile();
                if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("txt")) {
                    file = new File(file + ".txt");
                }

                try {
                    textArea.write(Files.newBufferedWriter(file.toPath()));
                } catch (final Exception ex) {
                    Display.displayError(this, ex);
                }
            }
        });
        return button;
    }

    /**
     * Creates a new {@link JTextArea}.
     *
     * @return The text area.
     */
    private JTextArea createTextArea() {
        final var textArea = new JTextArea();
        textArea.setEnabled(false);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                copyButton.setEnabled(true);
                saveButton.setEnabled(true);
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                copyButton.setEnabled(!textArea.getText().isEmpty());
                saveButton.setEnabled(!textArea.getText().isEmpty());
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                copyButton.setEnabled(!textArea.getText().isEmpty());
                saveButton.setEnabled(!textArea.getText().isEmpty());
            }
        });
        return textArea;
    }

    /**
     * Retrieves a {@link Consumer} which appends text to the {@code textArea}.
     *
     * @return The consumer.
     */
    public Consumer<String> getAppendTextConsumer() {
        return s -> SwingUtilities.invokeLater(() -> textArea.append(s));
    }

    /** Clears the {@code textArea}. */
    public void clearText() {
        textArea.setText("");
    }
}
