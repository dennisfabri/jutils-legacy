package de.df.jutils.gui.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.util.OSUtils;

public final class GraphicsUtils {

    private GraphicsUtils() {
        // Hide
    }

    private enum Gradients {
        OCEAN, AQUA, GRAY
    }

    private static final Gradients defaultmode;

    private static Gradients getGradient() {
        return defaultmode;
    }

    static {
        Gradients value;
        if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) {
            value = Gradients.OCEAN;
        } else if (OSUtils.isWindows()) {
            value = Gradients.GRAY;
        } else if (OSUtils.isLinux()) {
            value = Gradients.GRAY;
        } else if (OSUtils.isMacOSX()) {
            value = Gradients.AQUA;
        } else {
            value = Gradients.GRAY;
        }

        defaultmode = value;
    }

    public static Color paintGradient(Graphics2D g2, int x, int y, int width, int height, Color gstart, Color gend) {
        if ((width <= 0) || (height <= 0)) {
            return gstart;
        }
        return paintGradient(g2, x, y, width, height, gstart, gend, getGradient());
    }

    private static Color paintGradient(Graphics2D g2, int x, int y, int width, int height, Color gstart, Color gend,
            Gradients mode) {
        if ((width <= 0) || (height <= 0)) {
            return gstart;
        }
        if (gstart == null) {
            gstart = g2.getBackground();
        }
        if (gend == null) {
            gend = ColorUtils.calculateColor(g2.getColor(), g2.getBackground(), 0.5);
        }
        g2 = (Graphics2D) g2.create();
        switch (mode) {
        case AQUA:
            return paintAquaGradient(g2, x, y, width, height, gstart, gend);
        case OCEAN:
            return paintOceanGradient(g2, x, y, width, height, gstart, gend);
        case GRAY:
        default:
            return paintFlat2Gradient(g2, x, y, width, height, gstart, gend);
        }
    }

    public static Color getGradientBorderColor() {
        return UIManager.getColor("ToolBar.shadow");
    }

    private static Color paintFlat2Gradient(Graphics g2, final int x, final int y, final int width, final int height,
            Color start, Color end) {
        if ((width <= 0) || (height <= 0)) {
            return start;
        }

        Graphics2D g = (Graphics2D) g2.create();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        Color background = start;

        g2.setColor(background);
        g2.fillRect(x, y, width, height);

        return background;
    }

    /**
     * Based on code from Jon Lipskys Weblog
     * 
     * @url http://blog.elevenworks.com/?p=2 @param g2 @param x @param y @param
     *      width @param height @param text @param foreground @param gstart @param
     *      gend
     */
    private static Color paintAquaGradient(Graphics2D g2, int x, int y, int width, int height, Color start, Color end) {
        if ((width <= 0) || (height <= 0)) {
            return start;
        }
        g2 = (Graphics2D) g2.create();
        BufferedImage vBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = vBuffer.createGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the background of the button
        bg.setColor(UIManager.getColor("control"));
        bg.fillRect(0, 0, width, height);

        Color middle = ColorUtils.calculateColor(start, end, 0.6);

        // Create the gradient paint for the first layer of the button
        Color vGradientStartColor = start;
        Color vGradientEndColor = middle.brighter();
        Paint vPaint = new GradientPaint(0, 0, vGradientStartColor, 0, height, vGradientEndColor, false);
        bg.setPaint(vPaint);

        // Paint the first layer of the button
        bg.fillRect(0, 0, width, height);

        // Calulate the size of the second layer of the button
        int vHighlightInset = 1;
        int vButtonHighlightHeight = height - (vHighlightInset * 2);
        int vButtonHighlightWidth = width - (vHighlightInset * 2);

        // Create the paint for the second layer of the button
        vGradientStartColor = end;
        // vGradientEndColor = middle;
        vPaint = new GradientPaint(0, vHighlightInset, vGradientStartColor, 0,
                vHighlightInset + (vButtonHighlightHeight / 2), middle, false);

        // Paint the second layer of the button
        bg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        bg.setPaint(vPaint);
        bg.setClip(new Rectangle2D.Float(vHighlightInset, vHighlightInset, vButtonHighlightWidth,
                (float) 0.5 * vButtonHighlightHeight));
        bg.fillRect(vHighlightInset, vHighlightInset, vButtonHighlightWidth, vButtonHighlightHeight);

        // Draw our aqua button
        g2.drawImage(vBuffer, x, y, null);

        g2.setColor(middle);
        g2.drawLine(x, y + height - 1, x + width - 1, y + height - 1);

        return ColorUtils.calculateColor(start, end, 0.5);
    }

    /**
     * Based on code from Jon Lipskys Weblog
     * 
     * @url http://blog.elevenworks.com/?p=2 @param g2 @param x @param y @param
     *      width @param height @param text @param foreground @param gstart @param
     *      gend
     */
    public static Color paintOceanGradient(Graphics2D g2, int x, int y, int width, int height, Color start, Color end) {
        if ((width <= 0) || (height <= 0)) {
            return start;
        }
        g2 = (Graphics2D) g2.create();
        BufferedImage vBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = vBuffer.createGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the background of the button
        bg.setColor(UIManager.getColor("control"));
        bg.fillRect(0, 0, width, height);

        Color c1 = ColorUtils.calculateColor(start, end, 0.7);
        Color c2 = end;
        Color c3 = start;

        int gradient1Height = height / 4;
        int solidHeight = 2;

        // Create the gradient paint for the first layer of the button
        Paint vPaint = new GradientPaint(0, 0, c1, 0, gradient1Height - 1, c2, false);
        bg.setPaint(vPaint);
        // Paint the first layer of the button
        bg.fillRect(0, 0, width, gradient1Height);

        vPaint = new GradientPaint(0, gradient1Height, c2, 0, gradient1Height + solidHeight - 1, c2, false);
        bg.setPaint(vPaint);
        // Paint the first layer of the button
        bg.fillRect(0, gradient1Height, width, solidHeight);

        vPaint = new GradientPaint(0, gradient1Height + solidHeight, c2, 0, 2 * gradient1Height + solidHeight - 1, c1,
                false);
        bg.setPaint(vPaint);
        // Paint the first layer of the button
        bg.fillRect(0, gradient1Height + solidHeight, width, gradient1Height);

        int rest = height - (gradient1Height * 2 + solidHeight);
        vPaint = new GradientPaint(0, gradient1Height * 2 + solidHeight, c1, 0, height, c3, false);
        bg.setPaint(vPaint);
        // Paint the first layer of the button
        bg.fillRect(0, gradient1Height * 2 + solidHeight, width, rest);

        // Draw our aqua button
        g2.drawImage(vBuffer, x, y, null);

        return ColorUtils.calculateColor(start, end, 0.5);
    }
}
