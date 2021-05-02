/*
 * Created on 02.01.2005
 */
package de.df.jutils.print;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RepaintManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import de.df.jutils.gui.layout.SimpleListBuilder;
import de.df.jutils.gui.util.EDTUtils;

/**
 * Generic printing utility.
 * <p>
 * The original source code for this class only was obtained from
 * http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html
 * without restrictions as specified on their web site. To quote the link above:
 * </p>
 * <i>All source code freely available for unrestricted use. </i>
 * <p>
 * However, additional changes have been made to the original.
 * </p>
 */

public class ComponentListPrintable implements Printable {

    private Component[] components = null;
    private int         width      = 0;
    private int         height     = 0;
    private int         amountY    = 0;
    private int         gapY       = 0;
    private int         maxGapY    = -1;
    private int         minGapY    = 0;

    private boolean     border     = true;

    public ComponentListPrintable(boolean border, Component... components) {
        this(0, -1, border, components);
    }

    public ComponentListPrintable(Component... components) {
        this(0, -1, true, components);
    }

    public ComponentListPrintable(int min, int max, Component... components) {
        this(min, max, true, components);
    }

    public ComponentListPrintable(int min, int max, boolean border, Component... components) {
        if (components == null) {
            throw new NullPointerException();
        }
        setDrawBorder(border);
        this.components = components;
        setGap(min, max);
        EDTUtils.executeOnEDT(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    public void setGap(int min, int max) {
        maxGapY = max;
        minGapY = min;
    }

    public void setDrawBorder(boolean b) {
        border = b;
    }

    /**
     * @param components
     */
    void init() {
        SimpleListBuilder slb = new SimpleListBuilder(1);
        Frame f = new Frame();

        for (Component component2 : components) {
            slb.add(component2);
        }

        f.add(new JScrollPane(slb.getPanel()), BorderLayout.CENTER);
        EDTUtils.pack(f);
        f.removeAll();

        for (Component component1 : components) {
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

        if (amountY <= 0) {
            width = (int) pageFormat.getImageableWidth();
            if (height > pageFormat.getImageableHeight()) {
                height = (int) pageFormat.getImageableHeight();
            }
            amountY = Math.max((int) pageFormat.getImageableHeight() / (height + minGapY), 1);

            gapY = (int) pageFormat.getImageableHeight() - (height * amountY);
            gapY = gapY / (amountY + 1);

            if (maxGapY >= 0) {
                int diff = Math.max(gapY - maxGapY, 0);
                int add = diff / amountY;
                gapY = gapY - amountY * add;
                height += add;
            }
        }

        int offset = amountY * pageIndex;
        if (offset >= components.length) {
            return NO_SUCH_PAGE;
        }

        EDTUtils.executeOnEDT(new PagePrinter(g, pageFormat, offset));

        return PAGE_EXISTS;
    }

    private class PagePrinter implements Runnable {

        private Graphics   g;
        private PageFormat pf;
        private int        offset;

        public PagePrinter(Graphics gr, PageFormat p, int o) {
            g = gr;
            pf = p;
            offset = o;
        }

        @Override
        public void run() {
            printPage(g, pf, offset);
        }
    }

    /**
     * @param g
     * @param pageFormat
     * @param offset
     */
    void printPage(Graphics g, PageFormat pageFormat, int offset) {
        // Without this the Component will not be printed
        JPanel panel = new JPanel(new GridLayout(amountY, 1, 0, gapY));
        panel.setBorder(new EmptyBorder(gapY, 0, gapY, 0));
        panel.setBackground(Color.WHITE);
        panel.setForeground(Color.BLACK);

        for (int y = 0; y < amountY; y++) {
            int index = offset + y;
            Component component = null;
            if (index < components.length) {
                component = components[index];
                component.setBackground(Color.WHITE);
                if (component instanceof JComponent) {
                    if (border) {
                        ((JComponent) component).setBorder(new LineBorder(Color.BLACK, 2));
                    }
                } else {
                    JPanel p = new JPanel(new BorderLayout());
                    p.setBackground(Color.WHITE);
                    p.add(component, BorderLayout.CENTER);
                    if (border) {
                        p.setBorder(new LineBorder(Color.BLACK, 2));
                    }
                    component = p;
                }
            } else {
                component = new JPanel();
                component.setBackground(Color.WHITE);
            }
            panel.add(component);
        }

        Dimension size = new Dimension((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
        panel.setSize(size);
        panel.setMaximumSize(size);
        panel.setPreferredSize(size);
        panel.setMinimumSize(size);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();

        disableDoubleBuffering(panel);
        panel.paint(g2d);
        enableDoubleBuffering(panel);

        // Cleanup
        frame.removeAll();
    }

    private static void disableDoubleBuffering(Component c) {
        RepaintManager.currentManager(c).setDoubleBufferingEnabled(false);
    }

    private static void enableDoubleBuffering(Component c) {
        RepaintManager.currentManager(c).setDoubleBufferingEnabled(true);
    }
}