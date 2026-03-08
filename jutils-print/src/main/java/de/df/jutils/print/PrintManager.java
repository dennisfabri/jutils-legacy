package de.df.jutils.print;

import de.df.jutils.gui.jtable.ExtendedTableModel;
import de.df.jutils.print.printables.BannerPrintable;
import de.df.jutils.print.printables.HeaderFooterPrintable;
import de.df.jutils.util.StringTools;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

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

    private static BufferedImage adsPTop;
    private static BufferedImage adsPBottom;
    private static BufferedImage adsLTop;
    private static BufferedImage adsLBottom;
    private static String[] adsJobs = new String[0];

    private static final List<String> defaultFontNames = List.of("DLRG Univers 55 Roman",
                                                                 "DLRG-Jugend Text",
                                                                 "Liberation Sans",
                                                                 "DejaVu Sans",
                                                                 "Noto Sans Regular",
                                                                 "Aptos",
                                                                 "Calibri",
                                                                 "Ubuntu Sans Regular",
                                                                 "Tahoma",
                                                                 "Arial",
                                                                 "Nimbus Sans",
                                                                 "Lucida Sans",
                                                                 "Garamond",
                                                                 "Linux Libertine O",
                                                                 "Linux Biolinum O",
                                                                 "Times New Roman",
                                                                 "Dialog.plain");

    private static List<String> fontlog = List.of();

    static {
        leftFooter = new MessageFormat("");
        centerFooter = new MessageFormat("");
        rightFooter = new MessageFormat("");

        rightHeader = new MessageFormat("");
        leftHeader = new MessageFormat("");

        font = getDefaultFont();
    }

    public static Font getDefaultFont() {
        List<String> log = new ArrayList<>();

        Font defaultfont = null;

        Map<String, Font> fonts = stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()).collect(Collectors.toMap(Font::getFontName, f -> f));

        for (String fontname : defaultFontNames) {
            if (fonts.containsKey(fontname)) {
                log.add("Found %s".formatted(fontname));
                if (defaultfont == null) {
                    defaultfont = fonts.get(fontname);
                }
            } else {
                log.add("Not found: %s".formatted(fontname));
                stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()).filter(f -> f.getFontName().contains(fontname))
                                                                                       .forEach(f -> log.add("  Similar: %s".formatted(f.getFontName())));
            }
        }

        log.add("Current default: %s".formatted(toText(defaultfont)));

        if (defaultfont != null) {
            defaultfont = defaultfont.deriveFont(Font.PLAIN, 10);
            log.add("Derived Font: %s".formatted(toText(defaultfont)));
        }
        log.add("Result: %s".formatted(toText(defaultfont)));

        fontlog = log;

        return defaultfont;
    }

    private static String toText(Font defaultfont) {
        if (defaultfont == null) {
            return "<null>";
        }
        return "%s (%d %s, %s)".formatted(defaultfont.getFontName(), defaultfont.getSize(), toStyle(defaultfont.getStyle()), defaultfont.getFamily());
    }

    private static String toStyle(int style) {
        return switch (style) {
            case Font.PLAIN -> "PLAIN";
            case Font.BOLD -> "BOLD";
            case Font.ITALIC -> "ITALIC";
            case Font.BOLD | Font.ITALIC -> "BOLD|ITALIC";
            default -> String.valueOf(style);
        };
    }

    public static List<String> getDefaultFontLog() {
        return fontlog;
    }

    public static void registerAds(BufferedImage iptop, BufferedImage ipbottom, BufferedImage iltop, BufferedImage ilbottom, String... jobs) {
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
        return switch (align) {
            case HeaderFooterPrintable.CENTER -> centerFooter;
            case HeaderFooterPrintable.LEFT -> leftFooter;
            case HeaderFooterPrintable.RIGHT -> rightFooter;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    public static MessageFormat getHeaderMessage(int align) {
        return switch (align) {
            case HeaderFooterPrintable.CENTER -> throw new IllegalArgumentException("Value HeaderFooterPrintable.CENTER not allowed!");
            case HeaderFooterPrintable.LEFT -> leftHeader;
            case HeaderFooterPrintable.RIGHT -> rightHeader;
            default -> throw new IndexOutOfBoundsException();
        };
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

    public static Printable getFinalPrintable(Printable printable, Date currentDate, MessageFormat header, String jobname) {
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
        l.setForeground(Color.BLACK);
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
        if (shortname != null && !shortname.isBlank()) {
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

    public static Printable getPrintable(ExtendedTableModel tableModel, String title, int optimize, boolean shrink, boolean enlarge) {
        return PrintUtils.getPrintable(tableModel, title, optimize, shrink, enlarge, getFont());
    }

    public static Printable getPrintable(ExtendedTableModel[] tableModels, int optimize, boolean shrink, boolean enlarge) {
        return PrintUtils.getPrintable(tableModels, optimize, shrink, enlarge, getFont());
    }

    public static Printable getHeaderPrintable(Printable printable, String title) {

        return PrintUtils.getHeaderPrintable(printable, title, getFont());
    }

    public static Printable getPrintable(JTable[] tables, Component[] titles, int optimize, boolean shrink, boolean enlarge) {
        return PrintUtils.getPrintable(tables, titles, optimize, shrink, enlarge, getFont());
    }

    public static Printable getPrintable(JTable[] tables, String[] names, int optimize, boolean shrink, boolean enlarge) {
        return PrintUtils.getPrintable(tables, names, optimize, shrink, enlarge, getFont());
    }
}
