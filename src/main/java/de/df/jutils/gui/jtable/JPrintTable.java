package de.df.jutils.gui.jtable;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.*;

import de.df.jutils.gui.border.InsetsBorder;
import de.df.jutils.gui.renderer.PrintHeaderRenderer;
import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.util.AReturnRunner;

/*
 * Created on Oct 19, 2004
 */

/**
 * @author mueller
 */
public class JPrintTable extends JTable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long  serialVersionUID = 3832617391833299257L;

    private static int         VALUE            = 230;

    private static final Color GRIDCOLOR        = Color.BLACK;
    private static final Color ODDCOLOR         = Color.WHITE;
    private static Color       EVENCOLOR        = new Color(VALUE, VALUE, VALUE);

    public static void setRowMarker(int v) {
        if (v < 0) {
            throw new IllegalArgumentException("Value must not be lower than 0: " + v);
        }
        if (v > 255) {
            throw new IllegalArgumentException("Value must not be higher than 255: " + v);
        }
        VALUE = v;
        EVENCOLOR = new Color(VALUE, VALUE, VALUE);
    }

    public static int getRowMarker() {
        return VALUE;
    }

    public static Color getRowMarkerColor() {
        return EVENCOLOR;
    }

    /**
     */
    public JPrintTable() {
        super();
        init();
    }

    /**
     * @param numRows
     * @param numColumns
     */
    public JPrintTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        init();
    }

    /**
     * @param rowData
     * @param columnNames
     */
    public JPrintTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        init();
    }

    /**
     * @param rowData
     * @param columnNames
     */
    @SuppressWarnings("rawtypes")
    public JPrintTable(Vector<? extends Vector> rowData, Vector<?> columnNames) {
        super(rowData, columnNames);
        init();
    }

    /**
     * @param dm
     */
    public JPrintTable(TableModel dm) {
        super(dm);
        init();
    }

    /**
     * @param dm
     * @param cm
     */
    public JPrintTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        init();
    }

    /**
     * @param tm
     * @param tcm
     *            @param lsm
     */
    public JPrintTable(TableModel tm, TableColumnModel tcm, ListSelectionModel lsm) {
        super(tm, tcm, lsm);
        init();
    }

    public static JPrintTable createTable(TableModel tm) {
        return EDTUtils.executeOnEDTwithReturn(() -> new JPrintTable(tm));
    }

    private void init() {
        initPrintableJTable(this);
    }

    public static void initPrintableJTable(JTable table) {
        initPrintableJTable(table, true);
    }

    public static void initPrintableJTable(JTable table, boolean colors) {
        JTableHeader anHeader = table.getTableHeader();
        anHeader.setDefaultRenderer(new PrintHeaderRenderer(EVENCOLOR));
        anHeader.setForeground(Color.BLACK);
        anHeader.setBackground(EVENCOLOR);
        anHeader.setBorder(new InsetsBorder(GRIDCOLOR, 0, 0, 1, 0));

        if (table instanceof JGroupableTable) {
            // Fix Linux problem which resets all Renderes on next line
            // table.setUI(new BasicTableUI());
            table.getTableHeader().setUI(new GroupableTableHeaderUI(new BasicTableHeaderUI()));
        } else {
            // Fix Linux problem which resets all Renderes on next line
            // table.setUI(new BasicTableUI());
            table.getTableHeader().setUI(new BasicTableHeaderUI());
        }

        table.setIntercellSpacing(new Dimension(0, 0));
        if (colors) {
            JTableUtils.setAlternatingTableCellRenderer(table, EVENCOLOR, ODDCOLOR);
        }
        table.setShowGrid(!colors);
        table.setGridColor(GRIDCOLOR);
        table.setShowHorizontalLines(!colors);
        table.setShowVerticalLines(!colors);

        table.setSelectionBackground(Color.WHITE);
        table.setOpaque(false);
    }
}