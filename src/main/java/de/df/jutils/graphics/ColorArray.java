/*
 * Created on 19.01.2004
 */
package de.df.jutils.graphics;

import java.awt.Color;

/**
 * @author Dennis
 */
public final class ColorArray {

    public static final int BYTE_VALUES = 256;

    private ColorArray() {
        // This constructor is not needed
    }

    public static Color[] createGrayRange() {
        Color[] colors = new Color[BYTE_VALUES];
        for (int x = 0; x < colors.length; x++) {
            colors[x] = new Color(x, x, x);
        }
        return colors;
    }

    public static Color[] createGrayRange(final int steps) {
        return createRange(Color.BLACK, Color.WHITE, steps);
    }

    public static Color[] createRange(final Color start, final Color end, final int steps) {
        Color[] colors = new Color[steps];
        for (int x = 0; x < steps; x++) {
            colors[x] = ColorUtils.calculateColor(start, end, x, steps);
        }
        return colors;
    }

    public static Color[] createRange(final Color start, final Color end) {
        return createRange(start, end, BYTE_VALUES);
    }

    public static Color[] createRange(final Color[] colors, final int steps) {
        Color[][] color = new Color[colors.length][0];

        int stepsper = steps / colors.length;
        int rest = steps - (stepsper * (colors.length - 1));

        for (int x = 0; x < colors.length - 1; x++) {
            color[x] = createRange(colors[x], colors[x + 1], stepsper + 1);
        }
        color[color.length - 1] = createRange(colors[colors.length - 1], colors[0], rest + 1);
        Color[] result = new Color[steps];
        for (int x = 0; x < color.length; x++) {
            System.arraycopy(color[x], 1, result, x * stepsper, color[x].length - 1);
        }
        return result;
    }
}
