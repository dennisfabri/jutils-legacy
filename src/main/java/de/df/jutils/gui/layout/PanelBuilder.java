/*
 * Created on 02.05.2005
 */
package de.df.jutils.gui.layout;

import java.awt.Component;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.df.jutils.gui.JLabelSeparator;

public class PanelBuilder {

    private final FormLayout          layout;
    private final JPanel              panel;

    private final LinkedList<Integer> fills = new LinkedList<Integer>();

    public PanelBuilder(String columns) {
        layout = new FormLayout(columns, "4dlu");
        panel = new JPanel(layout);
    }

    public void add(Object... name) {
        add(false, name);
    }

    public void add(boolean title, Object... name) {
        Component[] c = new Component[name.length];
        for (int x = 0; x < name.length; x++) {
            if (name[x] instanceof Component) {
                c[x] = (Component) name[x];
            } else {
                if (name[x] == null) {
                    c[x] = new JLabel();
                } else {
                    c[x] = new JLabel(name[x].toString());
                }
            }
        }
        add(c, title);
    }

    public void add(Component[] c, boolean title) {
        if ((c == null) || (c.length == 0)) {
            return;
        }
        addRow();
        int index = layout.getRowCount();
        addEmptyRow();
        int offset = 2;
        if (title) {
            for (int x = 0; x < c.length - 1; x++) {
                panel.add(c[x], CC.xy(offset, index, "center,fill"));
                offset += 2;
            }
            if (c.length < layout.getColumnCount() / 2) {
                panel.add(c[c.length - 1], CC.xyw(offset, index, layout.getColumnCount() - offset, "center,fill"));
            } else {
                panel.add(c[c.length - 1], CC.xy(offset, index, "center,fill"));
            }
        } else {
            for (int x = 0; x < c.length - 1; x++) {
                panel.add(c[x], CC.xy(offset, index));
                offset += 2;
            }
            if (c.length < layout.getColumnCount() / 2) {
                panel.add(c[c.length - 1], CC.xyw(offset, index, layout.getColumnCount() - offset));
            } else {
                panel.add(c[c.length - 1], CC.xy(offset, index));
            }
        }
    }

    public void addSeparator(String name) {
        if ((name == null) || (name.length() == 0)) {
            add(new JSeparator());
        } else {
            add(new JLabelSeparator(name));
        }
    }

    private void addEmptyRow() {
        addRow("4dlu");
    }

    private void addRow() {
        addRow("fill:default");
        fills.addLast(layout.getRowCount());
    }

    private void addRow(String spec) {
        layout.appendRow(RowSpec.decode(spec));
    }

    private int[] getFilledRows() {
        Integer[] ints = fills.toArray(new Integer[fills.size()]);
        int[] result = new int[ints.length];
        for (int x = 0; x < ints.length; x++) {
            result[x] = ints[x];
        }
        return result;
    }

    public JPanel getPanel() {
        int[] fr = getFilledRows();
        if (fr.length > 1) {
            layout.setRowGroups(new int[][] { fr });
        } else {
            layout.setRowGroups(new int[0][0]);
        }
        return panel;
    }

    public int getRowCount() {
        return layout.getRowCount();
    }
}