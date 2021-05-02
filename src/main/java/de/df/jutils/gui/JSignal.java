package de.df.jutils.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.pushingpixels.substance.api.colorscheme.BaseLightColorScheme;
import org.pushingpixels.substance.api.colorscheme.SubstanceColorScheme;
import org.pushingpixels.substance.api.painter.border.GlassBorderPainter;
import org.pushingpixels.substance.api.painter.border.SubstanceBorderPainter;
import org.pushingpixels.substance.api.painter.fill.GlassFillPainter;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.util.DesignInit;
import sl.shapes.RoundPolygon;

public class JSignal extends JComponent {

    private String        text      = "";
    private Color         basecolor = null;

    private static JLabel label     = new JLabel("", SwingConstants.CENTER);

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public JSignal(String text) {
        setText(text);
        setOpaque(true);
    }

    @Override
    public void paint(Graphics g) {
        // Create a copy of the graphics object
        Graphics2D g2 = (Graphics2D) g.create();

        // Calculate drawing area
        Insets i = getInsets();
        int x = i.left;
        int y = i.top;
        int width = getWidth() - i.left - i.right;
        int height = getHeight() - i.top - i.bottom;

        // Clear background
        g2.setBackground(getBackground());
        g2.clearRect(0, 0, getWidth(), getHeight());

        // Create shapes for the painted area
        Shape contour = new RoundPolygon(new Polygon(new int[] { x, x + width - 1, x + width - 1, x }, new int[] { y, y, y + height - 1, y + height - 1 }, 4),
                Math.min(width, height) / 2);
        Shape icontour = new RoundPolygon(
                new Polygon(new int[] { x + 1, x + width - 2, x + width - 2, x + 1 }, new int[] { y + 1, y + 1, y + height - 2, y + height - 2 }, 4),
                Math.min(width, height) / 2);

        // Calculate drawing colors
        Color foreground = Color.BLACK;
        if (getForeground() != null) {
            foreground = getForeground();
        }
        Color base = Color.GRAY;
        if (getBasecolor() != null) {
            base = getBasecolor();
        }

        // Adjust color if disabled
        if (!isEnabled()) {
            if (getBackground() != null) {
                base = getBackground();
            } else {
                base = ColorUtils.toGray(base);
            }
        }

        // Calculate ColorScheme based on base color
        SubstanceColorScheme scheme = new SimpleColorScheme(base, foreground);
        {

            // Paint main area
            GlassFillPainter painter = new GlassFillPainter();
            painter.paintContourBackground(g, this, width, height, contour, false, scheme, true);
        }
        {
            // Paint border
            SubstanceBorderPainter painter = new GlassBorderPainter();
            painter.paintBorder(g2, this, width, height, contour, icontour, scheme);
        }

        // Paint text
        paintText(g2, x, y, width, height, text, foreground, isEnabled());
    }

    private static void paintText(Graphics2D g2, int x, int y, int width, int height, String text, Color foreground, boolean enabled) {
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

            g2.translate(x, y);
            synchronized (label) {
                label.setText(text);
                label.setEnabled(enabled);
                label.setForeground(foreground);
                label.setBounds(0, 0, width, height);
                label.paint(g2);
            }
        }
    }

    public void setBasecolor(Color basecolor) {
        this.basecolor = basecolor;
    }

    public Color getBasecolor() {
        return basecolor;
    }
    
    private static class SimpleColorScheme extends BaseLightColorScheme {

        private final Color base;
        private final Color foreground;

        private Color       light1;
        private Color       light2;
        private Color       light3;

        private Color       dark1;
        private Color       dark2;

        public SimpleColorScheme(Color base, Color foreground) {
            super("");
            this.base = base;
            this.foreground = foreground;

            dark2 = base.darker();
            dark1 = ColorUtils.calculateColor(base, dark2, 0.5);

            light2 = base.brighter();
            light1 = ColorUtils.calculateColor(base, light2, 0.5);
            // light2 = ColorUtils.calculateColor(base, light3, 0.67);
            light3 = light2.brighter();
        }

        @Override
        public Color getDarkColor() {
            return dark1;
        }

        @Override
        public Color getExtraLightColor() {
            return light2;
        }

        @Override
        public Color getForegroundColor() {
            return foreground;
        }

        @Override
        public Color getLightColor() {
            return light1;
        }

        @Override
        public Color getMidColor() {
            return base;
        }

        @Override
        public Color getUltraDarkColor() {
            return dark2;
        }

        @Override
        public Color getUltraLightColor() {
            return light3;
        }
    }
}