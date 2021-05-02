/*
 * Created on 04.10.2004
 */
package de.df.jutils.gui.jtable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.renderer.AlignmentCellRenderer;
import de.df.jutils.gui.renderer.AlternatingTableCellRenderer;
import de.df.jutils.gui.util.EDTUtils;

/**
 * Utilites for manipulating JTables. Note: This is a modified version!
 * 
 * @author Marc Hedlund
 *         <a href="mailto:marc@precipice.org">&lt;marc@precipice.org&gt; </a>
 */
public final class JTableUtils {

    private static final Class<?>[] CLASSES = new Class<?>[] { Object.class, Number.class, Boolean.class, Date.class,
            ImageIcon.class, Icon.class, Float.class, Double.class, String.class };

    private JTableUtils() {
        // Never used
    }

    public static void setPreferredCellSizes(JTable table) {
        setPreferredCellSizes(table, false, false);
    }

    /**
     * This is based on code found at
     * <http://java.sun.com/docs/books/tutorial/ui/swingComponents/table.html>,
     * somewhat modified. Added some lines from
     * http://www.jroller.com/page/santhosh/20050523#fit_tablecolumns_on_demand to
     * handle header width correctly
     */
    public static void setPreferredCellSizes(JTable table, boolean includeHeader, boolean tablesize) {
        TableModel model = table.getModel();
        TableColumnModel colModel = table.getColumnModel();

        int highestCell = 0;
        int twidth = 0;

        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = colModel.getColumn(i);

            if (column.getMaxWidth() > 0) {
                int longestCell = 0;

                if (includeHeader) {
                    JTableHeader header = table.getTableHeader();
                    Component component = header.getDefaultRenderer().getTableCellRendererComponent(table,
                            column.getIdentifier(), false, false, -1, i);
                    Dimension size = component.getPreferredSize();

                    longestCell = size.width;
                    highestCell = Math.max(highestCell, size.height);
                }

                for (int j = 0; j < model.getRowCount(); j++) {
                    Object value = model.getValueAt(j, i);
                    if (value != null) {
                        Component cell = getRenderer(table, j, i, value);

                        int width = cell.getPreferredSize().width;
                        int height = cell.getPreferredSize().height + table.getRowMargin() * 2;

                        if (width > longestCell) {
                            longestCell = width;
                        }
                        if (height > highestCell) {
                            highestCell = height;
                        }
                    }
                }
                int max = column.getMaxWidth();
                int realwidth = 4 + Math.round(Math.min(max, longestCell + table.getIntercellSpacing().width));
                EDTUtils.setPreferredWidth(column, realwidth);
                twidth += realwidth;
            }
        }
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = colModel.getColumn(i);
            if (column.getMaxWidth() == 0) {
                column.setWidth(0);
            }
        }
        if (highestCell > 0) {
            if (SwingUtilities.isEventDispatchThread()) {
                table.setRowHeight(highestCell + table.getColumnModel().getColumnMargin());
            } else {
                EDTUtils.setRowHeight(table, highestCell + table.getColumnModel().getColumnMargin());
            }
        }
        if (tablesize) {
            table.setSize(new Dimension(twidth, table.getHeight()));
        }
    }

    public static void setPreferredRowHeight(JTable table) {
        setPreferredRowHeight(table, false);
    }

    private static void setPreferredRowHeight(JTable table, boolean includeHeader) {
        TableModel model = table.getModel();
        TableColumnModel colModel = table.getColumnModel();

        int highestCell = 0;

        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = colModel.getColumn(i);

            if (column.getMaxWidth() > 0) {
                if (includeHeader) {
                    JTableHeader header = table.getTableHeader();
                    Dimension size = header.getDefaultRenderer()
                            .getTableCellRendererComponent(table, column.getIdentifier(), false, false, -1, i)
                            .getPreferredSize();

                    highestCell = Math.max(highestCell, size.height);
                }

                for (int j = 0; j < model.getRowCount(); j++) {
                    Object value = model.getValueAt(j, i);
                    if (value != null) {
                        Component cell = getRenderer(table, j, i, value);

                        int height = cell.getPreferredSize().height + table.getRowMargin() * 2;

                        if (height > highestCell) {
                            highestCell = height;
                        }
                    }
                }
            }
        }
        if (highestCell > 0) {
            if (SwingUtilities.isEventDispatchThread()) {
                table.setRowHeight(highestCell + table.getColumnModel().getColumnMargin());
            } else {
                EDTUtils.setRowHeight(table, highestCell + table.getColumnModel().getColumnMargin());
            }
        }
    }

    public static void setPreferredCellWidths(JTable table) {
        TableModel model = table.getModel();
        TableColumnModel colModel = table.getColumnModel();

        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = colModel.getColumn(i);

            if (column.getMaxWidth() > 0) {
                JTableHeader header = table.getTableHeader();
                Dimension size = header.getDefaultRenderer()
                        .getTableCellRendererComponent(table, column.getIdentifier(), false, false, -1, i)
                        .getPreferredSize();

                int longestCell = size.width;

                for (int j = 0; j < model.getRowCount(); j++) {
                    Object value = model.getValueAt(j, i);
                    if (value != null) {
                        Component cell = getRenderer(table, j, i, value);

                        int width = cell.getPreferredSize().width;

                        if (width > longestCell) {
                            longestCell = width;
                        }
                    }
                }
                EDTUtils.setPreferredWidth(column, longestCell + table.getIntercellSpacing().width);
            }
        }
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = colModel.getColumn(i);
            if (column.getMaxWidth() == 0) {
                column.setWidth(0);
            }
        }
    }

    public static void setPreferredRowHeights(JTable table) {
        TableModel model = table.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            int highestCell = 0;

            for (int j = 0; j < model.getColumnCount(); j++) {
                Object value = model.getValueAt(i, j);
                if (value != null) {
                    Component cell = getRenderer(table, i, j, value);

                    try {
                        highestCell = Math.max(highestCell, cell.getPreferredSize().height + table.getRowMargin() * 2);
                    } catch (NullPointerException npe) {
                        // Nothing to do
                    }
                }
            }
            EDTUtils.setRowHeight(table, i, highestCell + table.getColumnModel().getColumnMargin());
        }
    }

    private static class RendererGetter implements Runnable {

        private Component renderer = null;

        private JTable    table;
        private int       x;
        private int       y;
        private Object    value;

        public RendererGetter(JTable t, int i, int j, Object v) {
            this.table = t;
            this.x = i;
            this.y = j;
            this.value = v;
        }

        public Component getRenderer() {
            return renderer;
        }

        @Override
        public void run() {
            renderer = table.getDefaultRenderer(table.getModel().getColumnClass(y)).getTableCellRendererComponent(table,
                    value, false, false, x, y);
        }

    }

    private static Component getRenderer(JTable table, int i, int j, Object value) {
        RendererGetter rg = new RendererGetter(table, i, j, value);
        EDTUtils.executeOnEDT(rg);
        return rg.getRenderer();
    }

    /**
     * Takes a column in an existing table and makes it fixed-width. Specifically,
     * it sets the column's minimum and maximum widths to its preferred width, and
     * disables auto-resize for the table as a whole.
     * <p>
     * Later on this should take a column array for efficiency.
     * 
     * @param table JTable The table to modify @param colIndex int Which column to
     *              fix @return int The width of the column as it was fixed
     */
    private static int fixColumnToPreferredWidth(JTable table, int colIndex) {
        TableColumnModel tcm = table.getColumnModel();
        TableColumn col = tcm.getColumn(colIndex);
        int width = col.getPreferredWidth();

        col.setMaxWidth(width);
        col.setMinWidth(width);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        return width;
    }

    public static Color getOddDefault() {
        Color odd = UIManager.getColor("textHighlight");
        Color base = UIManager.getColor("Table.background");
        if (base == null) {
            base = Color.WHITE;
        }
        if (odd == null) {
            odd = UIManager.getColor("InternalFrame.borderShadow");
        }
        odd = ColorUtils.calculateColor(odd, base, 0.65);
        Color gray = ColorUtils.toGray(odd);
        odd = ColorUtils.calculateColor(odd, gray, 0.5);

        return odd;
    }

    public static Color getEvenDefault() {
        return UIManager.getColor("Table.background");
    }

    /**
     * @param table
     */
    public static void setAlternatingTableCellRenderer(JTable table) {
        setAlternatingTableCellRenderer(table, getOddDefault(), getEvenDefault());
    }

    static void setAlternatingTableCellRenderer(JTable table, Color odd, Color even) {
        for (Class<?> c : CLASSES) {
            TableCellRenderer tcr = table.getDefaultRenderer(c);
            if (tcr instanceof AlternatingTableCellRenderer) {
                AlternatingTableCellRenderer atcr = (AlternatingTableCellRenderer) tcr;
                atcr.setColors(odd, even);
            } else {
                table.setDefaultRenderer(c, new AlternatingTableCellRenderer(odd, even, tcr));
            }
        }

        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setShowGrid(false);
    }

    public static void setTableCellRenderer(JTable table, TableCellRenderer renderer) {
        for (Class<?> c : CLASSES) {
            table.setDefaultRenderer(c, renderer);
        }
    }

    private static <T> TableCellRenderer createAlignmentRenderer(int[] aligns, int defaultAlign) {
        return new AlignmentCellRenderer<T>(aligns, defaultAlign);
    }

    public static void setAlignmentRenderer(JTable table, int[] aligns, int defaultAlign) {
        setTableCellRenderer(table, createAlignmentRenderer(aligns, defaultAlign));
    }

    public static void hideColumnAndRemoveData(JTable tm, int index) {
        TableColumn column = tm.getColumnModel().getColumn(index);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setWidth(0);

        column.setHeaderValue("");
        TableModel stm = tm.getModel();
        for (int x = 0; x < stm.getRowCount(); x++) {
            stm.setValueAt("", x, index);
        }
    }

    public static void unhideColumn(JTable tm, int index, String title) {
        TableColumn column = tm.getColumnModel().getColumn(index);
        column.setMaxWidth(Integer.MAX_VALUE);

        column.setHeaderValue(title);
    }
}