/*
 * Created on 12.12.2005
 */
package de.df.jutils.print;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.LinkedList;

public class NotifyingPrintable implements Printable {

    private Printable                 printable;
    private LinkedList<PrintListener> listeners = new LinkedList<PrintListener>();

    public NotifyingPrintable(Printable p, PrintListener pl) {
        printable = p;
        addListener(pl);
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        firePage(pageIndex);
        int result = 0;
        try {
            result = printable.print(graphics, pageFormat, pageIndex);
        } catch (Exception e) {
            e.printStackTrace();
            result = NO_SUCH_PAGE;
        }
        fireResult(pageIndex, result);
        return result;
    }

    public void addListener(PrintListener pl) {
        if (pl != null) {
            listeners.addLast(pl);
        }
    }

    private void firePage(int index) {
        for (PrintListener pl : listeners) {
            pl.printingPage(index);
        }
    }

    private void fireResult(int index, int result) {
        for (PrintListener pl : listeners) {
            pl.finishedPage(index, result);
        }
    }

    public static interface PrintListener {
        void printingPage(int index);

        void finishedPage(int index, int result);
    }
}
