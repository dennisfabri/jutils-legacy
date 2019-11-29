/*
 * Created on 27.06.2005
 */
package de.df.jutils.gui.renderer;

import java.awt.Component;

import javax.swing.*;

public class ComfortListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -8759120492404434813L;

    private ListCellRenderer  parent           = null;

    public ComfortListCellRenderer() {
        this(null);
    }

    public ComfortListCellRenderer(ListCellRenderer lcr) {
        parent = lcr;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component orig = null;
        if (parent == null) {
            orig = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        } else {
            orig = parent.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
        if ((value != null) && (value instanceof ListRenderDataProvider)) {
            orig = ((ListRenderDataProvider) value).getListRenderData(orig.getForeground(), orig.getBackground());
        }
        return orig;
    }
}
