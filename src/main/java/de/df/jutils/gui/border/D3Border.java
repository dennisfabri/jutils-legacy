package de.df.jutils.gui.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

public final class D3Border extends AbstractBorder {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257571698126567224L;

    /*
     * (non-Javadoc)
     * @see javax.swing.border.Border#paintBorder(java.awt.Component,
     * java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
        int x2 = x + width - 1;
        int y2 = y + height - 1;
        g.setColor(UIManager.getColor("controlShadow"));
        g.drawLine(x, y2, x2, y2);
        g.drawLine(x2, y, x2, y2);
        g.setColor(UIManager.getColor("controlHighlight"));
        g.drawLine(x, y2 - 1, x, y);
        g.drawLine(x, y, x2, y);
    }

    @Override
    public Insets getBorderInsets(final Component c, final Insets insets) {
        insets.top = 1;
        insets.left = 1;
        insets.bottom = 1;
        insets.right = 1;
        return insets;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    @Override
    public Insets getBorderInsets(final Component c) {
        return new Insets(1, 1, 1, 1);
    }

}
