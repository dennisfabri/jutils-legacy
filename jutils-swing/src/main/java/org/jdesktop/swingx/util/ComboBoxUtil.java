package org.jdesktop.swingx.util;

import java.awt.Component;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JLayer;
import javax.swing.JTextField;

public final class ComboBoxUtil {

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
