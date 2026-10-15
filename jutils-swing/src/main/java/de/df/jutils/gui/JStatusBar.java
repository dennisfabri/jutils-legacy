package de.df.jutils.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import de.df.jutils.gui.border.ExtendedLineBorder;

public class JStatusBar extends JPanel {

    private static final long serialVersionUID = -2467131936289163554L;

    private final JLabel label = new JLabel();

    public JStatusBar() {
        super(new BorderLayout());
        Border padding = BorderFactory.createEmptyBorder(2, 5, 2, 5);
        setBorder(BorderFactory.createCompoundBorder(new ExtendedLineBorder(1, 0, 0, 0), padding));
        add(label, BorderLayout.CENTER);
    }

    public void setText(String text) {
        label.setText(text == null ? "" : text);
    }

    public String getText() {
        return label.getText();
    }
}
