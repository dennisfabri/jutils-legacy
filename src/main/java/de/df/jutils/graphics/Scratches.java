/*
 * Generator.java Created on 11. Oktober 2000, 17:25
 */

package de.df.jutils.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Dennis Mueller
 * @version 0.1
 */

public final class Scratches {

    private int[][] blatt      = null;

    private int     width      = 0;
    private int     height     = 0;
    private int     colorcount = 0;

    public Scratches(final int displaywidth, final int displayheight, final int colors) {
        this(displaywidth, displayheight, colors, 2);
    }

    public Scratches(final int displaywidth, final int displayheight, final int colors, final float roughness) {
        if (displaywidth < 0) {
            throw new IllegalArgumentException("Illegal size");
        }
        if (displayheight < 0) {
            throw new IllegalArgumentException("Illegal size");
        }
        if (colorcount < 0) {
            throw new IllegalArgumentException("Illegal colorcount");
        }
        height = displayheight;
        width = displaywidth;

        colorcount = colors;
        blatt = new int[width][height];
        draw(roughness / 8);
    }

    private void draw(final float roughness) {
        blatt[0][0] = fit(colorcount / 2 + random(colorcount, (float) (0.5 * colorcount)));

        int randomer = Math.round(colorcount * roughness);

        for (int x = 1; x < blatt.length; x++) {
            setPixel(x, 0, getPixel(x - 1, 0) + random(randomer, getPixel(x - 1, 0)));
        }
        for (int y = 1; y < blatt[0].length; y++) {
            setPixel(0, y, getPixel(0, y - 1) + random(randomer, getPixel(0, y - 1)));
        }
        for (int x = 1; x < blatt.length; x++) {
            for (int y = 1; y < blatt[x].length; y++) {
                float cul = getPixel(x - 1, y - 1);
                float cl = getPixel(x - 1, y);
                float cu = getPixel(x, y - 1);
                int color = Math.round((cul + cl + cu) / 3);
                setPixel(x, y, color + random(randomer, color));
            }
        }
    }

    private int getPixel(final int x, final int y) {
        return (blatt[x % width][y % height]);
    }

    private void setPixel(final int x, final int y, final int color) {
        blatt[x % width][y % height] = fit(color);
    }

    private int random(final float z, final float orig) {
        float z2 = z / 2;
        float value = (float) ((Math.random() * z) - z2);

        value = stretch(value, z2);

        if (value < 0) {
            if (z2 > orig) {
                value = ((value * orig) / z2);
            }
            assert value <= 0;
        } else {
            if (z2 >= colorcount - orig) {
                value = ((value * (colorcount - orig)) / z2);
            }
            assert value >= 0;
        }

        return Math.round(value);
    }

    private float stretch(final float value, final float range) {
        float seperator = 100;
        float move = 100;

        float prefix = 1;
        if (value < 0) {
            prefix = -1;
        }
        float v = value * prefix;

        if (v > range / seperator) {
            v = (range * (move - 1) / move) + ((v - range) * 2 / move);
        } else {
            v = v * 2 / move;
        }
        v *= prefix;
        return v;
    }

    private int fit(final int x) {
        if (x >= colorcount) {
            return colorcount - 1;
        }
        if (x < 0) {
            return 0;
        }
        return x;
    }

    public Image getImage(final Color[] colors) {
        return getImage(new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR), colors);
    }

    public Image getImage(final Image image, final Color[] colors) {
        draw(image.getGraphics(), colors);
        return image;
    }

    public void draw(final Graphics g, final Color[] colors) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g.setColor(colors[blatt[x][y]]);
                g.drawLine(x, y, x, y);
            }
        }
    }
}