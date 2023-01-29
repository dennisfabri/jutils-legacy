/*
 * Created on 11.07.2004
 */
package de.df.jutils.gui.wizard;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Dennis Fabri
 * @date 11.07.2004
 */
public class WizardComboBoxPage<T extends Object> extends AWizardPage implements UpdateListener {

    final class JWizardPanel extends JPanel {

        private static final long serialVersionUID = 881455444130820538L;

        JWizardPanel() {
            super(new BorderLayout(5, 5));
        }

        @Override
        public void requestFocus() {
            super.requestFocus();
            if (buttons == null) {
                return;
            }
            if (buttons.length == 0) {
                return;
            }
            buttons[0].requestFocus();
        }
    }

    JComboBox<T>[] buttons;
    private JPanel panel = new JWizardPanel();
    private final JWizard wizard;

    public WizardComboBoxPage(JWizard w, String title, String note, String[] names, T[][] values) {
        this(w, title, note, names, null, values, null);
    }

    private static <T extends Object> void checkArguments(String[] names, boolean[] enabled, T[][] values,
            int[] selection) {
        if (names == null) {
            throw new NullPointerException("String[] options must " + "not be null!");
        }
        if ((enabled != null) && (enabled.length != names.length)) {
            throw new IllegalArgumentException(
                    "boolean[] enabled must be null" + " or have the same length as String[] names!");
        }
        if (values == null) {
            throw new IllegalArgumentException("T[][] values must not be null!");
        }
        if (values.length != names.length) {
            throw new IllegalArgumentException("T[][] values have the same length as String[] names!");
        }
        for (T[] value : values) {
            if (value == null) {
                throw new IllegalArgumentException("T[][] values must not contain a null-array");
            }
            if (value.length < 1) {
                throw new IllegalArgumentException("T[][] values must not contain an empty array");
            }
        }
        if ((selection != null) && (selection.length != names.length)) {
            throw new IllegalArgumentException(
                    "int[] selection must be null" + " or have the same length as String[] names!");
        }
        if (selection != null) {
            for (int x = 0; x < values.length; x++) {
                if (values[x].length <= selection[x]) {
                    throw new IllegalArgumentException("Selected index must be lower than corresponding value count!");
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public WizardComboBoxPage(JWizard w, String title, String note, String[] names, boolean[] enabled, T[][] values,
            int[] selection) {
        super(title, note);
        checkArguments(names, enabled, values, selection);
        wizard = w;
        StringBuilder topdown = new StringBuilder("0px:grow");
        for (int x = 0; x < names.length; x++) {
            topdown.append(",4dlu,fill:default");
        }
        topdown.append(",4dlu:grow");
        panel.setLayout(new FormLayout("4dlu,fill:default,4dlu,fill:default:grow,4dlu", topdown.toString()));
        buttons = new JComboBox[names.length];
        for (int x = 0; x < names.length; x++) {
            T[] value = values[x];
            int index = 0;
            if (selection != null) {
                index = selection[x];
            }
            buttons[x] = new JComboBox<>(value);
            buttons[x].setSelectedIndex(index);
            if (enabled != null) {
                buttons[x].setEnabled(enabled[x]);
            }
            buttons[x].addItemListener(e -> {
                notifyUpdate();
            });
            panel.add(new JLabel(names[x] + ":"), CC.xy(2, 3 + 2 * x));
            panel.add(buttons[x], CC.xy(4, 3 + 2 * x));
        }
    }

    public final void setEnabled(int index, boolean enabled) {
        buttons[index].setEnabled(enabled);
    }

    public int getItemCount() {
        return buttons.length;
    }

    public int getSelectedIndex(int index) {
        return buttons[index].getSelectedIndex();
    }

    @SuppressWarnings("unchecked")
    public T getSelectedItem(int index) {
        return (T) buttons[index].getSelectedItem();
    }

    @Override
    public JComponent getPage() {
        return panel;
    }

    @Override
    public void update() {
        if (wizard.isCurrentPage(this)) {
            wizard.setNextButtonEnabled(true);
        }
    }

    protected void notifyUpdate() {
        wizard.notifyUpdate();
    }
}
