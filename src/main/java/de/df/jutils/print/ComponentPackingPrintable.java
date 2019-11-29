/*
 * Created on 02.01.2005
 */
package de.df.jutils.print;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.*;
import javax.swing.border.LineBorder;

import de.df.jutils.gui.util.EDTUtils;

/**
 * Generic printing utility.
 * <p>
 * The original source code for this class only was obtained from
 * http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html
 * without restrictions as specified on their web site. To quote the link above:
 * <p>
 * <i>All source code freely available for unrestricted use. </i>
 * <p>
 * However, additional changes have been made to the original.
 */

public class ComponentPackingPrintable implements Printable {

    private Component[] components = null;
    private int         width      = 0;
    private int         height     = 0;
    private int         amountX    = 0;
    private int         amountY    = 0;
    private int         gapX       = 0;
    private int         gapY       = 0;
    private int         maxGapX    = -1;
    private int         maxGapY    = -1;
    private boolean     border     = true;

    public ComponentPackingPrintable(Component... components) {
        this(-1, -1, components);
    }

    public ComponentPackingPrintable(int mGapX, int mGapY, Component... components2) {
        this(mGapX, mGapY, true, components2);
    }

    public ComponentPackingPrintable(int mGapX, int mGapY, boolean border, Component... components) {
        if (components == null) {
            components = new Component[0];
        }
        this.border = border;
        this.components = components;
        setMaxGap(mGapX, mGapY);
        init();
    }

    public void setMaxGap(int x, int y) {
        maxGapX = x;
        maxGapY = y;
    }

    /**
     * @param components
     */
    private void init() {
        for (Component component1 : components) {
            Frame f = new Frame();
            f.add(component1);
            f.pack();
            f.removeAll();

            Dimension size = component1.getSize();
            if (width < size.width) {
                width = size.width;
            }
            if (height < size.height) {
                height = size.height;
            }
        }
        for (Component component : components) {
            component.setSize(width, height);
        }
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex >= components.length) {
            return NO_SUCH_PAGE;
        }

        if (amountX <= 0) {
            if (width > pageFormat.getImageableWidth()) {
                width = (int) pageFormat.getImageableWidth();
            }
            if (height > pageFormat.getImageableHeight()) {
                height = (int) pageFormat.getImageableHeight();
            }
            amountX = Math.max((int) pageFormat.getImageableWidth() / width, 1);
            amountY = Math.max((int) pageFormat.getImageableHeight() / height, 1);

            if (amountY >= components.length) {
                amountX = 1;
            }

            gapX = (int) pageFormat.getImageableWidth() - (width * amountX);
            gapY = (int) pageFormat.getImageableHeight() - (height * amountY);
            if (amountX <= 1) {
                gapX = 0;
            } else {
                gapX = gapX / (amountX - 1);
            }
            if (amountY <= 1) {
                gapY = 0;
            } else {
                gapY = gapY / (amountY - 1);
            }

            if (maxGapX >= 0) {
                int diff = Math.max(gapX - maxGapX, 0);
                int add = diff / amountX;
                gapX = gapX - amountX * add;
                width += add;
            }
            if (maxGapY >= 0) {
                int diff = Math.max(gapY - maxGapY, 0);
                int add = diff / amountY;
                gapY = gapY - amountY * add;
                height += add;
            }
        }

        int offset = amountX * amountY * pageIndex;
        if (offset >= components.length) {
            return NO_SUCH_PAGE;
        }

        // Without this the Component will not be printed
        JPanel panel = new JPanel(new GridLayout(amountY, amountX, gapX, gapY));
        // panel.setBorder(new EmptyBorder(gapY, gapX, gapY, gapX));
        panel.setBackground(Color.WHITE);
        panel.setForeground(Color.BLACK);

        for (int y = 0; y < amountY; y++) {
            for (int x = 0; x < amountX; x++) {
                int index = offset + amountX * y + x;
                Component component = null;
                if (index < components.length) {
                    component = components[index];
                    if (border) {
                        if (component instanceof JComponent) {
                            ((JComponent) component).setBorder(new LineBorder(Color.BLACK, 2));
                        } else {
                            JPanel p = new JPanel(new BorderLayout());
                            p.setBackground(Color.WHITE);
                            p.add(component, BorderLayout.CENTER);
                            p.setBorder(new LineBorder(Color.BLACK, 2));
                            component = p;
                        }
                    }
                } else {
                    component = new JPanel();
                    component.setBackground(Color.WHITE);
                }
                panel.add(component);
            }
        }

        Dimension size = new Dimension((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
        panel.setSize(size);
        panel.setMaximumSize(size);
        panel.setPreferredSize(size);
        panel.setMinimumSize(size);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setClip(0, 0, size.width, size.height);

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setUndecorated(true);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();

        disableDoubleBuffering(panel);
        EDTUtils.print(panel, g2d);
        enableDoubleBuffering(panel);

        // Cleanup
        frame.removeAll();
        frame.dispose();

        return PAGE_EXISTS;
    }

    private static void disableDoubleBuffering(Component c) {
        RepaintManager.currentManager(c).setDoubleBufferingEnabled(false);
    }

    private static void enableDoubleBuffering(Component c) {
        RepaintManager.currentManager(c).setDoubleBufferingEnabled(true);
    }
}