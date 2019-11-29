/*
 * Created on 07.10.2005
 */
package de.df.jutils.gui.window;

import javax.swing.*;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.layout.FormLayoutUtils;
import de.df.jutils.gui.util.WindowUtils;

public class JTaskDialog extends JDialog {

    private static final long serialVersionUID = -7817053483420697392L;

    private JLabel[]          icons            = null;
    private Icon              icon             = null;

    public JTaskDialog(JFrame parent, String title, boolean modal, String[] tasks, Icon icon) {
        super(parent, title, modal);
        if (tasks == null) {
            throw new NullPointerException("Argument tasks must not be null");
        }
        if (icon == null) {
            throw new NullPointerException("Argument icon must not be null");
        }

        this.icon = icon;

        icons = new JLabel[tasks.length];

        FormLayout layout = new FormLayout("4dlu,center:" + (icon.getIconWidth() + 4) + "px,4dlu,fill:default:grow,4dlu",
                FormLayoutUtils.createDoubleFormLayout(tasks.length + 1));
        setLayout(layout);

        for (int x = 0; x < tasks.length; x++) {
            icons[x] = new JLabel();

            add(icons[x], CC.xy(2, 2 * x + 2));
            add(new JLabel(tasks[x]), CC.xy(4, 2 * x + 2));
        }
        JProgressBar jpg = new JProgressBar();
        jpg.setIndeterminate(true);
        add(jpg, CC.xyw(2, tasks.length * 2 + 2, 3));

        pack();
        WindowUtils.center(this);
    }

    public void setFinished(int index) {
        icons[index].setIcon(icon);
    }

    public void clear() {
        for (JLabel icon1 : icons) {
            icon1.setIcon(null);
        }
    }
}