/*
 * Created on 02.05.2005
 */
package de.df.jutils.gui.window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.util.AIconBundle;
import de.df.jutils.i18n.util.JUtilsI18n;

public class JSimpleDialog extends JDialog {

    private static final long serialVersionUID = 3256728398460891959L;

    private JButton           cancel;
    private JComponent        content;

    public JSimpleDialog(JFrame parent, String title, boolean modal, JComponent c, AIconBundle ib) {
        this(parent, title, modal, ib);
        setContent(c);
    }

    public JSimpleDialog(JFrame parent, String title, boolean modal, AIconBundle ib) {
        super(parent, title, modal);
        content = null;

        initUI(ib);
        initListeners();
        addActions();
    }

    private void initUI(AIconBundle ib) {
        cancel = new JButton(JUtilsI18n.get("Close"), ib.getSmallIcon("close"));

        FormLayout layout = new FormLayout("4dlu,0px:grow,fill:default,4dlu", "4dlu,fill:default:grow,4dlu,fill:default,4dlu");
        setLayout(layout);
        add(cancel, CC.xy(3, 4));
    }

    private void initListeners() {
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                fireCancel();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                fireCancel();
            }
        });
    }

    private void addActions() {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 3257572818995525944L;

            @Override
            public void actionPerformed(ActionEvent e) {
                fireCancel();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    protected void fireCancel() {
        setVisible(false);
    }

    public void setContent(JComponent c) {
        if (content != null) {
            remove(content);
        }
        if (c != null) {
            add(c, CC.xyw(2, 2, 2));
        }
        content = c;
    }
}