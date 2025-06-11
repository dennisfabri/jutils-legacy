package de.df.jutils.gui.jtable;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import de.df.jutils.gui.border.InsetsBorder;
import de.df.jutils.gui.renderer.PrintHeaderRenderer;
import de.df.jutils.gui.util.EDTUtils;

/*
 * Created on Oct 19, 2004
 */

/**
 * @author Dennis Fabri
 */
public class JPrintTable extends JTable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3832617391833299257L;

    private static int value = 230;

    private static final Color GRIDCOLOR = Color.BLACK;
    private static final Color ODDCOLOR = Color.WHITE;
    private static Color evencolor = new Color(value, value, value);

    public static void setRowMarker(int v) {
        if (v < 0) {
            throw new IllegalArgumentException("Value must not be lower than 0: " + v);
        }
        if (v > 255) {
            throw new IllegalArgumentException("Value must not be higher than 255: " + v);
        }
        value = v;
        evencolor = new Color(value, value, value);
    }

    public static int getRowMarker() {
        return value;
    }

    public static Color getRowMarkerColor() {
        return evencolor;
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
     * @param lsm
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
        anHeader.setDefaultRenderer(new PrintHeaderRenderer(evencolor));
        anHeader.setForeground(Color.BLACK);
        anHeader.setBackground(evencolor);
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
            JTableUtils.setAlternatingTableCellRenderer(table, evencolor, ODDCOLOR);
        }
        table.setShowGrid(!colors);
        table.setGridColor(GRIDCOLOR);
        table.setShowHorizontalLines(!colors);
        table.setShowVerticalLines(!colors);

        table.setSelectionBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setOpaque(false);
    }
}
