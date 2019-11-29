/*
 * Created on 29.04.2006
 */
package de.df.jutils.print;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import de.df.jutils.gui.jtable.JPrintTable;
import de.df.jutils.gui.jtable.JTableUtils;
import de.df.jutils.gui.util.EDTUtils;

public class TextTablePrintable implements Printable {

    private JTable    table     = null;
    private Printable printable = null;

    public TextTablePrintable(JTable table) {
        this.table = table;
        EDTUtils.executeOnEDT(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (printable == null) {
            TableColumn c = table.getColumnModel().getColumn(1);
            EDTUtils.setWidth(c, (int) pf.getImageableWidth() - table.getWidth() + c.getWidth());
            JTableUtils.setPreferredRowHeights(table);
            printable = new mod.javax.swing.TablePrintable(table, JTable.PrintMode.FIT_WIDTH, null, null);
        }
        // return EDTUtils.print(printable, g.create(), pf, page);
        return printable.print(g, pf, page);
    }

    void init() {
        Font font = PrintManager.getFont();
        if (font != null) {
            table.getTableHeader().setFont(font);
            table.setFont(font);
        }

        table.setSelectionForeground(Color.BLACK);

        JPrintTable.initPrintableJTable(table, false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setShowGrid(false);

        // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableUtils.setPreferredCellSizes(table);
        table.setSize(table.getPreferredSize());
        Frame dummy = new Frame();
        JScrollPane scr = new JScrollPane(table);
        scr.setColumnHeader(null);
        dummy.add(scr);
        EDTUtils.pack(dummy);

        table.setFont(PrintManager.getFont());
    }

    public static JTable createTable(String[]... n) {
        int length = n[0].length;
        for (String[] nx : n) {
            if (length != nx.length) {
                throw new IllegalArgumentException("Length of all arrays must be identical.");
            }
        }
        return EDTUtils.executeOnEDTwithReturn(() -> new JTextTable(n));
    }

    private static class JTextTable extends JTable {

        private static final long serialVersionUID = 1L;

        public JTextTable(String[][] n) {
            super(new DataModel(n));

            TableCellRenderer tcr = new Renderer();
            setDefaultRenderer(String.class, tcr);
            setDefaultRenderer(Object.class, tcr);
        }

        static class Renderer implements TableCellRenderer {

            DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
            JTextArea                area = new JTextArea();

            public Renderer() {
                if (PrintManager.getFont() != null) {
                    area.setFont(PrintManager.getFont());
                }
                area.setForeground(Color.BLACK);
                area.setBackground(Color.WHITE);
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value == null) {
                    value = "";
                }
                switch (column) {
                case 0:
                    String s = value.toString();
                    JLabel label = (JLabel) dtcr.getTableCellRendererComponent(table, s, false, false, row, column);
                    label.setVerticalAlignment(SwingConstants.TOP);
                    if (s.endsWith(":")) {
                        label.setFont(label.getFont().deriveFont(Font.PLAIN));
                    } else {
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    }
                    return label;
                default:
                    int width = table.getColumnModel().getColumn(column).getWidth();
                    area.setMinimumSize(new Dimension(width, 1));
                    area.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
                    area.setSize(new Dimension(width, Integer.MAX_VALUE));
                    area.setText(value.toString());
                    return area;
                }
            }
        }

        static class DataModel implements TableModel {

            final String[][] values;

            public DataModel(String[][] values) {
                this.values = values;
            }

            @Override
            public int getRowCount() {
                return values[0].length;
            }

            @Override
            public int getColumnCount() {
                return values.length;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return "";
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return values[columnIndex][rowIndex];
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                throw new RuntimeException("Not implemented!");
            }

            @Override
            public void addTableModelListener(TableModelListener l) {
                // Nothing to do
            }

            @Override
            public void removeTableModelListener(TableModelListener l) {
                // Nothing to do
            }
        }
    }
}