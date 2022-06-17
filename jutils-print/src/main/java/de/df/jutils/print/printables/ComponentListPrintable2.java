/*
 * Created on 02.01.2005
 */
package de.df.jutils.print.printables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.border.LineBorder;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.layout.FormLayoutUtils;
import de.df.jutils.gui.layout.ListLayout;
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
public class ComponentListPrintable2 implements Printable {

    private Component[] components = null;
    private int gapY = 0;

    private int lastindex = -1;
    private int offset = 0;
    private int nextoffset = 0;

    private boolean border = true;

    public ComponentListPrintable2(boolean border, Component... components) {
        this(0, border, components);
    }

    public ComponentListPrintable2(Component... components) {
        this(0, true, components);
    }

    public ComponentListPrintable2(int gap, Component... components) {
        this(gap, true, components);
    }

    public ComponentListPrintable2(int gap, boolean border, Component... components) {
        if (components == null) {
            throw new NullPointerException();
        }
        setDrawBorder(border);
        this.components = components;
        setGap(gap);
    }

    public void setGap(int gap) {
        this.gapY = gap;
    }

    public void setDrawBorder(boolean b) {
        border = b;
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex >= components.length) {
            return NO_SUCH_PAGE;
        }

        if (lastindex < pageIndex) {
            lastindex = pageIndex;
            offset = nextoffset;
        }

        if (offset >= components.length) {
            return NO_SUCH_PAGE;
        }

        BufferedImage i = new BufferedImage((int) pageFormat.getWidth(), (int) pageFormat.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = i.createGraphics();

        EDTUtils.executeOnEDT(new PagePrinter(g2, pageFormat));
        EDTUtils.sleep();
        EDTUtils.executeOnEDT(new PagePrinter(g, pageFormat));

        return PAGE_EXISTS;
    }

    private class PagePrinter implements Runnable {

        private Graphics g;
        private PageFormat pf;

        public PagePrinter(Graphics gr, PageFormat p) {
            g = gr;
            pf = p;
        }

        @Override
        public void run() {
            printPage(g, pf);
        }
    }

    /**
     * @param g
     * @param pageFormat
     * @param offset
     */
    void printPage(Graphics g, PageFormat pageFormat) {
        Dimension pagesize = new Dimension((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

        int vgap = 0;
        {
            JPanel test = new JPanel(new FormLayout("fill:default:grow", FormLayoutUtils.createLayoutString(0, gapY)));
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            frame.setPreferredSize(pagesize);
            frame.setMinimumSize(pagesize);
            frame.setMaximumSize(pagesize);
            frame.setLayout(new ListLayout(0));
            frame.add(test);
            frame.pack();

            vgap = test.getHeight();

            frame.removeAll();
            frame.dispose();
        }

        int height = 0;
        int x = 0;
        while ((height < pagesize.height) && (offset + x < components.length)) {
            for (int i = 0; i < 1; i++) {
                Component component = components[offset + x];
                if (component instanceof JComponent c) {
                    if (border) {
                        c.setBorder(new LineBorder(Color.BLACK, 1));
                    }
                } else {
                    JPanel p = new JPanel(new BorderLayout());
                    p.setBackground(Color.WHITE);
                    p.add(component, BorderLayout.CENTER);
                    if (border) {
                        p.setBorder(new LineBorder(Color.BLACK, 1));
                    }
                    component = p;
                }
                components[offset + x] = component;

                JFrame frame = new JFrame();
                frame.setUndecorated(true);
                frame.setPreferredSize(pagesize);
                frame.setMinimumSize(pagesize);
                frame.setMaximumSize(pagesize);
                frame.setLayout(new ListLayout(0));
                frame.add(component);
                frame.pack();

                frame.removeAll();
                frame.dispose();
            }

            height += components[offset + x].getHeight() + vgap;
            x++;
        }
        if ((x > 1) && (height - vgap > pagesize.height)) {
            x--;
        }

        // Without this the Component will not be printed
        JPanel panel = new JPanel(new FormLayout("fill:default:grow", FormLayoutUtils.createLayoutString(x, gapY)));
        panel.setBackground(Color.WHITE);
        panel.setForeground(Color.BLACK);

        for (int y = 0; y < x; y++) {
            int index = offset + y;
            Component component = null;
            if (index < components.length) {
                component = components[index];
                component.setBackground(Color.WHITE);
            } else {
                component = new JLabel();
                component.setBackground(Color.WHITE);
            }
            panel.add(component, CC.xy(1, 2 + 2 * y));
        }

        panel.setSize(pagesize);
        panel.setMaximumSize(pagesize);
        panel.setPreferredSize(pagesize);
        panel.setMinimumSize(pagesize);

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
        frame.dispose();

        nextoffset = offset + x;
    }

    private static void disableDoubleBuffering(Component c) {
        RepaintManager.currentManager(c).setDoubleBufferingEnabled(false);
    }

    private static void enableDoubleBuffering(Component c) {
        RepaintManager.currentManager(c).setDoubleBufferingEnabled(true);
    }
}