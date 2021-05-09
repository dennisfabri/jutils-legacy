package de.df.jutils.gui.window;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.layout.FormLayoutUtils;
import de.df.jutils.gui.util.AIconBundle;
import de.df.jutils.gui.util.UIStateUtils;
import de.df.jutils.gui.util.WindowUtils;
import de.df.jutils.i18n.util.JUtilsI18n;

public class JSelectionDialog extends JDialog {

    private static final long serialVersionUID = -9078628731839451352L;

    private JCheckBox[]       boxes;
    private boolean           allowEmpty;

    private JButton           ok;
    private JButton           cancel;

    boolean                   accepted         = false;

    public JSelectionDialog(JFrame parent, String title, String[] options, boolean allowempty, AIconBundle icons) {
        this(parent, title, options, null, allowempty, icons);
    }

    public JSelectionDialog(JFrame parent, String title, String[] options, boolean[] selected, boolean allowempty, AIconBundle icons) {
        super(parent, title, true);
        this.allowEmpty = allowempty;
        this.boxes = new JCheckBox[options.length];
        for (int x = 0; x < options.length; x++) {
            boxes[x] = new JCheckBox(options[x], (selected == null) || selected[x]);
            boxes[x].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkEmpty();
                }
            });
        }

        WindowUtils.addEscapeAction(this);
        WindowUtils.addEnterAction(this, new Runnable() {
            @Override
            public void run() {
                accepted = true;
                setVisible(false);
            }
        });

        ok = new JButton(JUtilsI18n.get("Ok"), icons.getSmallIcon("ok"));
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accepted = true;
                setVisible(false);
            }
        });
        cancel = new JButton(JUtilsI18n.get("Cancel"), icons.getSmallIcon("cancel"));
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accepted = false;
                setVisible(false);
            }
        });

        FormLayout layout = new FormLayout("4dlu,0px:grow,fill:default,4dlu,fill:default,4dlu", FormLayoutUtils.createLayoutString(boxes.length + 1));
        setLayout(layout);

        for (int x = 0; x < boxes.length; x++) {
            add(boxes[x], CC.xyw(2, 2 + 2 * x, 4, "fill,fill"));
        }
        add(ok, CC.xy(3, boxes.length * 2 + 2));
        add(cancel, CC.xy(5, boxes.length * 2 + 2));

        pack();
        setResizable(false);
        WindowUtils.center(this);
        UIStateUtils.uistatemanage(this, "JSelectionDialog." + title);
        pack();
    }

    @Override
    public void setVisible(boolean b) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.setEnabled(!b);
        }
        if (b) {
            accepted = false;
        }
        super.setVisible(b);
    }

    public boolean[] getSelection() {
        boolean[] result = new boolean[boxes.length];
        for (int x = 0; x < boxes.length; x++) {
            result[x] = boxes[x].isSelected();
        }
        return result;
    }

    public boolean checkEmpty() {
        if (getSelectionCount() == 0) {
            if (!allowEmpty) {
                ok.setEnabled(false);
            }
            return true;
        }
        ok.setEnabled(true);
        return false;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public int getSelectionCount() {
        int count = 0;
        for (JCheckBox boxe : boxes) {
            if (boxe.isSelected()) {
                count++;
            }
        }
        return count;
    }
}