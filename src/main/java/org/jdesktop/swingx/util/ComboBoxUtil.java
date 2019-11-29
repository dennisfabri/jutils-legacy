package org.jdesktop.swingx.util;

import java.awt.Component;

import javax.swing.*;

public class ComboBoxUtil {

    private ComboBoxUtil() {
    }

    public static JTextField getEditorComponent(JComboBox<?> comboBox) {
        ComboBoxEditor editor = comboBox.getEditor();
        if (editor == null) {
            return null;
        }
        Component c = editor.getEditorComponent();
        if (c instanceof JLayer) {
            c = ((JLayer<?>) c).getView();
        }
        if (c instanceof JTextField) {
            return (JTextField) c;
        }
        return null;
    }
}
