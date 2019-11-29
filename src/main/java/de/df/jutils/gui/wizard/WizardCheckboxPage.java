/*
 * Created on 11.07.2004
 */
package de.df.jutils.gui.wizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Dennis Mueller
 * @date 11.07.2004
 */
public class WizardCheckboxPage extends AWizardPage implements UpdateListener {

    final class JWizardPanel extends JPanel {
        private static final long serialVersionUID = 8349265144249514814L;

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

    JCheckBox[]           buttons = null;
    private JPanel        panel   = new JWizardPanel();
    private final JWizard wizard;

    public WizardCheckboxPage(JWizard w, String title, String note, String[] options) {
        this(w, title, note, options, null, null, null);
    }

    public WizardCheckboxPage(JWizard w, String title, String note, String[] options, String[] tooltips) {
        this(w, title, note, options, tooltips, null, null);
    }

    public WizardCheckboxPage(JWizard w, String title, String note, String[] options, boolean[] selected) {
        this(w, title, note, options, null, null, selected);
    }

    public WizardCheckboxPage(JWizard w, String title, String note, String[] options, String[] tooltips, boolean[] selected) {
        this(w, title, note, options, tooltips, null, selected);
    }

    public WizardCheckboxPage(JWizard w, String title, String note, String[] options, String[] tooltips, boolean[] enabled, boolean[] selected) {
        super(title, note);
        wizard = w;
        if (options == null) {
            throw new NullPointerException("String[] options must " + "not be null!");
        }
        if ((enabled != null) && (enabled.length != options.length)) {
            throw new IllegalArgumentException("boolean[] enabled must be null" + " or have the same length as String[] options!");
        }
        if ((selected != null) && (selected.length != options.length)) {
            throw new IllegalArgumentException("boolean[] enabled must be null" + " or have the same length as String[] options!");
        }
        StringBuffer topdown = new StringBuffer("0px:grow");
        for (int x = 0; x < options.length; x++) {
            topdown.append(",4dlu,fill:default");
        }
        topdown.append(",4dlu:grow");
        panel.setLayout(new FormLayout("4dlu,fill:default:grow,4dlu", topdown.toString()));
        buttons = new JCheckBox[options.length];
        for (int x = 0; x < options.length; x++) {
            buttons[x] = new JCheckBox(options[x]);
            if ((tooltips != null) && (tooltips[x] != null) && (tooltips[x].length() > 0)) {
                buttons[x].setToolTipText(tooltips[x]);
            }
            if (enabled != null) {
                buttons[x].setEnabled(enabled[x]);
            }
            panel.add(buttons[x], CC.xy(2, 3 + 2 * x));
            if (selected != null) {
                setSelectedIndexI(x, selected[x]);
            }
            buttons[x].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    notifyUpdate();
                    notifySelectionChange();
                }
            });
        }
    }

    public final int firstSelectedIndex() {
        for (int x = 0; x < buttons.length; x++) {
            if (buttons[x].isSelected()) {
                return x;
            }
        }
        return -1;
    }

    public final void setEnabled(int index, boolean enabled) {
        buttons[index].setEnabled(enabled);
        // if ((!enabled) && (buttons[index].isSelected())) {
        // for (JCheckBox button : buttons) {
        // if (button.isEnabled()) {
        // button.setSelected(true);
        // return;
        // }
        // }
        // }
    }

    public final void setSelection(int index, boolean value) {
        setSelectedIndexI(index, value);
    }

    private void setSelectedIndexI(int index, boolean value) {
        if (buttons[index].isEnabled()) {
            buttons[index].setSelected(value);
        }
    }

    private LinkedList<ChangeListener> listeners = new LinkedList<ChangeListener>();

    public void addSelectionListener(ChangeListener cl) {
        listeners.addLast(cl);
    }

    public void removeSelectionListener(ChangeListener cl) {
        listeners.remove(cl);
    }

    private class SelectionChangeEvent extends ChangeEvent {

        private static final long serialVersionUID = 5861178658890265455L;

        public SelectionChangeEvent() {
            super(WizardCheckboxPage.this);
        }
    }

    void notifySelectionChange() {
        ChangeEvent ce = new SelectionChangeEvent();
        for (ChangeListener cl : listeners) {
            cl.stateChanged(ce);
        }
    }

    public boolean isSelected(int index) {
        return buttons[index].isSelected();
    }

    public int getItemCount() {
        return buttons.length;
    }

    public final void setOptionName(int index, String name) {
        buttons[index].setText(name);
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