/*
 * Created on 11.07.2004
 */
package de.df.jutils.gui.wizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Dennis Fabri
 * @date 11.07.2004
 */
public class WizardOptionPage extends AWizardPage implements UpdateListener {

    private final class JWizardPanel extends JPanel {

        private static final long serialVersionUID = 6260468761554139626L;

        JWizardPanel() {
            super(new BorderLayout(5, 5));
        }

        @Override
        public void requestFocus() {
            super.requestFocus();
            if (buttons == null) {
                return;
            }
            for (JRadioButton button : buttons) {
                if (button.isSelected()) {
                    button.requestFocus();
                    break;
                }
            }
        }
    }

    private ButtonGroup group = new ButtonGroup();
    private JRadioButton[] buttons;
    private JPanel panel = new JWizardPanel();
    private final JWizard wizard;

    public WizardOptionPage(JWizard w, String title, String note, String[] options) {
        this(w, title, note, options, null, 0, null, SwingConstants.CENTER);
    }

    public WizardOptionPage(JWizard w, String title, String note, String[] options, int selectedIndex) {
        this(w, title, note, options, null, selectedIndex, null, SwingConstants.CENTER);
    }

    public WizardOptionPage(JWizard w, String title, String note, String[] options, boolean[] enabled,
            int selectedIndex, String explanation, int align) {
        super(title, note);
        wizard = w;
        if (options == null) {
            throw new NullPointerException("String[] options must " + "not be null!");
        }
        if ((enabled != null) && (enabled.length != options.length)) {
            throw new IllegalArgumentException(
                    "boolean[] enabled must be null" + " or have the same length as String[] options!");
        }
        if ((selectedIndex < 0) || (selectedIndex >= options.length)) {
            throw new IndexOutOfBoundsException(
                    "selectedIndex must be at least zero and lower " + "than the length of String[] options");
        }
        if (explanation != null) {
            if (explanation.trim().length() == 0) {
                explanation = null;
            }
        }
        StringBuilder topdown = new StringBuilder();
        if (explanation != null) {
            topdown.append("4dlu,fill:default,4dlu,");
        } else {
            topdown.append("0dlu,0dlu,0dlu,");
        }
        if (align == SwingConstants.TOP) {
            topdown.append("0dlu");
        } else {
            topdown.append("0dlu:grow");
        }
        for (int x = 0; x < options.length; x++) {
            topdown.append(",4dlu,fill:default");
        }
        if (align == SwingConstants.BOTTOM) {
            topdown.append(",4dlu");
        } else {
            topdown.append(",4dlu:grow");
        }
        panel.setLayout(new FormLayout("4dlu,fill:default:grow,4dlu", topdown.toString()));

        if (explanation != null) {
            panel.add(new JLabel(explanation), CC.xy(2, 2));
        }

        buttons = new JRadioButton[options.length];
        for (int x = 0; x < options.length; x++) {
            buttons[x] = new JRadioButton(options[x]);
            buttons[x].addKeyListener(new MoveListener(x));
            if (enabled != null) {
                buttons[x].setEnabled(enabled[x]);
            }
            panel.add(buttons[x], CC.xy(2, 3 + 3 + 2 * x));
            buttons[x].addActionListener(arg0 -> {
                notifyUpdate();
            });
            buttons[x].addChangeListener(e -> {
                notifyUpdate();
            });
            group.add(buttons[x]);
        }
        setSelectedIndex(selectedIndex);
        buttons[selectedIndex].requestFocus();
    }

    public final int getSelectedIndex() {
        for (int x = 0; x < buttons.length; x++) {
            if (buttons[x].isSelected()) {
                return x;
            }
        }
        return -1;
    }

    public final void setEnabled(int index, boolean enabled) {
        if (enabled != buttons[index].isEnabled()) {
            buttons[index].setEnabled(enabled);
            if ((!enabled) && (buttons[index].isSelected())) {
                for (JRadioButton button : buttons) {
                    if (button.isEnabled()) {
                        button.setSelected(true);
                        return;
                    }
                }
            }
            wizard.notifyUpdate();
        }
    }

    public final void setSelectedIndex(int index) {
        setSelectedIndexI(index);
    }

    private void setSelectedIndexI(int index) {
        if (buttons[index].isEnabled()) {
            if (!buttons[index].isSelected()) {
                buttons[index].setSelected(true);
                wizard.notifyUpdate();
            }
        }
    }

    public final String getSelectedItemname() {
        int index = getSelectedIndex();
        if (index < 0) {
            return null;
        }
        return buttons[index].getText();
    }

    public int getItemCount() {
        return buttons.length;
    }

    @Override
    public JComponent getPage() {
        return panel;
    }

    @Override
    public void update() {
        if (wizard.isCurrentPage(this)) {
            int index = getSelectedIndex();
            if (index < 0) {
                wizard.setNextButtonEnabled(false);
            } else {
                if (!buttons[index].isEnabled()) {
                    wizard.setNextButtonEnabled(false);
                } else {
                    wizard.setNextButtonEnabled(true);
                    buttons[index].requestFocus();
                }
            }
        }
    }

    private void notifyUpdate() {
        wizard.notifyUpdate();
    }

    private class MoveListener implements KeyListener {

        private int index;

        public MoveListener(int index) {
            this.index = index;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.isConsumed()) {
                return;
            }
            if (e.getSource() != buttons[index]) {
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                e.consume();
                int x = index - 1;
                while ((x >= 0) && (!buttons[x].isEnabled())) {
                    x--;
                }
                if (x >= 0) {
                    buttons[x].requestFocus();
                    buttons[x].setSelected(true);
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                e.consume();
                int x = index + 1;
                while ((x < buttons.length) && (!buttons[x].isEnabled())) {
                    x++;
                }
                if (x < buttons.length) {
                    buttons[x].requestFocus();
                    buttons[x].setSelected(true);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Nothing to do
        }

        @Override
        public void keyTyped(KeyEvent e) {
            keyPressed(e);
        }
    }
}
