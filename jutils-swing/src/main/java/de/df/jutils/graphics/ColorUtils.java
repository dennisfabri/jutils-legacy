/*
 * Created on 08.12.2003
 */
package de.df.jutils.graphics;

import java.awt.Color;

/**
 * @author Dennis
 */
public final class ColorUtils {

    private ColorUtils() {
        // Never used
    }

    public static Color calculateColor(final Color start, final Color end, final double percent) {
        if (start == null) {
            if (end == null) {
                throw new NullPointerException();
            }
            return end;
        }
        if (end == null) {
            return start;
        }
        int red = Calculator.calculateValue(start.getRed(), end.getRed(), percent, 0, 255);
        int green = Calculator.calculateValue(start.getGreen(), end.getGreen(), percent, 0, 255);
        int blue = Calculator.calculateValue(start.getBlue(), end.getBlue(), percent, 0, 255);

        return new Color(red, green, blue);
    }

    public static Color calculateColor(final Color start, final Color end, final double current, final double max) {
        double percent = current / max;
        return calculateColor(start, end, percent);
    }

    public static Color invert(final Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return new Color(255 - r, 255 - g, 255 - b);
    }

    public static Color contrastColor(final Color base, final Color preferred) {
        Color foreground = preferred;
        boolean b1 = ColorUtils.isBright(foreground);
        boolean b2 = ColorUtils.isBright(base);
        if (b2) {
            if (b1) {
                foreground = ColorUtils.invert(foreground);
            }
        } else if (!b2) {
            if (!b1) {
                foreground = ColorUtils.invert(foreground);
            }
        }
        return foreground;
    }

    public static Color contrastColor(final Color color) {
        Color i = invert(color);
        if (!areSimilar(color, i, 40)) {
            return i;
        }
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        if ((r + g + b) / 3 > 127) {
            return Color.BLACK;
        }
        return Color.WHITE;
    }

    private static double rx = 0.7;
    private static double gx = 1.0;
    private static double bx = 0.4;

    public static boolean isBright(Color color) {
        double r = rx * color.getRed();
        double g = gx * color.getGreen();
        double b = bx * color.getBlue();
        return (r + g + b) > 400;
    }

    public static boolean areSimilar(Color c1, Color c2, int distance) {
        int p1 = 0;
        int p2 = 0;
        {
            int r = c1.getRed();
            int g = c1.getGreen();
            int b = c1.getBlue();
            p1 = (int) Math.round(Math.sqrt((r * r + g * g + b * b) / 3));
        }
        {
            int r = c2.getRed();
            int g = c2.getGreen();
            int b = c2.getBlue();
            p2 = (int) Math.round(Math.sqrt((r * r + g * g + b * b) / 3));
        }
        return Math.abs(p1 - p2) <= distance;
    }

    public static Color toGray(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int c = (int) Math.round(((double) r + g + b) / 3.0);
        return new Color(c, c, c);
    }

    public static Color toSaturated(Color source, float weight) {
        float[] hsb = color2HSB(source);
        hsb[1] = Math.max(0.0f, Math.min(1.0f, hsb[1] + (1.0f - hsb[1]) * weight));
        return hSB2Color(hsb[0], hsb[1], hsb[2]);
    }

    public static Color brighter(Color source, float weight) {
        float[] hsb = color2HSB(source);
        hsb[2] = Math.max(0.0f, Math.min(1.0f, hsb[2] + (1.0f - hsb[2]) * weight));
        return hSB2Color(hsb[0], hsb[1], hsb[2]);
    }

    public static float[] color2HSB(Color c) {
        return rGB2HSB(c.getRed(), c.getGreen(), c.getBlue());
    }

    public static float[] rGB2HSB(int r, int g, int b) {
        return Color.RGBtoHSB(r, g, b, null);
    }

    public static Color hSB2Color(float h, float s, float b) {
        return Color.getHSBColor(h, s, b);
    }
}
