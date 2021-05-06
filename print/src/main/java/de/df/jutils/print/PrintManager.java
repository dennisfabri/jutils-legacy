package de.df.jutils.print;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;

import de.df.jutils.gui.jtable.ExtendedTableModel;
import de.df.jutils.print.printables.BannerPrintable;
import de.df.jutils.print.printables.HeaderFooterPrintable;
import de.df.jutils.util.StringTools;

/**
 * @author Dennis Fabri
 * @date 17.10.2004
 */
public final class PrintManager {

    private static MessageFormat centerFooter;
    private static MessageFormat rightFooter;
    private static MessageFormat leftFooter;
    private static MessageFormat rightHeader;
    private static MessageFormat leftHeader;

    private static Font font;

    private static String name = "";
    private static String location = "";
    private static String date = "";

    private static BufferedImage adsPTop = null;
    private static BufferedImage adsPBottom = null;
    private static BufferedImage adsLTop = null;
    private static BufferedImage adsLBottom = null;
    private static String[] adsJobs = new String[0];

    static {
        leftFooter = new MessageFormat("");
        centerFooter = new MessageFormat("");
        rightFooter = new MessageFormat("");

        rightHeader = new MessageFormat("");
        leftHeader = new MessageFormat("");

        font = getDefaultFont();
    }

    private static String fontlog = null;

    public static Font getDefaultFont() {
        StringBuilder log = new StringBuilder();

        Font defaultfont1 = null;
        Font defaultfont2 = null;
        Font defaultfont3 = null;

        Font dialog = null;

        // Try different fonts
        // 1. DLRG-Font
        // 2. DLRG-Jugend-Font
        // 3. OS-StandardFonts
        // 4. Dialog
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (int x = 0; x < fonts.length; x++) {
            String fontname = fonts[x].getName();

            if (fontname.equals("DLRG Univers 55 Roman")) {
                log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
                defaultfont1 = fonts[x];
            }
            if (fontname.equals("DLRG-Jugend Text")) {
                log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
                defaultfont2 = fonts[x];
            }
            if (fontname.equals("Arial")) {
                log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
                defaultfont3 = fonts[x];
            }
            if (fontname.equals("Tahoma")) {
                log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
                defaultfont3 = fonts[x];
            }
            if (fontname.equals("Helvetica")) {
                log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
                defaultfont3 = fonts[x];
            }
            if (fontname.equals("Lucida")) {
                log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
                defaultfont3 = fonts[x];
            }
            if (fontname.equals("Dialog")) {
                log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
                dialog = fonts[x];
            }
        }

        log.append("defaultfont1: ").append(defaultfont1).append("\n");
        log.append("defaultfont2: ").append(defaultfont2).append("\n");
        log.append("defaultfont3: ").append(defaultfont3).append("\n");

        if (defaultfont1 == null) {
            defaultfont1 = defaultfont2;
        }
        if (defaultfont1 == null) {
            defaultfont1 = defaultfont3;
        }

        log.append("Current default: ").append(defaultfont1).append("\n");

        // Try Window default font
        if (defaultfont1 == null) {
            JFrame f = new JFrame();
            f.pack();
            defaultfont1 = f.getFont();
            f.dispose();
            log.append("JFrame: ").append(defaultfont1).append("\n");
        }

        // Choose first one
        if (defaultfont1 == null) {
            defaultfont1 = fonts[0];
            log.append("fonts[0]: ").append(defaultfont1).append("\n");
        }

        // If nothing helps use DIALOG.
        if (defaultfont1 == null) {
            defaultfont1 = dialog;
            log.append("dialog: ").append(defaultfont1).append("\n");
        }

        if (defaultfont1 != null) {
            defaultfont1 = defaultfont1.deriveFont(Font.PLAIN, 10);
            log.append("Derived Font: ").append(defaultfont1).append("\n");
        }
        log.append("Result: ").append(defaultfont1).append("\n");

        if (fontlog == null) {
            fontlog = log.toString();
        }
        return defaultfont1;
    }

    public static String getDefaultFontLog() {
        if (fontlog == null) {
            return "No log";
        }
        return fontlog;
    }

    public static void registerAds(BufferedImage iptop, BufferedImage ipbottom, BufferedImage iltop,
            BufferedImage ilbottom, String... jobs) {
        adsJobs = jobs;
        adsPTop = iptop;
        adsPBottom = ipbottom;
        adsLTop = iltop;
        adsLBottom = ilbottom;
    }

    private PrintManager() {
        // Never used
    }

    public static void setHeaderMessages(MessageFormat left, MessageFormat right) {
        if (left == null) {
            left = new MessageFormat("");
        }
        if (right == null) {
            right = new MessageFormat("");
        }
        leftHeader = left;
        rightHeader = right;
    }

    public static void setFooterMessages(MessageFormat left, MessageFormat center, MessageFormat right) {
        if (left == null) {
            left = new MessageFormat("");
        }
        if (center == null) {
            center = new MessageFormat("");
        }
        if (right == null) {
            right = new MessageFormat("");
        }
        leftFooter = left;
        centerFooter = center;
        rightFooter = right;
    }

    public static MessageFormat getFooterMessage(int align) {
        switch (align) {
        case HeaderFooterPrintable.CENTER:
            return centerFooter;
        case HeaderFooterPrintable.LEFT:
            return leftFooter;
        case HeaderFooterPrintable.RIGHT:
            return rightFooter;
        default:
            throw new IndexOutOfBoundsException();

        }
    }

    public static MessageFormat getHeaderMessage(int align) {
        switch (align) {
        case HeaderFooterPrintable.CENTER:
            throw new IllegalArgumentException("Value HeaderFooterPrintable.CENTER not allowed!");
        // return centerHeader;
        case HeaderFooterPrintable.LEFT:
            return leftHeader;
        case HeaderFooterPrintable.RIGHT:
            return rightHeader;
        default:
            throw new IndexOutOfBoundsException();

        }
    }

    public static void setFont(Font f) {
        if (f == null) {
            throw new NullPointerException("Font must not be null");
        }
        font = f;
    }

    public static Font getFont() {
        if (font == null) {
            font = getDefaultFont();
        }
        return font;
    }

    public static Printable getFinalPrintable(Printable printable, Date currentDate, String header, String jobname) {
        return getFinalPrintable(printable, currentDate, header == null ? null : new MessageFormat(header), jobname);
    }

    public static Printable getFinalPrintable(Printable printable, Date currentDate, boolean header, String jobname) {
        return getFinalPrintable(printable, currentDate, header ? new MessageFormat("") : null, jobname);
    }

    public static Printable getFinalPrintable(Printable printable, Date currentDate, MessageFormat header,
            String jobname) {
        HeaderFooterPrintable hfp = new HeaderFooterPrintable(printable, null, null, font);
        hfp.addDynamic(name);
        hfp.addDynamic(location);
        hfp.addDynamic(date);
        if (header != null) {
            hfp.setHeader(header, HeaderFooterPrintable.CENTER);
            hfp.setHeader(leftHeader, HeaderFooterPrintable.LEFT);
            hfp.setHeader(rightHeader, HeaderFooterPrintable.RIGHT);
        }
        if (currentDate != null) {
            hfp.addDynamic(DateFormat.getDateInstance().format(currentDate));
            hfp.addDynamic(DateFormat.getTimeInstance().format(currentDate));
            hfp.setFooter(leftFooter, HeaderFooterPrintable.LEFT);
        }
        hfp.setFooter(centerFooter, HeaderFooterPrintable.CENTER);
        hfp.setFooter(rightFooter, HeaderFooterPrintable.RIGHT);

        for (String job : adsJobs) {
            if (job.equals(jobname)) {
                return new BannerPrintable(hfp, adsPTop, adsPBottom, adsLTop, adsLBottom);
            }
        }
        return hfp;
    }

    public static JLabel getPrintLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(getFont());
        return l;
    }

    public static String getDate() {
        return date;
    }

    public static void setDatum(String datum) {
        PrintManager.date = StringTools.firstLine(datum);
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name, String shortname) {
        shortname = StringTools.firstLine(shortname);
        if (shortname != null && shortname.trim().length() > 0) {
            PrintManager.name = shortname;
        } else {
            PrintManager.name = StringTools.firstLine(name);
        }
    }

    public static String getLocation() {
        return location;
    }

    public static void setOrt(String ort) {
        PrintManager.location = StringTools.firstLine(ort);
    }

    public static Printable getPrintable(JTable table, String title, int optimize, boolean shrink, boolean enlarge) {
        return PrintUtils.getPrintable(table, title, optimize, shrink, enlarge, getFont());
    }

    public static Printable getPrintable(ExtendedTableModel tableModel, String title, int optimize, boolean shrink,
            boolean enlarge) {
        return PrintUtils.getPrintable(tableModel, title, optimize, shrink, enlarge, getFont());
    }

    public static Printable getPrintable(ExtendedTableModel[] tableModels, int optimize, boolean shrink,
            boolean enlarge) {
        return PrintUtils.getPrintable(tableModels, optimize, shrink, enlarge, getFont());
    }

    public static Printable getHeaderPrintable(Printable printable, String title) {

        return PrintUtils.getHeaderPrintable(printable, title, getFont());
    }

    public static Printable getPrintable(JTable[] tables, Component[] titles, int optimize, boolean shrink,
            boolean enlarge) {
        return PrintUtils.getPrintable(tables, titles, optimize, shrink, enlarge, getFont());
    }

    public static Printable getPrintable(JTable[] tables, String[] names, int optimize, boolean shrink, boolean enlarge) {
        return PrintUtils.getPrintable(tables, names, optimize, shrink, enlarge, getFont());
    }
}