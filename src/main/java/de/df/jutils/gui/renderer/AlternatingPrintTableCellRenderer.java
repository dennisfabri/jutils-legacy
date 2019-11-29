/*
 * Created on 15.12.2005
 */
package de.df.jutils.gui.renderer;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import de.df.jutils.gui.util.EDTUtils;

public class AlternatingPrintTableCellRenderer extends AlternatingTableCellRenderer {

    public AlternatingPrintTableCellRenderer(Color odd, Color even, TableCellRenderer tcr) {
        super(odd, even, tcr);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        CellUpdater cu = new CellUpdater(table, value, isSelected, hasFocus, row, column);
        EDTUtils.executeOnEDT(cu);
        return cu.getResult();
    }

    private class CellUpdater implements Runnable {

        JTable    table;
        Object    value;
        // boolean isSelected;
        boolean   hasFocus;
        int       row;
        int       column;

        Component result = null;

        public CellUpdater(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.table = table;
            this.value = value;
            // this.isSelected = isSelected;
            this.hasFocus = hasFocus;
            this.row = row;
            this.column = column;
        }

        public Component getResult() {
            return result;
        }

        @Override
        public void run() {
            Component c = tcr.getTableCellRendererComponent(table, value, false, hasFocus, row, column);
            if (row % 2 == 0) {
                c.setBackground(even);
            } else {
                c.setBackground(odd);
            }
            c.setForeground(UIManager.getDefaults().getColor("Text"));
            if (table.getColumnModel().getColumn(column).getMaxWidth() == 0) {
                c.setForeground(c.getBackground());
            }
            if (value.toString().length() == 0) {
                c.setForeground(c.getBackground());
            }
            // c.repaint();

            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.setOpaque(true);
            }

            int low = 0;
            int right = 0;
            if (table.getShowHorizontalLines()) {
                if (column + 1 < table.getColumnCount()) {
                    right = 2;
                }
            }
            if (table.getShowVerticalLines()) {
                if (row + 1 < table.getRowCount()) {
                    low = 2;
                }
            }

            JPanel p = new JPanel(new BorderLayout(0, 0));
            p.setSize(c.getWidth() + right, c.getHeight() + low);
            p.add(c, BorderLayout.CENTER);
            p.setBorder(new EmptyBorder(0, 1, low, right + 1));

            result = p;
        }
    }
}