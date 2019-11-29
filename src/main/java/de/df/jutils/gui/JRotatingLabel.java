package de.df.jutils.gui;

import java.awt.*;

import javax.swing.*;

public class JRotatingLabel extends JLabel {

    public JRotatingLabel(Icon icon) {
        this("", icon, SwingConstants.LEFT);
    }

    public JRotatingLabel(String text) {
        this(text, null, SwingConstants.LEFT);
    }

    public JRotatingLabel(String text, Icon icon, int align) {
        super(text, icon, align);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension s = super.getPreferredSize();
        int w = s.height;
        int h = s.width;
        s.height = h;
        s.width = w;
        return s;
    }

    /*
     * @Override public void setHorizontalAlignment(int alignment) {
     * super.setVerticalAlignment(alignment); }
     * @Override public void setVerticalAlignment(int alignment) {
     * super.setHorizontalAlignment(alignment); }
     */
    @Override
    public Dimension getMinimumSize() {
        Dimension s = super.getMinimumSize();
        int w = s.height;
        int h = s.width;
        s.height = h;
        s.width = w;
        return s;
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension s = super.getMaximumSize();
        int w = s.height;
        int h = s.width;
        s.height = h;
        s.width = w;
        return s;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(-Math.PI / 2.0);
        g2d.translate(-getHeight(), 0);

        if (isOpaque()) {
            if (getBackground() != null) {
                g2d.setBackground(getBackground());
                g2d.clearRect(0, 0, getHeight(), getWidth());
            }
        }
        if (getForeground() != null) {
            g2d.setColor(getForeground());
        } else {
            g2d.setColor(Color.BLACK);
        }
        int ascent = g2d.getFontMetrics().getMaxAscent();
        String t = getText();
        if (t == null) {
            t = "";
        }
        g2d.drawString(t, 0, ascent);
    }
}