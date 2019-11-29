/*
 * Created on 01.11.2003
 */
package de.df.jutils.gui.border;

import java.awt.*;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.util.GraphicsUtils;
import de.df.jutils.util.OSUtils;

/**
 * @author Dennis Mueller
 */
public final class ShadowBorder extends AbstractBorder {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 4050768182447256377L;
    private final int         size;

    public ShadowBorder() {
        this(3);
    }

    public ShadowBorder(final int borderSize) {
        super();
        if (OSUtils.isWindows8OrHigher()) {
            this.size = 1;
        } else {
            this.size = borderSize;
        }
    }

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x1, final int y1, final int width, final int height) {
        if (size == 0) {
            return;
        }
        Color start = GraphicsUtils.getGradientBorderColor();
        Color end = UIManager.getColor("control");

        g.setColor(end);
        g.drawRect(x1, y1, width, height);

        g.setColor(start);
        g.drawRect(x1, y1, width - size, height - size);

        int x2 = x1 + width - size;
        int y2 = y1 + height - size;
        for (int x = 1; x < size; x++) {
            Color color = ColorUtils.calculateColor(start, end, x, size);
            drawEdge(g, x1 + x, y1 + x, x2 + x, y2 + x, x + 1, color);
        }
        drawQuarterCircle(g, start, end, x2, y2, size);
    }

    private void drawQuarterCircle(final Graphics g, final Color start, final Color end, final int x, final int y, final int radius) {
        for (int x1 = 0; x1 < radius; x1++) {
            int sqrx = x1 * x1;
            int posx = x1 + x;
            for (int y1 = 0; y1 < radius; y1++) {
                double sqrt = Math.sqrt(sqrx + y1 * y1);
                Color c = ColorUtils.calculateColor(start, end, sqrt, radius);
                int posy = y1 + y;
                g.setColor(c);
                g.drawLine(posx, posy, posx, posy);
            }
        }
    }

    private void drawEdge(final Graphics g, final int x1, final int y1, final int x2, final int y2, final int level, final Color c) {
        g.setColor(c);
        g.drawLine(x1, y2, x2 - level, y2);
        g.drawLine(x2, y2 - level, x2, y1);
    }

    @Override
    public Insets getBorderInsets(final Component c, final Insets insets) {
        insets.top = 1;
        insets.left = 1;
        insets.bottom = size;
        insets.right = size;
        return insets;
    }

    @Override
    public Insets getBorderInsets(final Component c) {
        return new Insets(1, 1, size, size);
    }
}