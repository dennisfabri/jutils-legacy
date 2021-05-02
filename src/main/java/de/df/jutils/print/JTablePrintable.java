/*
 * Created on 01.11.2004
 */
package de.df.jutils.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.TableColumn;

import de.df.jutils.gui.jtable.JPrintTable;
import de.df.jutils.gui.jtable.JTableUtils;
import de.df.jutils.gui.util.EDTUtils;

/**
 * @author Dennis Mueller
 * @date 01.11.2004
 */
public class JTablePrintable implements Printable {

    public static final int OPT_NONE     = 0;
    public static final int OPT_HEADER   = 1;
    public static final int OPT_RENDERER = 2;

    public static final int OPT_ALL      = OPT_HEADER | OPT_RENDERER;

    Printable               printable    = null;
    JTable                  table        = null;
    JDialog                 dummy        = null;

    boolean                 resize       = false;

    JTable.PrintMode        mode;

    public JTablePrintable(JTable jtable, int optimize, boolean variableRows, JTable.PrintMode printmode, boolean resize, Font font) {
        mode = printmode;
        this.resize = resize;
        this.table = jtable;

        ToolTipManager.sharedInstance().unregisterComponent(table);
        ToolTipManager.sharedInstance().unregisterComponent(table.getTableHeader());

        if ((optimize & OPT_ALL) > 0) {
            JPrintTable.initPrintableJTable(table, (optimize & OPT_RENDERER) > 0);
        }
        if (font != null) {
            table.getTableHeader().setFont(font);
            table.setFont(font);
        }

        table.setSelectionForeground(Color.BLACK);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableUtils.setPreferredCellSizes(table, true, false);
        if (variableRows) {
            JTableUtils.setPreferredRowHeights(table);
        }
        table.setSize(table.getPreferredSize());

        dummy = new JDialog();
        dummy.add(new JScrollPane(table));
        EDTUtils.pack(dummy);

        printable = table.getPrintable(printmode, null, null);
    }

    private boolean firstPage = true;

    /*
     * (non-Javadoc)
     * @see java.awt.print.Printable#print(java.awt.Graphics,
     * java.awt.print.PageFormat, int)
     */
    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        EDTUtils.sleep();
        if (firstPage) {
            firstPage = false;
            EDTUtils.executeOnEDT(new PrintUpdater(pf));
        }
        return EDTUtils.print(printable, g, pf, page);
        // return printable.print(g, pf, page);
    }

    private class PrintUpdater implements Runnable {

        private final PageFormat pf;

        public PrintUpdater(PageFormat pf) {
            this.pf = pf;
        }

        @Override
        public void run() {
            int width = (int) pf.getImageableWidth();
            if (resize && (table.getWidth() < width) && (table.getColumnCount() > 0)) {
                int colplus = (width - table.getWidth()) / table.getColumnCount() - 1;
                if (colplus > 0) {
                    for (int x = 0; x < table.getColumnCount(); x++) {
                        TableColumn tc = table.getColumnModel().getColumn(x);
                        tc.setMaxWidth(tc.getWidth() + colplus);
                        tc.setWidth(tc.getWidth() + colplus);
                        tc.setMinWidth(tc.getWidth() + colplus);
                    }
                    EDTUtils.pack(dummy);
                    printable = table.getPrintable(mode, null, null);
                }
            }
        }
    }
}