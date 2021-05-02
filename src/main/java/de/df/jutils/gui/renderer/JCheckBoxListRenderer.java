package de.df.jutils.gui.renderer;

/**
 * Taken from an Example of www.java2s.com
 */

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/*
 * Created on 24.04.2005
 */

public class JCheckBoxListRenderer implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {

        JCheckBox box = (JCheckBox) value;
        box.setText(box.getText().replace(' ', '\u00a0'));
        box.setBackground(UIManager.getColor("List.textBackground"));
        box.setForeground(UIManager.getColor("List.textForeground"));
        box.setEnabled(list.isEnabled());
        box.setFont(list.getFont());

        return box;
    }
}