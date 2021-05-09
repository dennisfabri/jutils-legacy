/*
 * Created on 27.06.2005
 */
package de.df.jutils.gui.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComfortListCellRenderer<T> extends DefaultListCellRenderer {

    private static final long serialVersionUID = -8759120492404434813L;

    private ListCellRenderer<T>  parent           = null;

    public ComfortListCellRenderer() {
        this(null);
    }

    public ComfortListCellRenderer(ListCellRenderer<T> lcr) {
        parent = lcr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component orig = null;
        if (parent == null) {
            orig = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        } else {
            orig = parent.getListCellRendererComponent((JList<T>)list, (T)value, index, isSelected, cellHasFocus);
        }
        if ((value != null) && (value instanceof ListRenderDataProvider)) {
            orig = ((ListRenderDataProvider) value).getListRenderData(orig.getForeground(), orig.getBackground());
        }
        return orig;
    }
}
