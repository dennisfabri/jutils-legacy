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

public final class Plasma {

    private int[][] blatt      = null;

    private int     size       = 0;
    private int     width      = 0;
    private int     height     = 0;
    private int     colorcount = 0;

    public Plasma(final int displaywidth, final int displayheight, final int colors) {
        this(displaywidth, displayheight, colors, 2);
    }

    public Plasma(final int displaywidth, final int displayheight, final int colors, final float roughness) {
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

        size = 1;
        int max = Math.max(height, width);
        while (size < max) {
            size = size << 1;
        }

        colorcount = colors;
        blatt = new int[size][size];
        draw(roughness);
    }

    private void draw(final float roughness) {
        int max2 = colorcount >> 1;
        blatt[0][0] = fit(max2 + random(colorcount, max2));

        int zufall = Math.round(colorcount * roughness);

        for (int b = size; b > 1; b = b >> 1) {
            for (int x = 0; x < size; x += b) {
                for (int y = 0; y < size; y += b) {
                    generate1(x, y, x + b, y + b, b, zufall);
                }
            }
            for (int x = 0; x < size; x += b) {
                for (int y = 0; y < size; y += b) {
                    generate2(x, y, x + b, y + b, b, zufall);
                }
            }
            if (zufall > 1) {
                zufall = zufall >> 1;
            }
        }
    }

    private void generate1(final int x, final int y, final int xb, final int yb, final int b, final int zufall) {
        if (b > 1) {
            int lo = getPixel(x, y);
            int ro = getPixel(xb, y);
            int lu = getPixel(x, yb);
            int ru = getPixel(xb, yb);

            int mitte = (lo + ro + lu + ru) >> 2;

            mitte += random(zufall, mitte);

            int xmitte = x + (b >> 1);
            int ymitte = y + (b >> 1);

            setPixel(xmitte, ymitte, fit(mitte));
        }
    }

    private void generate2(final int x, final int y, final int xb, final int yb, final int b, final int zufall) {
        if (b > 1) {
            int xmitte = x + (b >> 1);
            int ymitte = y + (b >> 1);

            int m = getPixel(xmitte, ymitte);
            int rm = getPixel(xmitte + b, ymitte);
            int bm = getPixel(xmitte, ymitte + b);

            int ro = getPixel(xb, y);
            int lu = getPixel(x, y + b);
            int ru = getPixel(x + b, yb);

            int rechts = (ro + ru + m + rm) >> 2;
            int unten = (lu + ru + m + bm) >> 2;

            rechts += random(zufall >> 1, rechts);
            unten += random(zufall >> 1, unten);

            rechts = fit(rechts);
            unten = fit(unten);

            setPixel(xmitte, yb, unten);
            setPixel(xb, ymitte, rechts);
        }
    }

    private int getPixel(final int x, final int y) {
        return (blatt[x % size][y % size]);
    }

    private void setPixel(final int x, final int y, final int color) {
        blatt[x % size][y % size] = color;
    }

    private int random(final float z, final float orig) {
        float z2 = z / 2;
        float value = (float) ((Math.random() * z) - z2);

        value = stretch(value, z2);

        if (value < 0) {
            if (z2 > orig) {
                value = ((value * orig) / z2);
            }
        } else {
            if (z2 >= colorcount - orig) {
                value = ((value * (colorcount - orig)) / z2);
            }
        }

        return Math.round(value);
    }

    private static final int SEPERATOR = 1000;
    private static final int MOVE      = 1000;

    private float stretch(final float value, final float range) {
        float seperator = SEPERATOR;
        float move = MOVE;

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
        Graphics g = image.getGraphics();
        draw(g, colors);
        return image;
    }

    public void draw(final Graphics g, final Color[] colors) {
        draw(g, 0, 0, colors);
    }

    public void draw(final Graphics g, final int px, final int py, final Color[] colors) {
        int pxw = px + width;
        int pyh = py + height;
        for (int x = px; x < pxw; x++) {
            for (int y = py; y < pyh; y++) {
                g.setColor(colors[blatt[x - px][y - py]]);
                g.drawLine(x, y, x, y);
            }
        }
    }
}