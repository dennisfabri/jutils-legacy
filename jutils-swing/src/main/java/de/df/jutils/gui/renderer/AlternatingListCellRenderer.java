/*
 * Created on 15.12.2005
 */
package de.df.jutils.gui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class AlternatingListCellRenderer<E> implements ListCellRenderer<E> {

    protected Color odd;
    protected Color even;
    protected ListCellRenderer<E> tcr;

    public AlternatingListCellRenderer(Color odd, Color even, ListCellRenderer<E> tcr) {
        this.odd = odd;
        this.even = even;
        this.tcr = tcr;
    }

    public ListCellRenderer<E> getListCellRenderer() {
        return tcr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int row, boolean isSelected,
            boolean hasFocus) {
        if (value == null) {
            value = (E) "\u00a0";
        } else {
            if ((value instanceof String) && (value.toString().length() == 0)) {
                value = (E) "\u00a0";
            }
            if (value instanceof String) {
                value = (E) value.toString().replace(' ', '\u00A0');
            }
            if (value instanceof String[]) {
                String[] s = (String[]) value;
                for (int x = 0; x < s.length; x++) {
                    s[x] = s[x].replace(' ', '\u00A0');
                }
            }
        }
        Component c = tcr.getListCellRendererComponent(list, value, row, isSelected, hasFocus);

        if (!isSelected) {
            if (row % 2 == 0) {
                c.setBackground(even);
            } else {
                c.setBackground(odd);
            }
            c.setForeground(UIManager.getDefaults().getColor("Text"));
        }
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            jc.setOpaque(true);
            jc.setBorder(new EmptyBorder(2, 1, 2, 1));
        }
        return c;
    }
}