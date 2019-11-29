/*
 * Created on 02.05.2005
 */
package de.df.jutils.gui.layout;

import java.awt.Component;
import java.awt.Font;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.Border;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.df.jutils.gui.JLabelSeparator;

public class SimpleListBuilder {

    private static final String       HORIZONTAL = "4dlu,fill:default:grow,4dlu";

    private final FormLayout          layout;
    private final JPanel              panel;
    private final LinkedList<Integer> fills      = new LinkedList<Integer>();

    private final String              spacer;

    public SimpleListBuilder(JPanel panel, FormLayout layout, int spacing) {
        spacer = "" + spacing + "dlu";
        if (layout == null) {
            layout = new FormLayout(HORIZONTAL, spacer);
        }
        if (panel == null) {
            panel = new JPanel();
        }
        this.layout = layout;
        this.panel = panel;
        panel.setLayout(layout);
    }

    public SimpleListBuilder(JPanel panel, FormLayout layout) {
        this(panel, layout, 4);
    }

    public SimpleListBuilder(FormLayout layout) {
        this(null, layout, 4);
    }

    public SimpleListBuilder(JPanel panel) {
        this(panel, null, 4);
    }

    public SimpleListBuilder() {
        this(null, null, 4);
    }

    public SimpleListBuilder(int spacer) {
        this(null, null, spacer);
    }

    public void addText(String name) {
        JLabel jls = new JLabel(name);
        jls.setFont(panel.getFont());
        add(jls);
    }

    public void addButton(JButton c) {
        addRow();
        int index = layout.getRowCount();
        addEmptyRow();
        c.setFont(panel.getFont());
        panel.add(c, CC.xy(2, index, "right,fill"));
    }

    public void setFont(Font f) {
        panel.setFont(f);
    }

    public void add(Component c) {
        addRow();
        int index = layout.getRowCount();
        addEmptyRow();
        c.setFont(panel.getFont());
        panel.add(c, CC.xy(2, index));
    }

    public void addSpace() {
        add(new JLabel(" "));
    }

    public void addSeparator(String name) {
        if ((name == null) || (name.length() == 0)) {
            add(new JSeparator());
        } else {
            JLabelSeparator jls = new JLabelSeparator(name);
            jls.setFont(panel.getFont());
            add(jls);
        }
    }

    private void addEmptyRow() {
        addRow(spacer);
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
        return getPanel(true);
    }

    public JPanel getPanel(boolean rowgroups) {
        if (rowgroups) {
            int[] fr = getFilledRows();
            if (fr.length > 1) {
                layout.setRowGroups(new int[][] { fr });
            } else {
                layout.setRowGroups(new int[0][0]);
            }
        } else {
            layout.setRowGroups(new int[0][0]);
        }
        return panel;
    }

    public void setBorder(Border b) {
        panel.setBorder(b);
    }

    public int getRowCount() {
        return layout.getRowCount();
    }
}