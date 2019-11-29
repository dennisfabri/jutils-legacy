package de.df.jutils.gui.util;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.util.OSUtils;

public final class GraphicsUtils {

    private GraphicsUtils() {
        // Hide
    }

    /**
     * Method based on an entry in Chet Haase's Blog:
     * http://weblogs.java.net/blog/chet/archive/2007/01/font_hints_for.html
     * 
     * @param g2d
     *            Graphics object
     */
    public static void setDefaultRenderingHints(Graphics2D g2d) {
        Map<?, ?> desktopHints = (Map<?, ?>) (Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints"));
        if (desktopHints != null) {
            g2d.addRenderingHints(desktopHints);
        }
    }

    private static void drawGradient(Graphics g, int x1, int y1, int x2, int y2, Color start, Color end) {
        Color c = g.getColor();
        float divisor = x2 - x1;
        for (int x = x1; x < x2 + 1; x++) {
            float divident = x - x1;
            float percent = divident / divisor;
            g.setColor(ColorUtils.calculateColor(start, end, percent));
            g.drawLine(x, y1, x, y2);
        }
        g.setColor(c);
    }

    public static enum Gradients {
        Horizontal, Ocean, Royal, Aqua, KDE, GTK, Vista, CDE, FlatGray, FlatGray2, FlatColor
    }

    private static Gradients defaultmode = Gradients.Royal;

    public static void setGradient(Gradients i) {
        defaultmode = i;
    }

    public static Gradients getGradient() {
        return defaultmode;
    }

    static {
        if (OSUtils.isWindows()) {
            if (OSUtils.isWindows2000()) {
                defaultmode = Gradients.Horizontal;
            } else if (OSUtils.isWindowsXP()) {
                defaultmode = Gradients.Royal;
            } else {
                defaultmode = Gradients.FlatGray2;
            }
        } else {
            if (OSUtils.isLinux()) {
                defaultmode = Gradients.FlatGray2;
            } else {
                if (OSUtils.isMacOSX()) {
                    defaultmode = Gradients.Aqua;
                } else {
                    defaultmode = Gradients.Horizontal;
                }
            }
        }
        LookAndFeel laf = UIManager.getLookAndFeel();
        // String name = laf.getName();
        String cname = laf.getClass().getName();
        if (laf instanceof MetalLookAndFeel) {
            defaultmode = Gradients.Ocean;
        }
        // if (name.equals("CDE/Motif")) {
        // defaultmode = Gradients.CDE;
        // }
        if (cname.equals("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel")) {
            defaultmode = Gradients.Horizontal;
        }
        // if (cname.startsWith("com.jgoodies.looks.plastic")) {
        // defaultmode = Gradients.Horizontal;
        // }

    }

    public static Color paintGradient(Graphics2D g2, int x, int y, int width, int height, Color gstart, Color gend) {
        if ((width <= 0) || (height <= 0)) {
            return gstart;
        }
        return paintGradient(g2, x, y, width, height, gstart, gend, defaultmode);
    }

    private static Color paintGradient(Graphics2D g2, int x, int y, int width, int height, Color gstart, Color gend, Gradients mode) {
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
        case Aqua:
            return paintAquaGradient(g2, x, y, width, height, gstart, gend);
        case Royal:
            return paintRoyalGradient(g2, x, y, width, height, gstart, gend);
        case Ocean:
            return paintOceanGradient(g2, x, y, width, height, gstart, gend);
        case KDE:
            return paintKDEGradient(g2, x, y, width, height, gstart, gend);
        case GTK:
            return paintGTKGradient(g2, x, y, width, height, gstart, gend);
        case Vista:
            return paintVistaGradient(g2, x, y, width, height, gstart, gend);
        case CDE:
            return paintCDEGradient(g2, x, y, width, height, gstart, gend);
        case FlatGray:
            return paintFlatGradient(g2, x, y, width, height, gstart, gend);
        case FlatGray2:
            return paintFlat2Gradient(g2, x, y, width, height, gstart, gend);
        case FlatColor:
            return paintFlatColorGradient(g2, x, y, width, height, gstart, gend);
        default:
        case Horizontal:
            return paintHorizontalGradient(g2, x, y, width, height, gstart, gend, false);
        }
    }

    public static Color getGradientBorderColor() {
        switch (defaultmode) {
        case FlatColor:
            return ColorUtils.brighter(UIManager.getColor("List.selectionBackground"), 0.4f);
        case FlatGray2:
            return UIManager.getColor("ComboBox.buttonShadow");
        // return ColorUtils.calculateColor(UIManager.getColor("control"),
        // UIManager.getColor("InternalFrame.activeBorderColor"), 0.3);
        default:
            return UIManager.getColor("ComboBox.buttonShadow");
        }
    }

    public static Color paintHorizontalGradient(Graphics g, final int x1, final int y1, final int w, final int h, Color start, Color end,
            boolean rightBordered) {
        if ((w <= 0) || (h <= 0)) {
            return start;
        }
        g = g.create();
        Color backup = g.getColor();
        Font fbackup = g.getFont();

        Font f = UIManager.getFont("TitledBorder.font");
        g.setFont(f);

        int x2 = x1 + w - 1;
        int y2 = y1 + h - 1;
        // Color end = c.getBackground();
        GraphicsUtils.drawGradient(g, x1, y1, x2, y2, start, end);

        g.setColor(UIManager.getColor("controlShadow"));
        g.drawLine(x1, y2, x2, y2);
        if (rightBordered) {
            g.drawLine(x2, y1, x2, y2);
        }
        g.setColor(UIManager.getColor("controlHighlight"));
        g.drawLine(x1, y2 - 1, x1, y1);
        g.drawLine(x1, y1, x2, y1);

        g.setColor(backup);
        g.setFont(fbackup);

        return ColorUtils.calculateColor(start, end, 0.5);
    }

    public static Color paintFlatColorGradient(Graphics g, final int x1, final int y1, final int w, final int h, Color start, Color end) {
        if ((w <= 0) || (h <= 0)) {
            return start;
        }
        g = g.create();
        Color backup = g.getColor();
        Font fbackup = g.getFont();

        Font f = UIManager.getFont("TitledBorder.font");
        g.setFont(f);

        int x2 = x1 + w - 1;
        int y2 = y1 + h - 1;
        // Color end = c.getBackground();
        start = ColorUtils.brighter(UIManager.getColor("List.selectionBackground"), 0.4f);
        GraphicsUtils.drawGradient(g, x1, y1, x2, y2, start, start);

        // g.setColor(UIManager.getColor("controlShadow"));
        // g.drawLine(x1, y2, x2, y2);
        // if (rightBordered) {
        // g.drawLine(x2, y1, x2, y2);
        // }
        // g.setColor(UIManager.getColor("controlHighlight"));
        // g.drawLine(x1, y2 - 1, x1, y1);
        // g.drawLine(x1, y1, x2, y1);

        g.setColor(backup);
        g.setFont(fbackup);

        return start;
    }

    public static Color paintFlatGradient(Graphics g2, final int x, final int y, final int width, final int height, Color start, Color end) {
        if ((width <= 0) || (height <= 0)) {
            return start;
        }
        BufferedImage vBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = vBuffer.createGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color background = ColorUtils.calculateColor(UIManager.getColor("control"), UIManager.getColor("InternalFrame.activeBorderColor"), 0.3);

        // Paint the background of the button
        bg.setColor(background);
        bg.fillRect(0, 0, width, height);

        // bg.setColor(start);
        bg.setColor(UIManager.getColor("InternalFrame.activeBorderColor"));
        // bg.setColor(end);
        // bg.draw3DRect(0, 0, width - 1, height - 1, true);
        bg.drawLine(0, height - 1, width - 1, height - 1);

        // Draw our aqua button
        g2.drawImage(vBuffer, x, y, null);

        return background;
    }

    public static Color paintFlat2Gradient(Graphics g2, final int x, final int y, final int width, final int height, Color start, Color end) {
        if ((width <= 0) || (height <= 0)) {
            return start;
        }
        BufferedImage vBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = vBuffer.createGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color background = ColorUtils.calculateColor(UIManager.getColor("control"), UIManager.getColor("ComboBox.buttonShadow"), 0.3);

        // Paint the background of the button
        bg.setColor(background);
        bg.fillRect(0, 0, width, height);

        // bg.setColor(start);
        // bg.setColor(background);
        // bg.setColor(end);
        // bg.draw3DRect(0, 0, width - 1, height - 1, true);
        // bg.drawLine(0, height - 1, width - 1, height - 1);

        // Draw our aqua button
        g2.drawImage(vBuffer, x, y, null);

        return background;
    }

    /**
     * Based on code from Jon Lipskys Weblog
     * 
     * @url http://blog.elevenworks.com/?p=2 @param g2 @param x @param y @param width @param height @param text @param
     *      foreground @param gstart @param gend
     */
    public static Color paintAquaGradient(Graphics2D g2, int x, int y, int width, int height, Color start, Color end) {
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
        vPaint = new GradientPaint(0, vHighlightInset, vGradientStartColor, 0, vHighlightInset + (vButtonHighlightHeight / 2), middle, false);

        // Paint the second layer of the button
        bg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        bg.setPaint(vPaint);
        bg.setClip(new Rectangle2D.Float(vHighlightInset, vHighlightInset, vButtonHighlightWidth, (float) 0.5 * vButtonHighlightHeight));
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
     * @url http://blog.elevenworks.com/?p=2 @param g2 @param x @param y @param width @param height @param text @param
     *      foreground @param gstart @param gend
     */
    public static Color paintRoyalGradient(Graphics2D g2, int x, int y, int width, int height, Color start, Color end) {
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

        Color c1 = ColorUtils.calculateColor(Color.WHITE, end, 1);
        Color c2 = ColorUtils.calculateColor(start, end, 0.6);
        Color c4 = ColorUtils.calculateColor(start, end, 0.4);
        Color c5 = start.darker();

        // Create the gradient paint for the first layer of the button
        Paint vPaint = new GradientPaint(0, 0, c1, 0, height / 2 - 3, c2, false);
        bg.setPaint(vPaint);

        // Paint the first layer of the button
        bg.fillRect(0, 0, width, height / 2 - 2);

        // bg.setPaint(null);
        // bg.setColor(c3);
        // bg.fillRect(0, height / 3, width, height / 3 + 1);

        // Create the gradient paint for the first layer of the button
        vPaint = new GradientPaint(0, height / 2 - 2, c4, 0, height, c5, false);
        bg.setPaint(vPaint);

        // Paint the first layer of the button
        bg.fillRect(0, height / 2 - 2, width, height - (height / 2 - 2));

        // Draw our aqua button
        g2.drawImage(vBuffer, x, y, null);

        return ColorUtils.calculateColor(start, end, 0.5);
    }

    public static Color paintVistaGradient(Graphics2D g2, int x, int y, int width, int height, Color start, Color end) {
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

        Color c1 = ColorUtils.calculateColor(Color.WHITE, end, 1);
        Color c2 = ColorUtils.calculateColor(start, end, 0.4);
        Color c3 = start;

        bg.setColor(c3);
        // Paint the first layer of the button
        bg.fillRect(0, height / 2, width, height / 2);

        int h2 = height / 2;
        for (int i = 0; i < h2; i++) {
            double percent = h2 - i;
            percent = percent / (h2 - 1);
            Color current = ColorUtils.calculateColor(c2, c1, percent * percent);
            bg.setColor(current);
            bg.drawLine(0, i, width, i);
        }

        // Draw our aqua button
        g2.drawImage(vBuffer, x, y, null);

        return ColorUtils.calculateColor(start, end, 0.5);
    }

    public static Color paintGTKGradient(Graphics2D g2, int x, int y, int width, int height, Color start, Color end) {
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

        Color c1 = end;
        Color c2 = ColorUtils.calculateColor(start, end, 0.7);
        Color c3 = ColorUtils.calculateColor(start, end, 0.5);
        Color c4 = ColorUtils.calculateColor(start, end, 0.25);
        Color c5 = start;

        bg.setColor(c5);
        bg.drawLine(0, height - 1, width - 1, height - 1);
        bg.drawLine(width - 1, 0, width - 1, height - 1);

        bg.setColor(c1);
        bg.drawLine(0, 0, width - 1, 0);
        bg.drawLine(0, 0, 0, height - 1);

        int h2 = (height - 2) / 2;
        for (int i = 1; i <= h2; i++) {
            double percent = h2 - i;
            percent = percent / h2;
            Color current = ColorUtils.calculateColor(c3, c2, percent * percent);
            bg.setColor(current);
            bg.drawLine(1, i, width - 2, i);
        }

        for (int i = 1; i <= h2; i++) {
            double percent = h2 - i;
            percent = percent / h2;
            Color current = ColorUtils.calculateColor(c4, c3, percent * percent);
            bg.setColor(current);
            bg.drawLine(1, i + h2, width - 2, i + h2);
        }

        // Draw our aqua button
        g2.drawImage(vBuffer, x, y, null);

        return ColorUtils.calculateColor(start, end, 0.5);
    }

    public static Color paintKDEGradient(Graphics2D g2, int x, int y, int width, int height, Color start, Color end) {
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

        Color c1 = ColorUtils.calculateColor(start, end, 1);
        Color c2 = ColorUtils.calculateColor(start, end, 0);
        Color c3 = ColorUtils.calculateColor(start, end, 0.7);

        int h7 = height / 5;
        if (h7 < 2) {
            h7 = 2;
        }

        // Create the gradient paint for the first layer of the button
        Paint vPaint = new GradientPaint(0, 0, c1, 0, h7 - 1, c2, false);
        bg.setPaint(vPaint);

        // Paint the first layer of the button
        bg.fillRect(0, 0, width, h7);

        // bg.setPaint(null);
        // bg.setColor(c3);
        // bg.fillRect(0, height / 3, width, height / 3 + 1);

        // Create the gradient paint for the first layer of the button
        vPaint = new GradientPaint(0, h7, c2, 0, height, c3, false);
        bg.setPaint(vPaint);

        // Paint the first layer of the button
        bg.fillRect(0, h7, width, height - h7);

        // Draw our aqua button
        g2.drawImage(vBuffer, x, y, null);

        return ColorUtils.calculateColor(start, end, 0.5);
    }

    /**
     * Based on code from Jon Lipskys Weblog
     * 
     * @url http://blog.elevenworks.com/?p=2 @param g2 @param x @param y @param width @param height @param text @param
     *      foreground @param gstart @param gend
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

        vPaint = new GradientPaint(0, gradient1Height + solidHeight, c2, 0, 2 * gradient1Height + solidHeight - 1, c1, false);
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

    public static Color paintCDEGradient(Graphics2D g2, int x, int y, int width, int height, Color start, Color end) {
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

        bg.setColor(start);
        bg.setColor(end);
        bg.draw3DRect(0, 0, width - 1, height - 1, true);
        // Draw our aqua button
        g2.drawImage(vBuffer, x, y, null);

        return ColorUtils.calculateColor(start, end, 0.5);
    }
}