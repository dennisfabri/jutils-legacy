package de.df.jutils.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public final class TipOfTheDayDialog {

    private TipOfTheDayDialog() {
        // Hide
    }

    public static JDialog create(Component parent, TipOfTheDay tips, TipOfTheDay.Texts texts,
            Icon icon, TipOfTheDay.ShowOnStartupChoice choice) {
        Window window = parent == null ? JOptionPane.getRootFrame()
                : (parent instanceof Window w ? w : SwingUtilities.getWindowAncestor(parent));
        JDialog dialog = window instanceof Frame f ? new JDialog(f, texts.title(), true)
                : new JDialog((Dialog) window, texts.title(), true);

        TipOfTheDayPanel panel = new TipOfTheDayPanel(tips, texts, icon, choice);
        dialog.setContentPane(panel);

        Runnable closeAndSave = () -> {
            panel.saveChoice();
            dialog.setVisible(false);
        };
        panel.getCloseButton().addActionListener(e -> closeAndSave.run());
        dialog.getRootPane().setDefaultButton(panel.getCloseButton());
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAndSave.run();
            }
        });
        panel.registerKeyboardAction(e -> closeAndSave.run(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        return dialog;
    }
}
