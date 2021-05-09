/*
 * Created on 31.10.2004
 */
package de.df.jutils.print.printables;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * @author Dennis Fabri
 * @date 31.10.2004
 */
public class RotatingPrintable implements Printable {

    private final Printable printable;

    public RotatingPrintable(Printable p) {
        if (p == null) {
            throw new NullPointerException("Printable must not be null");
        }
        printable = p;
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

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(0, pf.getHeight());
        g2d.rotate(1.5 * Math.PI);

        PageFormat pageformat = new PageFormat();
        pageformat.setPaper(pf.getPaper());
        pageformat.setOrientation(isLandscape(pf) ? PageFormat.PORTRAIT : PageFormat.LANDSCAPE);
        return printable.print(g2d, pageformat, pageIndex);
    }
}
