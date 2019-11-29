/*
 * Created on 31.10.2004
 */
package de.df.jutils.print;

import java.awt.*;
import java.awt.print.*;
import java.util.*;

import de.df.jutils.gui.util.EDTUtils;

/**
 * @author Dennis Mueller
 * @date 31.10.2004
 */
public class MultiplePrintable implements Printable {

    /**
     * Holds the number of the last page given by print
     */
    private int                     lastPage     = -1;
    /**
     * Holds the pageIndex which was 0 of the current printable
     */
    private int                     lastZeroPage = -1;
    /**
     * Stores all printables that may be used
     */
    private LinkedList<Printable>   printables   = new LinkedList<Printable>();
    /**
     * Holds the pointer to the currently used printable
     */
    private ListIterator<Printable> iterator     = printables.listIterator();
    private Printable               printable    = null;

    public MultiplePrintable() {
        super();
    }

    public MultiplePrintable(Collection<Printable> c) {
        addAll(c);
    }

    public MultiplePrintable(Printable... c) {
        for (Printable aC : c) {
            add(aC);
        }
    }

    public void addAll(Collection<Printable> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        for (Printable aC : c) {
            add(aC);
        }
    }

    public void add(Printable p) {
        if (p == null) {
            throw new NullPointerException();
        }
        if (p instanceof MultiplePrintable) {
            addAll(((MultiplePrintable) p).printables);
        } else {
            if (!(p instanceof EmptyPrintable)) {
                printables.addLast(p);
            }
        }
    }

    public int getPrintableCount() {
        return printables.size();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.print.Printable#print(java.awt.Graphics,
     * java.awt.print.PageFormat, int)
     */
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        EDTUtils.sleep();
        if (pageIndex == 0) {
            lastPage = -1;
            printable = null;
        }
        if (pageIndex == lastPage) {
            return printLastPage(g, pf, pageIndex);
        }
        return printNewPage(g, pf, pageIndex);
    }

    private int printLastPage(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        return printable.print(g, pf, pageIndex - lastZeroPage);
    }

    private static boolean isLandscape(PageFormat pf) {
        switch (pf.getOrientation()) {
        case PageFormat.LANDSCAPE:
        case PageFormat.REVERSE_LANDSCAPE:
            return true;
        default:
            return false;
        }
    }

    private int printNewPage(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > lastPage + 1) {
            // This has to be done for correct printing
            // if the user wants to start with an index>0
            while (pageIndex > lastPage + 1) {
                printNewPage(g, pf, lastPage + 1);
            }
            Graphics2D g2d = (Graphics2D) g;
            int x = (int) pf.getImageableX();
            int y = (int) pf.getImageableY();
            int width = (int) pf.getImageableWidth();
            int height = (int) pf.getImageableHeight();

            if (isLandscape(pf)) {
                x = (int) pf.getImageableY();
                y = (int) pf.getImageableX();
                width = (int) pf.getImageableHeight();
                height = (int) pf.getImageableWidth();
            }
            Color color = g2d.getColor();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(color);
        }
        if (pageIndex != lastPage + 1) {
            throw new PrinterException("Wrong order of pages!");
        }
        lastPage = pageIndex;
        if (printables.size() == 0) {
            return NO_SUCH_PAGE;
        }
        if (printable == null) {
            if (pageIndex != 0) {
                throw new PrinterException("Wrong order of pages (Has to start with 0)");
            }
            iterator = printables.listIterator();
            printable = iterator.next();
            lastZeroPage = 0;
        }

        try {
            int result = printable.print(g, pf, pageIndex - lastZeroPage);
            if (result == NO_SUCH_PAGE) {
                lastZeroPage = pageIndex;
                do {
                    if (iterator.hasNext()) {
                        printable = iterator.next();
                        result = printable.print(g, pf, pageIndex - lastZeroPage);
                    } else {
                        return NO_SUCH_PAGE;
                    }
                } while (result == NO_SUCH_PAGE);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return NO_SUCH_PAGE;
        }
    }
}
