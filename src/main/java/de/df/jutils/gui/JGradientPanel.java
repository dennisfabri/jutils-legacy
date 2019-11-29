/*
 * Created on 14.04.2005
 */
package de.df.jutils.gui;

import java.awt.*;

import javax.swing.JPanel;

import de.df.jutils.graphics.ColorUtils;

public class JGradientPanel extends JPanel {

    private static final long serialVersionUID = 3258410651218425653L;

    private Color             left             = null;
    private Color             right            = null;

    public JGradientPanel() {
        super();
    }

    public JGradientPanel(boolean arg0) {
        super(arg0);
    }

    public JGradientPanel(LayoutManager arg0, boolean arg1) {
        super(arg0, arg1);
    }

    public JGradientPanel(LayoutManager arg0) {
        super(arg0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Store color
        Color backup = g.getColor();

        // prepare colors
        Color start = left;
        if (start == null) {
            start = Color.WHITE;
        }
        Color end = right;
        if (right == null) {
            end = getBackground();
        }

        // fill background
        float divisor = getWidth();
        for (int x = 0; x < getWidth(); x++) {
            float divident = x;
            float percent = divident / divisor;
            g.setColor(ColorUtils.calculateColor(start, end, percent));
            g.drawLine(x, 0, x, getHeight() - 1);
        }

        // restore color
        g.setColor(backup);
    }

    public Color getLeftColor() {
        return left;
    }

    public Color getRightColor() {
        return right;
    }

    public void setLeftColor(Color left) {
        this.left = left;
    }

    public void setRightColor(Color right) {
        this.right = right;
    }
}