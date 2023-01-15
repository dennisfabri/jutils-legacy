/*
 * Created on 15.12.2005
 */
package de.df.jutils.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class AlternatingTableCellRenderer implements TableCellRenderer {

    protected Color odd;
    protected Color even;
    protected TableCellRenderer tcr;

    public AlternatingTableCellRenderer(Color odd, Color even, TableCellRenderer tcr) {
        this.odd = odd;
        this.even = even;
        this.tcr = tcr;
    }

    public TableCellRenderer getTableCellRenderer() {
        return tcr;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (value == null) {
            value = "\u00a0";
        } else {
            if ((value instanceof String) && (value.toString().length() == 0)) {
                value = "\u00a0";
            }
            if (value instanceof String) {
                value = value.toString().replace(' ', '\u00A0');
            }
            if (value instanceof String[]) {
                String[] s = (String[]) value;
                for (int x = 0; x < s.length; x++) {
                    s[x] = s[x].replace(' ', '\u00A0');
                }
            }
        }
        Component c = tcr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (isSelected) {
            return c;
        }

        if (row % 2 == 0) {
            setBackground(c, even);
        } else {
            setBackground(c, odd);
        }
        return c;
    }

    public void setColors(Color odd, Color even) {
        this.odd = odd;
        this.even = even;
    }

    private void setBackground(Component c, Color col) {
        c.setBackground(col);
        if (c instanceof JComponent) {
            Component[] cs = ((JComponent) c).getComponents();
            for (Component co : cs) {
                setBackground(co, col);
                if (co.getBackground() != null) {
                    co.setBackground(col);
                }
            }
        }

    }
}
