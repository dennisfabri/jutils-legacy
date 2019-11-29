/*
 * Created on 02.05.2005
 */
package de.df.jutils.gui.layout;

import java.awt.Component;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SimpleTableBuilder {

    private final FormLayout          layout;
    private final JPanel              panel;
    private final LinkedList<Integer> fills          = new LinkedList<Integer>();
    private int                       columns        = 0;
    private int                       currentColumn  = 1;
    private String                    emptyContraint = "4dlu";

    private int[][]                   groups;

    public SimpleTableBuilder(JPanel panel, int doubleColumns, boolean emptygrow) {
        if (emptygrow) {
            emptyContraint = "2dlu:grow";
        }
        columns = doubleColumns * 2;
        currentColumn = columns + 1;
        layout = new FormLayout(FormLayoutUtils.createDoubleFormLayout(doubleColumns), "4dlu");
        if (panel == null) {
            panel = new JPanel();
        }
        this.panel = panel;
        panel.setLayout(layout);

        groups = new int[1][columns / 2];
        for (int x = 0; x < columns / 2; x++) {
            groups[0][x] = x * 4 + 4;
        }
    }

    public SimpleTableBuilder(JPanel panel, boolean[] grow, boolean group) {
        columns = grow.length;
        currentColumn = columns + 1;
        StringBuffer sb = new StringBuffer("4dlu");
        for (boolean aGrow1 : grow) {
            if (aGrow1) {
                sb.append(",fill:default:grow,4dlu");
            } else {
                sb.append(",fill:default,4dlu");
            }
        }
        layout = new FormLayout(sb.toString(), "4dlu");
        if (panel == null) {
            panel = new JPanel();
        }
        this.panel = panel;
        panel.setLayout(layout);
        if (group) {
            int growing = 0;
            for (boolean aGrow : grow) {
                if (aGrow) {
                    growing++;
                }
            }
            groups = new int[2][0];
            groups[0] = new int[growing];
            groups[1] = new int[grow.length - growing];
            int pos0 = 0;
            int pos1 = 0;
            for (int x = 0; x < grow.length; x++) {
                if (grow[x]) {
                    groups[0][pos0] = (x + 1) * 2;
                    pos0++;
                } else {
                    groups[1][pos1] = (x + 1) * 2;
                    pos1++;
                }
            }
        } else {
            groups = new int[0][0];
        }
    }

    public SimpleTableBuilder(JPanel panel) {
        this(panel, 1, false);
    }

    public SimpleTableBuilder() {
        this(null);
    }

    public void add(String name) {
        add(name, 1);
    }

    public void addSeparator(Component comp) {
        add(comp, true);
        fills.removeLast();
    }

    public void add(String name, int cols) {
        JLabel label = null;
        if (name != null) {
            label = new JLabel(name);
        }
        add(label, cols, "fill,fill");
    }

    public void add(String name, boolean fill) {
        add(name, fill, "fill,fill");
    }

    public void add(String name, boolean fill, String spec) {
        JLabel label = null;
        if (name != null) {
            label = new JLabel(name);
        }
        add(label, fill, spec);
    }

    public void addRow() {
        if (currentColumn > 1) {
            currentColumn = columns + 1;
        } else {
            newRow();
            newEmptyRow();
        }
    }

    public void add(Component c, String constraints) {
        add(c, 1, constraints);
    }

    public void add(Component c) {
        add(c, 1, "fill,fill");
    }

    public void add(Component c, int cols) {
        add(c, cols, "fill,fill");
    }

    public void add(Component c, boolean fill) {
        add(c, fill, "fill,fill");
    }

    public void add(Component c, boolean fill, String spec) {
        if (fill) {
            if (currentColumn > columns) {
                currentColumn = 1;
                newRow();
                newEmptyRow();
            }
            add(c, columns - currentColumn + 1, spec);
        } else {
            add(c, 1, spec);
        }
    }

    public void add(Component c, int cols, String spec) {
        if (currentColumn > columns) {
            currentColumn = 1;
            newRow();
            newEmptyRow();
        }
        int index = layout.getRowCount() - 1;
        if (c != null) {
            panel.add(c, CC.xyw(currentColumn * 2, index, cols * 2 - 1, spec));
        }
        currentColumn += cols;
        if (currentColumn > columns + 1) {
            throw new IndexOutOfBoundsException("Current double column is " + (currentColumn - 1) + " but should be " + columns + " at max.");
        }
    }

    private void newEmptyRow() {
        newRow(emptyContraint);
    }

    private void newRow() {
        newRow("fill:default");
        fills.addLast(layout.getRowCount());
    }

    private void newRow(String spec) {
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

    public int[][] getColumnGroups() {
        return groups;
    }

    public JPanel getPanel() {
        return getPanel(true);
    }

    public JPanel getPanel(boolean columngrouping) {
        return getPanel(true, columngrouping);
    }

    public JPanel getPanel(boolean rowgrouping, boolean columngrouping) {
        if (rowgrouping) {
            int[] fr = getFilledRows();
            if (fr.length > 1) {
                layout.setRowGroups(new int[][] { fr });
            } else {
                layout.setRowGroups(new int[0][0]);
            }
        }
        if (columngrouping) {
            int[][] cg = getColumnGroups();
            if (cg.length > 0 && cg[0].length > 1) {
                layout.setColumnGroups(cg);
            } else {
                layout.setColumnGroups(new int[0][0]);
            }
        }
        return panel;
    }

    public int getRowCount() {
        return layout.getRowCount();
    }
}