/**
 * 
 */
package de.df.jutils.print.printables;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.SwingConstants;

import de.df.jutils.print.PrintManager;

public class BannerPrintable implements Printable {

    private BufferedImage iptop;
    private BufferedImage ipbottom;
    private BufferedImage iltop;
    private BufferedImage ilbottom;

    private ImageHeaderFooterPrintable ehfp;
    private PageFormat pageformat;

    private Printable inner;

    public BannerPrintable(Printable p, BufferedImage iptop, BufferedImage ipbottom, BufferedImage iltop,
            BufferedImage ilbottom) {
        if (p != null) {
            inner = p;
        } else {
            inner = EmptyPrintable.Instance;
        }
        this.iptop = iptop;
        this.ipbottom = ipbottom;
        this.iltop = iltop;
        this.ilbottom = ilbottom;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int index) throws PrinterException {
        BufferedImage top = isLandscape(pf) ? iltop : iptop;
        BufferedImage bottom = isLandscape(pf) ? ilbottom : ipbottom;
        if ((top == null) && (bottom == null)) {
            return inner.print(g, pf, index);
        }

        if ((ehfp == null) || (!pageformat.equals(pf))) {
            ehfp = new ImageHeaderFooterPrintable(inner, top, bottom, SwingConstants.CENTER, SwingConstants.CENTER,
                    PrintManager.getFont());
            pageformat = pf;
        }

        return ehfp.print(g, pf, index);
    }

    static boolean isLandscape(PageFormat pf) {
        return pf.getWidth() > pf.getHeight();
    }
}
