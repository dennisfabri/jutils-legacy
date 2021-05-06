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

    private BufferedImage              iptop      = null;
    private BufferedImage              ipbottom   = null;
    private BufferedImage              iltop      = null;
    private BufferedImage              ilbottom   = null;

    private ImageHeaderFooterPrintable ehfp       = null;
    private PageFormat                 pageformat = null;

    private Printable                  inner;

    public BannerPrintable(Printable p, BufferedImage iptop, BufferedImage ipbottom, BufferedImage iltop, BufferedImage ilbottom) {
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
        BufferedImage top = null;
        BufferedImage bottom = null;
        if (pf.getOrientation() == PageFormat.PORTRAIT) {
            top = iptop;
            bottom = ipbottom;
        } else {
            top = iltop;
            bottom = ilbottom;
        }
        if ((top == null) && (bottom == null)) {
            return inner.print(g, pf, index);
        }

        if ((ehfp == null) || (!pageformat.equals(pf))) {
            ehfp = new ImageHeaderFooterPrintable(inner, top, bottom, SwingConstants.CENTER, SwingConstants.CENTER, PrintManager.getFont());
            pageformat = pf;
        }

        return ehfp.print(g, pf, index);
    }
}