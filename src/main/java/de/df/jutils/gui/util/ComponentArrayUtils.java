/*
 * Created on 02.05.2004
 */
package de.df.jutils.gui.util;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import de.df.jutils.gui.JIntegerField;
import de.df.jutils.gui.JWarningTextField;

/**
 * @author Dennis Mueller
 * @date 02.05.2004
 */
public final class ComponentArrayUtils {

    private ComponentArrayUtils() {
        // Never called
    }

    public static JIntegerField[] getIntegerFields(int count) {
        JIntegerField[] jnf = new JIntegerField[count];
        for (int x = 0; x < count; x++) {
            jnf[x] = new JIntegerField();
        }
        return jnf;
    }

    public static JTextField[] getTextFields(int count) {
        JTextField[] jnf = new JTextField[count];
        for (int x = 0; x < count; x++) {
            jnf[x] = new JTextField();
        }
        return jnf;
    }

    public static JWarningTextField[] getWarningTextFields(int count) {
        JWarningTextField[] jnf = new JWarningTextField[count];
        for (int x = 0; x < count; x++) {
            jnf[x] = new JWarningTextField();
        }
        return jnf;
    }

    public static JComboBox<String>[] getComboBoxes(int count, String[] data) {
        @SuppressWarnings({ "cast", "unchecked" })
        JComboBox<String>[] jbb = (JComboBox<String>[]) new JComboBox[count];
        for (int x = 0; x < count; x++) {
            jbb[x] = new JComboBox<String>(data);
            jbb[x].setSelectedIndex(0);
        }
        return jbb;
    }
}
