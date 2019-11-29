package de.df.jutils.gui.border;

import java.awt.*;

import javax.swing.border.AbstractBorder;

public final class InsetsBorder extends AbstractBorder {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3258413949668898869L;
    private int               top              = 1;
    private int               left             = 1;
    private int               bottom           = 1;
    private int               right            = 1;

    private Color             color            = Color.BLACK;

    public InsetsBorder() {
        super();
    }

    public InsetsBorder(Color c, int t, int l, int b, int r) {
        color = c;
        top = t;
        left = l;
        bottom = b;
        right = r;
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
        g.setColor(color);
        if (top > 0) {
            g.fillRect(x, y, x2, y + top - 1);
        }
        if (left > 0) {
            g.fillRect(x, y2, x + left - 1, y);
        }
        if (bottom > 0) {
            g.fillRect(x, y2 - bottom + 1, x2, y2);
        }
        if (right > 0) {
            g.drawLine(x2 - right + 1, y, x2, y2);
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
        return new Insets(top, left, bottom, right);
    }

}