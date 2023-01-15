package de.df.jutils.gui.autocomplete;

import javax.swing.JComboBox;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

final class JComboBoxAutoCompletion {

    public static <T> void enable(JComboBox<T> box, boolean strict) {
        box.setEditable(!strict);
        AutoCompleteDecorator.decorate(box);
    }

    public static <T> void enable(JComboBox<T> box) {
        enable(box, false);
    }

    private JComboBoxAutoCompletion() {
    }
}
