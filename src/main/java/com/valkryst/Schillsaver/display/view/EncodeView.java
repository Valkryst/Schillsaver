package com.valkryst.Schillsaver.display.view;

import com.valkryst.Schillsaver.display.Display;
import com.valkryst.Schillsaver.display.controller.EncodeController;
import com.valkryst.Schillsaver.display.model.TextOutputModel;
import com.valkryst.JPathList.JPathList;
import com.valkryst.VMVC.view.View;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class EncodeView extends View<EncodeController> {
    private final JPathList pathList = new JPathList();

    private final TextOutputView outputView = new TextOutputModel().createView();

    /**
     * Constructs a new {@code EncodeView}.
     *
     * @param controller {@link EncodeController} associated with this view.
     */
    public EncodeView(final @NonNull EncodeController controller) {
        super(controller);

        outputView.setBorder(BorderFactory.createTitledBorder("Output Messages"));

        final var splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setTopComponent(getPathListPanel());
        splitPane.setBottomComponent(outputView);

        this.setLayout(new BorderLayout());
        super.add(getButtonsPanel(), BorderLayout.NORTH);
        super.add(splitPane, BorderLayout.CENTER);
    }

    public JPanel getButtonsPanel() {
        final var addFilesButton = getAddFilesButton();
        final var removeSelectedFilesButton = getRemoveSelectedFilesButton();
        final var removeAllFilesButton = getRemoveAllFilesButton();

        final var encodeButton = new JToggleButton("Start Encoding");
        encodeButton.setEnabled(false); // Disabled until a path is added.
        encodeButton.addActionListener(e -> {
            final var enableUi = new Runnable() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        addFilesButton.setEnabled(true);
                        removeSelectedFilesButton.setEnabled(!pathList.isSelectionEmpty());
                        removeAllFilesButton.setEnabled(pathList.getModel().getSize() > 0);
                        pathList.setEnabled(true);

                        encodeButton.setSelected(false);
                        encodeButton.setText("Start Encoding");
                    });
                }
            };

            final var disableUi = new Runnable() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        addFilesButton.setEnabled(false);
                        removeSelectedFilesButton.setEnabled(false);
                        removeAllFilesButton.setEnabled(false);
                        pathList.setEnabled(false);

                        encodeButton.setText("Stop Encoding");
                    });
                }
            };

            if (encodeButton.isSelected()) {
                outputView.clearText();
                controller.startEncoding(pathList.getPaths(), enableUi, disableUi, outputView.getAppendTextConsumer());
            } else {
                controller.stopEncoding(enableUi);
            }
        });

        // Ensure the "Start Encoding" button is en/disabled when the number of paths changes.
        pathList.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(final ListDataEvent e) {
                encodeButton.setEnabled(true);
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                encodeButton.setEnabled(pathList.getModel().getSize() > 0);
            }

            @Override
            public void contentsChanged(final ListDataEvent e) {
                encodeButton.setEnabled(pathList.getModel().getSize() > 0);
            }
        });

        final var leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(addFilesButton);
        leftPanel.add(removeSelectedFilesButton);
        leftPanel.add(removeAllFilesButton);

        final var rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(encodeButton);

        final var panel = new JPanel(new BorderLayout());
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JButton getAddFilesButton() {
        final var button = new JButton("Add Files");
        button.addActionListener(e -> {
            final var fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            final var result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                final var files = fileChooser.getSelectedFiles();

                Arrays.stream(files).map(File::toPath).forEach(path -> {
                    try {
                        pathList.addPath(path);
                    } catch (final IOException ex) {
                        Display.displayError(this, ex);
                    }
                });
            }
        });

        return button;
    }

    private JButton getRemoveSelectedFilesButton() {
        final var button = new JButton("Remove Selected Files");
        button.setEnabled(false);
        button.addActionListener(e -> {
            pathList.removePaths(pathList.getSelectedValuesList());
        });

        pathList.addListSelectionListener(e -> button.setEnabled(!pathList.isSelectionEmpty()));
        return button;
    }

    private JButton getRemoveAllFilesButton() {
        final var button = new JButton("Remove All Files");
        button.setEnabled(false);
        button.addActionListener(e -> {
            pathList.removeAllPaths();
        });

        pathList.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(final ListDataEvent e) {
                button.setEnabled(shouldBeEnabled());
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                button.setEnabled(shouldBeEnabled());
            }

            @Override
            public void contentsChanged(final ListDataEvent e) {
                button.setEnabled(shouldBeEnabled());
            }

            private boolean shouldBeEnabled() {
                return pathList.getModel().getSize() > 0;
            }
        });
        return button;
    }

    private JScrollPane getPathListPanel() {
        pathList.setRecursionMode(JFileChooser.FILES_AND_DIRECTORIES);
        pathList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index % 2 == 0) {
                    c.setBackground(UIManager.getColor("List.background").darker());
                } else {
                    c.setBackground(UIManager.getColor("List.background"));
                }
                return c;
            }
        });


        final var scrollPane = new JScrollPane(pathList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Files to Encode"));
        return scrollPane;
    }
}
