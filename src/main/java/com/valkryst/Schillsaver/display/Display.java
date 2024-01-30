package com.valkryst.Schillsaver.display;

import com.valkryst.VMVC.view.View;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Display {
    /** Singleton instance. */
    private final static Display INSTANCE = new Display();

    @Getter private final JFrame frame = new JFrame();

    /** Constructs a new Display. */
    private Display() {
        final var icon = new ImageIcon(Display.class.getResource("/icon.png"));
        frame.setIconImage(icon.getImage());

        frame.setBackground(Color.BLACK);
        frame.setTitle("Schillsaver - Powered by /g/entoomen©®");
        frame.setIgnoreRepaint(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(525, 400));
        frame.pack();
        frame.setLocationRelativeTo(null); // Must be called after pack()
    }

	public void setContentPane(final @NonNull View view) {
		frame.setContentPane(view);
		frame.revalidate();

		view.requestFocusInWindow();
	}

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static Display getInstance() {
        return INSTANCE;
    }

    /**
     * Displays an error message in a dialog.
     *
     * @param dialogParent The parent component of the dialog.
     *
     * @param throwable The throwable.
     */
    public static void displayError(final Component dialogParent, final Throwable throwable) {
        SwingUtilities.invokeLater(() -> {
            final var stringBuilder = new StringBuilder(throwable.getMessage());
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(System.lineSeparator());

            for (final var element : throwable.getStackTrace()) {
                stringBuilder.append(element.toString());
                stringBuilder.append(System.lineSeparator());
            }

            final var textArea = new JTextArea(20, 60);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setText(stringBuilder.toString());

            JOptionPane.showMessageDialog(
                    dialogParent,
                    new JScrollPane(textArea),
                    "An Error Occurred",
                    JOptionPane.ERROR_MESSAGE
            );
        });
    }

    public static void displayInfo(final Component dialogParent, final String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                dialogParent,
                message,
                "Info",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    public static void displayWarning(final Component dialogParent, final String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                dialogParent,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE
            );
        });
    }
}
