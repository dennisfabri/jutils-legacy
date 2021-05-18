/*
 * Created on 11.07.2004
 */
package de.df.jutils.gui.wizard;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.JIntSpinner;

/**
 * @author Dennis Fabri
 * @date 11.07.2004
 */
public class WizardIntegerPage extends AWizardPage implements UpdateListener {

    private final class JWizardPanel extends JPanel {

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

    private JIntSpinner[]         buttons = null;
    private JPanel        panel   = new JWizardPanel();
    private final JWizard wizard;

    public WizardIntegerPage(JWizard w, String title, String note, String[] names) {
        this(w, title, note, names, null);
    }

    public WizardIntegerPage(JWizard w, String title, String note, String[] names, int[] values) {
        this(w, title, note, names, null, values, null, null);
    }

    private static void checkArguments(String[] names, boolean[] enabled, int[] values, int[] min, int[] max) {
        if (names == null) {
            throw new NullPointerException("String[] options must " + "not be null!");
        }
        if ((enabled != null) && (enabled.length != names.length)) {
            throw new IllegalArgumentException("boolean[] enabled must be null" + " or have the same length as String[] options!");
        }
        if ((values != null) && (values.length != names.length)) {
            throw new IllegalArgumentException("boolean[] enabled must be null" + " or have the same length as String[] options!");
        }
        if ((min != null) && (min.length != names.length)) {
            throw new IllegalArgumentException("int[] enabled must be null" + " or have the same length as String[] options!");
        }
        if ((max != null) && (max.length != names.length)) {
            throw new IllegalArgumentException("int[] enabled must be null" + " or have the same length as String[] options!");
        }
    }

    public WizardIntegerPage(JWizard w, String title, String note, String[] names, boolean[] enabled, int[] values, int[] min, int[] max) {
        super(title, note);
        checkArguments(names, enabled, values, min, max);
        wizard = w;
        StringBuilder topdown = new StringBuilder("0px:grow");
        for (int x = 0; x < names.length; x++) {
            topdown.append(",4dlu,fill:default");
        }
        topdown.append(",4dlu:grow");
        panel.setLayout(new FormLayout("4dlu,fill:default,4dlu,fill:default:grow,4dlu", topdown.toString()));
        buttons = new JIntSpinner[names.length];
        for (int x = 0; x < names.length; x++) {
            int value = 0;
            int minimum = JIntSpinner.NO_MIN;
            int maximum = JIntSpinner.NO_MAX;
            if (values != null) {
                value = values[x];
            }
            if (min != null) {
                minimum = min[x];
            }
            if (max != null) {
                maximum = max[x];
            }
            buttons[x] = new JIntSpinner(value, minimum, maximum, 1);
            if (enabled != null) {
                buttons[x].setEnabled(enabled[x]);
            }
            buttons[x].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent arg0) {
                    notifyUpdate();
                }
            });
            panel.add(new JLabel(names[x] + ":"), CC.xy(2, 3 + 2 * x));
            panel.add(buttons[x], CC.xy(4, 3 + 2 * x));
        }
    }

    public int getItemCount() {
        return buttons.length;
    }

    public int getInt(int index) {
        return buttons[index].getInt();
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

    private void notifyUpdate() {
        wizard.notifyUpdate();
    }
}