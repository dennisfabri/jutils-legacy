/*
 * Created on 23.05.2005
 */
package de.df.jutils.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

public final class JUnderline extends JComponent {

    private static final long serialVersionUID = 781435188766680950L;

    private boolean           horizontal       = true;

    public JUnderline() {
        this(true);
    }

    public JUnderline(boolean hor) {
        horizontal = hor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Store values
        Color backup = g.getColor();

        // Paint
        if (horizontal) {
            int y = getHeight() - 1;
            g.setColor(Color.BLACK);
            g.drawLine(0, y, getWidth(), y);
        } else {
            int x = getWidth() - 1;
            g.setColor(Color.BLACK);
            g.drawLine(x, 0, x, getHeight());
        }

        // Reset values
        g.setColor(backup);
    }
}