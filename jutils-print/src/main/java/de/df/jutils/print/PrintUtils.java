package de.df.jutils.print;

import java.awt.Component;
import java.awt.Font;
import java.awt.print.Printable;
import java.text.MessageFormat;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import de.df.jutils.gui.jtable.ExtendedTableModel;
import de.df.jutils.gui.jtable.JPrintTable;
import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.print.printables.ExtendedHeaderFooterPrintable;
import de.df.jutils.print.printables.HeaderFooterPrintable;
import de.df.jutils.print.printables.JTablePrintable;
import de.df.jutils.print.printables.MultiplePrintable;

public final class PrintUtils {

    private PrintUtils() {
    }

    public static Printable getHeaderPrintable(Printable source, MessageFormat header, Font font) {
        if (header == null) {
            return source;
        }
        return new HeaderFooterPrintable(source, header, null, font);
    }

    public static Printable getHeaderPrintable(Printable source, String header, Font font) {
        if (header == null) {
            return source;
        }
        return getHeaderPrintable(source, new MessageFormat(header), font);
    }

    public static Printable getPrintable(ExtendedTableModel[] tm, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        MultiplePrintable mp = new MultiplePrintable();
        for (ExtendedTableModel aTm : tm) {
            mp.add(getPrintableI(aTm, aTm.getName(), optimize, shrink, enlarge, font));
        }
        return mp;
    }

    public static Printable getPrintable(JTable table, Component title, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        GetPrintable gp = new GetPrintable(table, null, optimize, shrink, enlarge, font);
        EDTUtils.executeOnEDT(gp);
        return new ExtendedHeaderFooterPrintable(gp.printable, title, null, PrintManager.getFont());
    }

    public static Printable getPrintable(JTable table, String title, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        GetPrintable gp = new GetPrintable(table, title, optimize, shrink, enlarge, font);
        EDTUtils.executeOnEDT(gp);
        return gp.printable;
    }

    public static Printable getPrintable(JTable[] tm, Component[] title, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        MultiplePrintable mp = new MultiplePrintable();
        for (int x = 0; x < tm.length; x++) {
            mp.add(getPrintable(tm[x], title[x], optimize, shrink, enlarge, font));
        }
        return mp;
    }

    public static Printable getPrintable(JTable[] tm, String title, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        MultiplePrintable mp = new MultiplePrintable();
        for (JTable aTm : tm) {
            mp.add(getPrintable(aTm, title, optimize, shrink, enlarge, font));
        }
        return mp;
    }

    public static Printable getPrintable(JTable[] tm, String[] title, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        MultiplePrintable mp = new MultiplePrintable();
        for (int x = 0; x < tm.length; x++) {
            mp.add(getPrintable(tm[x], title[x], optimize, shrink, enlarge, font));
        }
        return mp;
    }

    public static Printable getPrintable(TableModel tm, String title, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        return new MultiplePrintable(getPrintableI(tm, title, optimize, shrink, enlarge, font));
    }

    public static Printable getPrintable(TableModel[] tm, String[] title, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        MultiplePrintable mp = new MultiplePrintable();
        for (int x = 0; x < tm.length; x++) {
            mp.add(getPrintableI(tm[x], title[x], optimize, shrink, enlarge, font));
        }
        return mp;
    }

    private static Printable getPrintableI(TableModel tm, String title, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        return getPrintable(JPrintTable.createTable(tm), title, optimize, shrink, enlarge, font);
    }

    public static Printable[] getPrintables(JTable[] tm, String[] title, int optimize, boolean shrink, boolean enlarge,
            Font font) {
        Printable[] ps = new Printable[tm.length];
        for (int x = 0; x < tm.length; x++) {
            ps[x] = getPrintable(tm[x], title[x], optimize, shrink, enlarge, font);
        }
        return ps;
    }

    private static final class GetPrintable implements Runnable {

        volatile Printable printable;

        private JTable table;
        private int optimize;
        private boolean shrink;
        private boolean enlarge;
        private String title;

        private Font font;

        private GetPrintable(JTable t, String ti, int o, boolean s, boolean e, Font font) {
            table = t;
            title = ti;
            optimize = o;
            shrink = s;
            enlarge = e;
            this.font = font;
        }

        @Override
        public void run() {
            printable = PrintUtils.getHeaderPrintable(
                    new JTablePrintable(table, optimize, false,
                            (shrink ? JTable.PrintMode.FIT_WIDTH : JTable.PrintMode.NORMAL), enlarge, font),
                    title, font);
        }
    }

}
