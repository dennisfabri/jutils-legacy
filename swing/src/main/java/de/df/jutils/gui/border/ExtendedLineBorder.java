package de.df.jutils.gui.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

public final class ExtendedLineBorder extends AbstractBorder {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256438127291086130L;

    private int               top              = 1;
    private int               left             = 1;
    private int               bottom           = 1;
    private int               right            = 1;

    private Color             color            = null;

    public ExtendedLineBorder() {
        super();
    }

    public ExtendedLineBorder(Color c, int space) {
        this(c, space, space, space, space);
    }

    public ExtendedLineBorder(Color c, int t, int l, int b, int r) {
        this(t, l, b, r);
        color = c;
    }

    public ExtendedLineBorder(int t, int l, int b, int r) {
        top = t;
        left = l;
        bottom = b;
        right = r;
    }

    public void setColor(Color c) {
        color = c;
    }

    public Color getColor() {
        return color;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.border.Border#paintBorder(java.awt.Component,
     * java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
        int x2 = x + width - 1;
        int y2 = y + height - 1;
        if (color == null) {
            g.setColor(UIManager.getColor("controlShadow"));
        } else {
            g.setColor(color);
        }
        if (top > 0) {
            g.fillRect(x, y, width, top);
        }
        if (left > 0) {
            g.fillRect(x, y, left, height);
        }
        if (bottom > 0) {
            g.fillRect(x, y2 - bottom + 1, width, bottom);
        }
        if (right > 0) {
            g.fillRect(x2 - right + 1, y, right, height);
        }
    }

    @Override
    public Insets getBorderInsets(final Component c, final Insets insets) {
        insets.top = top;
        insets.left = left;
        insets.bottom = bottom;
        insets.right = right;
        return insets;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    @Override
    public Insets getBorderInsets(final Component c) {
        return getBorderInsets(c, new Insets(0, 0, 0, 0));
    }
}