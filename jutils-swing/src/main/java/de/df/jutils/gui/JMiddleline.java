/*
 * Created on 23.05.2005
 */
package de.df.jutils.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

public final class JMiddleline extends JComponent {

    private static final long serialVersionUID = 781435188766680950L;

    private boolean horizontal = true;
    private int thickness = 1;

    public JMiddleline() {
        this(true, 1);
    }

    public JMiddleline(boolean hor) {
        this(hor, 1);
    }

    public JMiddleline(int thickness) {
        this(true, thickness);
    }

    public JMiddleline(boolean hor, int thickness) {
        horizontal = hor;
        this.thickness = thickness;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Store values
        Color backup = g.getColor();

        // Paint
        if (horizontal) {
            int y = getHeight() / 2;
            g.setColor(Color.BLACK);
            g.fillRect(0, y, getWidth(), thickness);
        } else {
            int x = getWidth() / 2;
            g.setColor(Color.BLACK);
            g.fillRect(x, 0, thickness, getHeight());
            // g.drawLine(x, 0, x, getHeight());
        }

        // Reset values
        g.setColor(backup);
    }
}
