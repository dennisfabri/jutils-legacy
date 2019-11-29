package de.df.jutils.gui.jlist;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;

/*
 * Created on 24.04.2005
 */

public class CheckBoxListMouseListener extends MouseAdapter {

    @SuppressWarnings("rawtypes")
    @Override
    public void mouseClicked(MouseEvent e) {
        JList list = (JList) e.getComponent();
        int index = list.locationToIndex(e.getPoint());
        JCheckBox item = (JCheckBox) list.getModel().getElementAt(index);
        item.setSelected(!item.isSelected());
        Rectangle rect = list.getCellBounds(index, index);
        list.repaint(rect);
    }
}