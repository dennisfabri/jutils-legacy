/*
 * Created on 18.01.2005
 */
package de.df.jutils.print.printables;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author Mueller
 */
public class HeaderFooterPrintable implements Printable {

    private Font               font     = null;
    private MessageFormat[]    header   = new MessageFormat[3];
    private MessageFormat[]    footer   = new MessageFormat[3];
    private Printable          source   = null;
    private LinkedList<Object> dynamics = new LinkedList<Object>();

    public static final int    LEFT     = 0;
    public static final int    CENTER   = 1;
    public static final int    RIGHT    = 2;

    /**
     * 
     */
    public HeaderFooterPrintable(Printable main, MessageFormat header, MessageFormat footer, Font font) {
        if (main == null) {
            throw new NullPointerException();
        }
        source = main;
        setHeader(header, CENTER);
        setFooter(footer, CENTER);
        setFont(font);

        Date date = new Date();
        dynamics.addLast("Placeholder");
        dynamics.addLast(DateFormat.getDateInstance().format(date));
        dynamics.addLast(DateFormat.getTimeInstance().format(date));
    }

    public void addDynamic(Object value) {
        if (value == null) {
            value = "";
        }
        dynamics.addLast(value);
    }

    public void setFont(Font f) {
        font = f;
    }

    public void setFooter(MessageFormat f, int align) {
        footer[align] = f;
    }

    public void setHeader(MessageFormat f, int align) {
        header[align] = f;
    }

    private static float getX(PageFormat pf, double width, int align) {
        double x = 0;
        switch (align) {
        case CENTER:
            x = (pf.getImageableWidth() - width) / 2;
            break;
        case RIGHT:
            x = pf.getImageableWidth() - width;
            break;
        case LEFT:
        default:
            break;
        }
        return (float) (x + pf.getImageableX());
    }

    private static String getText(MessageFormat mf, Object[] dynamic) {
        if (mf == null) {
            return "";
        }
        if (dynamic == null) {
            dynamic = new Object[0];
        }
        return mf.format(dynamic);
    }

    private static void print(MessageFormat mf, Graphics2D g2d, PageFormat pf, float y, int align, Object[] dynamic) {
        if (mf == null) {
            return;
        }
        String text = mf.format(dynamic);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        float x = getX(pf, width, align);
        g2d.drawString(text, x, y + fm.getHeight());
    }

    private static boolean noHeaders(MessageFormat[] header, Object[] dynamic) {
        if (header != null) {
            for (int x = 0; x < 3; x++) {
                if (header[x] != null) {
                    String text = getText(header[x], dynamic).trim();
                    if (text.length() > 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean noFooters(MessageFormat[] footer, Object[] dynamic) {
        if (footer != null) {
            for (int x = 0; x < 3; x++) {
                if (footer[x] != null) {
                    String text = getText(footer[x], dynamic).trim();
                    if (text.length() > 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean noHeadersAndFooters(MessageFormat[] header, MessageFormat[] footer, Object[] dynamic) {
        return noFooters(footer, dynamic) && noHeaders(header, dynamic);
    }

    @Override
    public int print(Graphics g, PageFormat pf, int index) throws PrinterException {
        // EDTUtils.sleep();
        Object[] dynamic1 = dynamics.toArray();
        Object[] dynamic2 = null;

        boolean combine = false;
        if (source instanceof HeaderFooterPrintable) {
            HeaderFooterPrintable hfp = (HeaderFooterPrintable) source;
            combine = true;
            for (int x = 0; x < 3; x++) {
                if ((getText(header[x], dynamic1).trim().length() > 0) && (getText(hfp.header[x], dynamic2).trim().length() > 0)) {
                    combine = false;
                    break;
                }
                if ((getText(footer[x], dynamic1).trim().length() > 0) && (getText(hfp.footer[x], dynamic2).trim().length() > 0)) {
                    combine = false;
                    break;
                }
            }
        }

        if (combine) {
            HeaderFooterPrintable hfp = (HeaderFooterPrintable) source;
            return print(g, pf, index, dynamic1, font, header, footer, dynamic2, hfp.font, hfp.header, hfp.footer, hfp.source);
        }
        return print(g, pf, index, dynamic1, font, header, footer, null, null, null, null, source);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.print.Printable#print(java.awt.Graphics,
     * java.awt.print.PageFormat, int)
     */
    public static int print(Graphics g, PageFormat pf, int index, Object[] dynamic1, Font font1, MessageFormat[] header1, MessageFormat[] footer1,
            Object[] dynamic2, Font font2, MessageFormat[] header2, MessageFormat[] footer2, Printable source) throws PrinterException {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE);

        // Update dynamics
        dynamic1[0] = index + 1;
        if (dynamic2 != null) {
            dynamic2[0] = index + 1;
        }

        // Draw Headers and Footers
        if (font1 != null) {
            g2d.setFont(font1);
        } else if (font2 != null) {
            g2d.setFont(font2);
        }
        FontMetrics fm = g2d.getFontMetrics();
        int height = fm.getHeight();

        Paper page = pf.getPaper();

        PageFormat ranged = null;
        if (noHeadersAndFooters(header1, footer1, dynamic1) && noHeadersAndFooters(header2, footer2, dynamic2)) {
            ranged = pf;
        } else {
            double x = page.getImageableX();
            double y = page.getImageableY();
            double w = page.getImageableWidth();
            double h = page.getImageableHeight();
            if (noFooters(footer1, dynamic1) && noFooters(footer2, dynamic2)) {
                switch (pf.getOrientation()) {
                default:
                case PageFormat.PORTRAIT:
                    x = page.getImageableX();
                    y = page.getImageableY() + 2 * height;
                    w = page.getImageableWidth();
                    h = page.getImageableHeight() - 2 * height;
                    break;
                case PageFormat.LANDSCAPE:
                case PageFormat.REVERSE_LANDSCAPE:
                    x = page.getImageableX() + 2 * height;
                    y = page.getImageableY();
                    w = page.getImageableWidth() - 2 * height;
                    h = page.getImageableHeight();
                    break;
                }
            } else {
                if (noHeaders(header1, dynamic1) && noHeaders(header2, dynamic2)) {
                    switch (pf.getOrientation()) {
                    default:
                    case PageFormat.PORTRAIT:
                        x = page.getImageableX();
                        y = page.getImageableY();
                        w = page.getImageableWidth();
                        h = page.getImageableHeight() - 2 * height;
                        break;
                    case PageFormat.LANDSCAPE:
                    case PageFormat.REVERSE_LANDSCAPE:
                        x = page.getImageableX();
                        y = page.getImageableY();
                        w = page.getImageableWidth() - 2 * height;
                        h = page.getImageableHeight();
                        break;
                    }
                } else {
                    switch (pf.getOrientation()) {
                    default:
                    case PageFormat.PORTRAIT:
                        x = page.getImageableX();
                        y = page.getImageableY() + 2 * height;
                        w = page.getImageableWidth();
                        h = page.getImageableHeight() - 4 * height;
                        break;
                    case PageFormat.LANDSCAPE:
                        x = page.getImageableX() + 2 * height;
                        y = page.getImageableY();
                        w = page.getImageableWidth() - 4 * height;
                        h = page.getImageableHeight();
                        break;
                    case PageFormat.REVERSE_LANDSCAPE:
                        x = page.getImageableX() + 2 * height;
                        y = page.getImageableY();
                        w = page.getImageableWidth() - 4 * height;
                        h = page.getImageableHeight();
                        break;
                    }
                }
            }
            // Creating Pageformt for central area
            ranged = new PageFormat();
            Paper p = new Paper();
            p.setSize(page.getWidth(), page.getHeight());
            p.setImageableArea(x, y, w, h);
            ranged.setOrientation(pf.getOrientation());
            ranged.setPaper(p);
        }

        Graphics g2x = g2d.create();
        g2x.setClip((int) ranged.getImageableX(), (int) ranged.getImageableY(), (int) ranged.getImageableWidth() + 1, (int) ranged.getImageableHeight() + 1);
        int result = source.print(g2x, ranged, index);

        if (result == PAGE_EXISTS) {
            g2d.setBackground(Color.WHITE);
            g2d.setColor(Color.BLACK);

            for (int x = 0; x < 3; x++) {
                if (!noHeaders(header1, dynamic1)) {
                    print(header1[x], g2d, pf, (float) pf.getImageableY(), x, dynamic1);
                }
                if (!noHeaders(header2, dynamic2)) {
                    print(header2[x], g2d, pf, (float) pf.getImageableY(), x, dynamic2);
                }
                if (!noFooters(footer1, dynamic1)) {
                    print(footer1[x], g2d, pf, (float) (pf.getImageableY() + pf.getImageableHeight() - fm.getHeight() * 1.5), x, dynamic1);
                }
                if (!noFooters(footer2, dynamic2)) {
                    print(footer2[x], g2d, pf, (float) (pf.getImageableY() + pf.getImageableHeight() - fm.getHeight() * 1.5), x, dynamic2);
                }
            }
        }

        return result;
    }
}