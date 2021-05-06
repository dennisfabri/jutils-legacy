package skt.swing.scroll;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.BitSet;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * @author mueller
 * @since 01.08.2005
 */
final class ScrollUtils {

    public static final int      DIRECTION_TOP_BIT      = 0;
    public static final int      DIRECTION_RIGHT_BIT    = 1;
    public static final int      DIRECTION_BOTTOM_BIT   = 2;
    public static final int      DIRECTION_LEFT_BIT     = 3;

    public static final int      DIRECTION_NONE         = 0;
    public static final int      DIRECTION_TOP          = 1;
    public static final int      DIRECTION_RIGHT        = 2;
    public static final int      DIRECTION_BOTTOM       = 4;
    public static final int      DIRECTION_LEFT         = 8;
    public static final int      DIRECTION_TOP_RIGHT    = 3;
    public static final int      DIRECTION_BOTTOM_RIGHT = 6;
    public static final int      DIRECTION_BOTTOM_LEFT  = 12;
    public static final int      DIRECTION_TOP_LEFT     = 9;
    public static final int      DIRECTIONS_ALL         = 15;

    private static final Polygon ARROW                  = new Polygon(new int[] { 6, 12, 8, 8, 4, 4, 0, 6 }, new int[] { 0, 6, 6, 12, 12, 6, 6, 0 }, 8);

    private ScrollUtils() {
        super();
    }

    static Image createImage(int bitmask) {
        BufferedImage image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw circles
        g.setColor(Color.GRAY);
        g.fillOval(0, 0, 30, 30);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 1.0f));
        g.setColor(Color.WHITE);
        g.fillOval(1, 1, 28, 28);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g.fillOval(1, 1, 28, 28);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(Color.BLACK);
        g.fillOval(13, 13, 4, 4);

        // Convert int bitmask to a BitSet for simpler use
        BitSet bits = new BitSet(4);
        for (int x = 0; x < 4; x++) {
            bits.set(x, (bitmask % 2) > 0);
            bitmask = bitmask >> 1;
        }

        // Draw Arrows
        // top
        if (bits.get(ScrollUtils.DIRECTION_TOP_BIT)) {
            g.fill(new Polygon(new int[] { 15, 11, 19 }, new int[] { 5, 9, 9 }, 3));
        } else {
            if (bits.get(ScrollUtils.DIRECTION_BOTTOM_BIT)) {
                g.setColor(Color.GRAY);
                g.fill(new Polygon(new int[] { 15, 11, 19 }, new int[] { 5, 9, 9 }, 3));
                g.setColor(Color.BLACK);
            }
        }

        g.rotate(Math.PI / 2, 15, 15);
        // right
        if (bits.get(ScrollUtils.DIRECTION_RIGHT_BIT)) {
            g.fill(new Polygon(new int[] { 15, 11, 19 }, new int[] { 5, 9, 9 }, 3));
        } else {
            if (bits.get(ScrollUtils.DIRECTION_LEFT_BIT)) {
                g.setColor(Color.GRAY);
                g.fill(new Polygon(new int[] { 15, 11, 19 }, new int[] { 5, 9, 9 }, 3));
                g.setColor(Color.BLACK);
            }
        }

        g.rotate(Math.PI / 2, 15, 15);
        // bottom
        if (bits.get(ScrollUtils.DIRECTION_BOTTOM_BIT)) {
            g.fill(new Polygon(new int[] { 15, 11, 19 }, new int[] { 5, 9, 9 }, 3));
        } else {
            if (bits.get(ScrollUtils.DIRECTION_TOP_BIT)) {
                g.setColor(Color.GRAY);
                g.fill(new Polygon(new int[] { 15, 11, 19 }, new int[] { 5, 9, 9 }, 3));
                g.setColor(Color.BLACK);
            }
        }

        g.rotate(Math.PI / 2, 15, 15);
        // left
        if (bits.get(ScrollUtils.DIRECTION_LEFT_BIT)) {
            g.fill(new Polygon(new int[] { 15, 11, 19 }, new int[] { 5, 9, 9 }, 3));
        } else {
            if (bits.get(ScrollUtils.DIRECTION_RIGHT_BIT)) {
                g.setColor(Color.GRAY);
                g.fill(new Polygon(new int[] { 15, 11, 19 }, new int[] { 5, 9, 9 }, 3));
                g.setColor(Color.BLACK);
            }
        }

        return image;
    }

    static Image[] createImages() {
        Image[] images = new Image[DIRECTIONS_ALL + 1];
        for (int x = 0; x < DIRECTIONS_ALL + 1; x++) {
            images[x] = createImage(x);
        }
        return images;
    }

    public static int getDirections(JViewport viewport) {
        if (!(viewport.getParent() instanceof JScrollPane)) {
            return ScrollUtils.DIRECTIONS_ALL;
        }
        // The binary operations on <directions> have been replaced by
        // integer substractions.
        int directions = 15;
        JScrollPane parent = (JScrollPane) viewport.getParent();

        if (parent != null) {
            // Check the vertical ScrollBar
            if (parent.getVerticalScrollBar() != null) {
                BoundedRangeModel brm = parent.getVerticalScrollBar().getModel();
                if (brm.getValue() == brm.getMinimum()) {
                    directions -= ScrollUtils.DIRECTION_TOP;
                }
                if (brm.getValue() == brm.getMaximum() - brm.getExtent()) {
                    directions -= ScrollUtils.DIRECTION_BOTTOM;
                }
            }

            // Check the horizontal ScrollBar
            if (parent.getHorizontalScrollBar() != null) {
                BoundedRangeModel brm = parent.getHorizontalScrollBar().getModel();
                if (brm.getValue() == brm.getMinimum()) {
                    directions -= ScrollUtils.DIRECTION_LEFT;
                }
                if (brm.getValue() == brm.getMaximum() - brm.getExtent()) {
                    directions -= ScrollUtils.DIRECTION_RIGHT;
                }
            }
        }
        return directions;
    }

    private static int directionToDegree(int direction) {
        switch (direction) {
        case DIRECTION_TOP:
            return 0;
        case DIRECTION_TOP_RIGHT:
            return 45;
        case DIRECTION_RIGHT:
            return 90;
        case DIRECTION_BOTTOM_RIGHT:
            return 135;
        case DIRECTION_BOTTOM:
            return 180;
        case DIRECTION_BOTTOM_LEFT:
            return 225;
        case DIRECTION_LEFT:
            return 270;
        case DIRECTION_TOP_LEFT:
            return 315;
        case 0:
            return -2;
        default:
            return -1;
        }
    }

    private static Cursor createCursor(int direction) {
        int degree = directionToDegree(direction);
        if (degree == -1) {
            return null;
        }
        BufferedImage cursor = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cursor.createGraphics();
        if (direction == 0) {
            g.setColor(Color.BLACK);
            g.fillOval(12, 12, 8, 8);
        } else {
            g.setColor(Color.BLACK);
            g.translate(9, 9);
            g.rotate(Math.PI / 180 * degree, 6, 6);
            g.fillPolygon(ARROW);
        }
        return Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(16, 16), "DirectionCursor" + direction);
    }

    public static Cursor[] createCursors() {
        Cursor[] cursors = new Cursor[DIRECTIONS_ALL];
        for (int x = 0; x < DIRECTIONS_ALL; x++) {
            cursors[x] = createCursor(x);
        }
        return cursors;
    }
}