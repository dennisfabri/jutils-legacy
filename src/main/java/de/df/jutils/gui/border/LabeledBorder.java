package de.df.jutils.gui.border;

import static de.df.jutils.gui.util.GraphicsUtils.paintGradient;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.util.DesignInit;

public final class LabeledBorder extends AbstractBorder {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3762536715289768244L;
    private String            name             = "";
    private int               hgap             = 10;
    private int               vgap             = 3;
    private boolean           rightBordered    = true;
    private int               minheight        = 0;

    public LabeledBorder(final String labelName) {
        setName(labelName);
    }

    public LabeledBorder(final String labelName, final boolean rightOff) {
        this(labelName);
        setRightBordered(rightOff);
    }

    public LabeledBorder(final String labelName, final int hGap, final int vGap, final boolean rightOff) {
        this(labelName, rightOff);
        setHgap(hGap);
        setVgap(vGap);
    }

    public LabeledBorder(final String label, final int hGap, final int vGap) {
        this(label);
        setHgap(hGap);
        setVgap(vGap);
    }

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x1, final int y1, final int w, final int h) {
        int h1 = Math.max(2 + 2 * vgap + getFontHeight(g), minheight);
        Color start = UIManager.getColor("InternalFrame.activeTitleBackground");
        Color end = UIManager.getColor("InternalFrame.activeTitleGradient");
        Color foreground = UIManager.getColor("InternalFrame.activeTitleForeground");
        if (start == null) {
            start = UIManager.getColor("InternalFrame.borderColor");
        }
        if (end == null) {
            end = UIManager.getColor("InternalFrame.borderShadow");
        }
        if (start == null) {
            start = Color.GRAY;
        }
        if (end == null) {
            end = Color.LIGHT_GRAY;
        }
        if (foreground == null) {
            Color medium = ColorUtils.calculateColor(start, end, 0.5);
            foreground = ColorUtils.invert(medium);
        }
        Color mix = paintGradient((Graphics2D) g, x1, y1, w, h1, start, end);
        foreground = ColorUtils.contrastColor(mix, foreground);
        drawText((Graphics2D) g, x1, y1, w, h1, name, foreground);
    }

    @Override
    public Insets getBorderInsets(final Component c, final Insets insets) {
        Graphics g = c.getGraphics();
        int size = getFontHeight(g);
        insets.top = Math.max(2 + 2 * vgap + size, minheight);
        insets.left = 0;
        insets.bottom = 0;
        insets.right = 0;
        return insets;
    }

    private int getFontHeight(Graphics g) {
        int size;
        if (g != null) {
            Font f = g.getFont();
            Graphics2D g2d = (Graphics2D) g;
            Rectangle2D r2d = f.getStringBounds(name, g2d.getFontRenderContext());
            size = (int) Math.ceil(r2d.getHeight());
        } else {
            size = 12;
        }
        return size;
    }

    @Override
    public Insets getBorderInsets(final Component c) {
        return getBorderInsets(c, new Insets(0, 0, 0, 0));
    }

    public int getHgap() {
        return hgap;
    }

    public String getName() {
        return name;
    }

    public int getVgap() {
        return vgap;
    }

    public void setHgap(final int i) {
        hgap = i;
    }

    public void setName(final String string) {
        if (string == null) {
            name = "";
        } else {
            name = string;
        }
    }

    public void setVgap(final int i) {
        vgap = i;
    }

    public boolean getRightBordered() {
        return rightBordered;
    }

    public void setRightBordered(final boolean b) {
        rightBordered = b;
    }

    public int getHeight() {
        return minheight;
    }

    public void setHeight(int height) {
        this.minheight = height;
    }

    private void drawText(Graphics2D g2, int x, int y, int width, int height, String text, Color foreground) {
        // Draw the text (if any)
        if (text != null) {
            if (DesignInit.ANTIALISED_TEXT) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }

            Font f = UIManager.getFont("TitledBorder.font");
            g2.setFont(f);
            if (foreground != null) {
                g2.setColor(foreground);
            }

            x += hgap;

            JLabel label = new JLabel(text);
            label.setForeground(foreground);
            g2.translate(x, y);
            label.setBounds(0, 0, width, height);
            label.paint(g2);
            g2.translate(-x, -y);
        }
    }

}