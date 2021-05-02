package de.df.jutils.gui.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

public final class UnderscoreBorder extends AbstractBorder {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3690756181119285043L;

    private Color             color            = Color.BLACK;
    private int               height           = 1;

    public UnderscoreBorder() {
        this(Color.BLACK, 1);
    }

    public UnderscoreBorder(int x) {
        this(Color.BLACK, x);
    }

    public UnderscoreBorder(Color c, int x) {
        if (c != null) {
            color = c;
        }
        height = x;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.border.Border#paintBorder(java.awt.Component,
     * java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w, final int h) {
        int x2 = x + w - 1;
        int y2 = y + h - 1;
        Graphics gr = g.create();
        gr.setColor(color);
        gr.fillRect(x, y2 - this.height + 1, x2, y2);
    }

    @Override
    public Insets getBorderInsets(final Component c, final Insets insets) {
        insets.top = 0;
        insets.left = 0;
        insets.bottom = height;
        insets.right = 0;
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