/*
 * Created on 18.03.2007
 * @source
 * http://java.sun.com/developer/onlineTraining/GUI/Swing1/Magercises/M5/solution/Borders.java
 */
package de.df.jutils.gui.border;

import java.awt.*;

import javax.swing.border.Border;

public class DashedBorder implements Border {

    private int   thickness = 2;

    private Color color;
    private int   dashWidth;
    private int   dashHeight;

    public DashedBorder() {
        this(Color.black, 2, 2);
    }

    public DashedBorder(Color c, int width, int height) {
        if (width < 1) {
            throw new IllegalArgumentException("Invalid width: " + width);
        }
        if (height < 1) {
            throw new IllegalArgumentException("Invalid height: " + height);
        }
        color = c;
        dashWidth = width;
        dashHeight = height;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Insets insets = getBorderInsets(c);
        g.setColor(color);
        int numWide = (int) Math.round(1.0 * width / dashWidth);
        int numHigh = (int) Math.round(1.0 * height / dashHeight);
        int startPoint;
        for (int i = 0; i <= numWide; i += 2) {
            startPoint = x + dashWidth * i;
            g.fillRect(startPoint, y, dashWidth, thickness);
            g.fillRect(startPoint, y + height - insets.bottom, dashWidth, thickness);
        }
        for (int i = 0; i <= numHigh; i += 2) {
            startPoint = x + dashHeight * i;
            g.fillRect(x, startPoint, thickness, dashHeight);
            g.fillRect(x + width - insets.right, startPoint, thickness, dashHeight);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}