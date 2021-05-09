/*
 * Created on 18.01.2005
 */
package de.df.jutils.print.printables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JFrame;
import javax.swing.RepaintManager;

import de.df.jutils.gui.util.EDTUtils;

/**
 * @author Mueller
 */
public class ExtendedHeaderFooterPrintable implements Printable {

    private Font            font   = null;
    private Component       header = null;
    private Component       footer = null;
    private Printable       source = null;

    private int             halign = 1;
    private int             falign = 1;

    public static final int LEFT   = 0;
    public static final int CENTER = 1;
    public static final int RIGHT  = 2;
    public static final int FILL   = 3;

    public ExtendedHeaderFooterPrintable(Printable main, Component header, Component footer, Font f) {
        this(main, header, footer, FILL, FILL, f);
    }

    public ExtendedHeaderFooterPrintable(Printable main, Component header, Component footer, int alignh, int alignf, Font font) {
        if (main == null) {
            throw new NullPointerException();
        }
        source = main;
        setHeader(header);
        setFooter(footer);
        setFont(font);
        halign = alignh;
        falign = alignf;
    }

    public void setFont(Font f) {
        font = f;
    }

    public void setFooter(Component f) {
        footer = f;
    }

    public void setHeader(Component f) {
        header = f;
    }

    private void print(Component c, Graphics2D g2d, PageFormat pf, double y, int align) {
        if (c == null) {
            return;
        }

        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);

        EDTUtils.setFont(c, font);
        int width = c.getWidth();
        double x = pf.getImageableX() + 1;
        switch (align) {
        case CENTER:
            x += Math.max(0, (pf.getImageableWidth() - width) / 2);
            break;
        case RIGHT:
            x += Math.max(0, pf.getImageableWidth() - width);
            break;
        case LEFT:
        case FILL:
        default:
            c.setSize((int) pf.getImageableWidth() - 2, c.getHeight());
            break;
        }
        g2d = (Graphics2D) g2d.create();
        g2d.translate(x, y);
        c.print(g2d);

        // EDTUtils.sleep();

        currentManager.setDoubleBufferingEnabled(true);
    }

    private boolean noHeader() {
        return header == null;
    }

    private boolean noFooter() {
        return footer == null;
    }

    private boolean noHeadersAndFooters() {
        return noFooter() && noHeader();
    }

    private boolean init = true;

    private static class Initializer implements Runnable {

        private Component  header;
        private PageFormat pf;

        public Initializer(Component h, PageFormat p) {
            header = h;
            pf = p;
        }

        @Override
        public void run() {
            header.setSize((int) pf.getImageableWidth(), header.getHeight());
            JFrame f = new JFrame();
            f.add(header);
            f.pack();
            int diff = (int) pf.getImageableWidth() - header.getWidth();
            f.setSize(f.getWidth() + diff, f.getHeight());
            f.validate();
        }
    }

    private void prepare(Graphics g, PageFormat pf, int index) {
        if (init) {
            init = false;
            if (!noHeader()) {
                EDTUtils.executeOnEDT(new Initializer(header, pf));
            }
            if (!noFooter()) {
                EDTUtils.executeOnEDT(new Initializer(footer, pf));
            }
            EDTUtils.sleep();
        }
    }

    private void printHeaderAndFooter(Graphics g, PageFormat pf, int index) {
        {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.WHITE);
            // g2d
            // .fillRect((int) pf.getImageableX(), (int) pf
            // .getImageableY(), (int) pf.getImageableWidth(),
            // (int) pf.getImageableHeight());

            // Draw Headers and Footers
            if (font != null) {
                g2d.setFont(font);
            }

            g2d.setBackground(Color.WHITE);
            g2d.setColor(Color.BLACK);

            for (int x = 0; x < 3; x++) {
                if (!noHeader()) {
                    print(header, (Graphics2D) g2d.create(), pf, (int) pf.getImageableY(), halign);
                }
                if (!noFooter()) {
                    print(footer, (Graphics2D) g2d.create(), pf, (int) (pf.getImageableY() + pf.getImageableHeight() - footer.getHeight()), falign);
                }
            }
        }
    }

    private int printInner(Graphics g2d, PageFormat pf, int index) throws PrinterException {
        PageFormat ranged = calculateRangedPaperFormat(pf);
        if ((ranged.getImageableHeight() <= 0) || (ranged.getImageableWidth() <= 0)) {
            return NO_SUCH_PAGE;
        }

        g2d.setClip((int) ranged.getImageableX() - 1, (int) ranged.getImageableY() - 1, (int) ranged.getImageableWidth() + 2,
                (int) ranged.getImageableHeight() + 2);

        // Print central area
        return source.print(g2d, ranged, index);
    }

    private PageFormat calculateRangedPaperFormat(PageFormat pf) {
        Paper page = pf.getPaper();
        PageFormat ranged = null;
        if (noHeadersAndFooters()) {
            ranged = pf;
        } else {
            int height1 = (header == null ? 0 : header.getHeight());
            int height2 = (footer == null ? 0 : footer.getHeight());

            // Creating Pageformt for central area
            ranged = new PageFormat();
            Paper p = new Paper();
            p.setSize(page.getWidth(), page.getHeight());
            switch (pf.getOrientation()) {
            case PageFormat.PORTRAIT:
                p.setImageableArea(page.getImageableX(), page.getImageableY() + height1, page.getImageableWidth(),
                        page.getImageableHeight() - height1 - height2);
                break;
            case PageFormat.LANDSCAPE:
            case PageFormat.REVERSE_LANDSCAPE:
                p.setImageableArea(page.getImageableX() + height1, page.getImageableY(), page.getImageableWidth() - height1 - height2,
                        page.getImageableHeight());
                break;
            default:
                break;
            }
            ranged.setOrientation(pf.getOrientation());
            ranged.setPaper(p);
        }
        return ranged;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.print.Printable#print(java.awt.Graphics,
     * java.awt.print.PageFormat, int)
     */
    @Override
    public int print(Graphics g, PageFormat pf, int index) throws PrinterException {
        // EDTUtils.sleep();
        prepare(g.create(), pf, index);
        int result = printInner(g.create(), pf, index);
        if (result == PAGE_EXISTS) {
            // EDTUtils.sleep();
            printHeaderAndFooter(g.create(), pf, index);
            // EDTUtils.sleep();
        }
        return result;
    }
}