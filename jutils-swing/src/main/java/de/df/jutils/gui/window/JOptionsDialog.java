/*
 * Created on 02.05.2005
 */
package de.df.jutils.gui.window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.util.AIconBundle;
import de.df.jutils.i18n.util.JUtilsI18n;

public class JOptionsDialog extends JDialog {

    private static final long           serialVersionUID = 3256728398460891959L;

    private JButton                     cancel;
    private JButton                     ok;
    private JButton                     apply;
    private boolean                     changed;
    private boolean                     isOk;
    private JComponent                  content;
    private LinkedList<OptionsListener> listeners;
    private AIconBundle                 ib;

    public JOptionsDialog(JFrame parent, String title, boolean modal, JComponent c, AIconBundle ib) {
        this(parent, title, modal, ib);
        setContent(c);
    }

    public JOptionsDialog(JFrame parent, String title, boolean modal, AIconBundle ib) {
        super(parent, title, modal);
        this.ib = ib;
        content = null;
        changed = false;
        isOk = true;
        listeners = new LinkedList<>();

        initUI();
        initListeners();
        addActions();
        setChanged(false);
    }

    private void initUI() {
        cancel = new JButton(JUtilsI18n.get("Close"), ib.getSmallIcon("close"));
        ok = new JButton(JUtilsI18n.get("Ok"), ib.getSmallIcon("ok"));
        apply = new JButton(JUtilsI18n.get("Apply"), ib.getSmallIcon("apply"));

        FormLayout layout = new FormLayout("4dlu,fill:default:grow,4dlu", "4dlu,fill:default:grow,4dlu,fill:default,4dlu");
        setLayout(layout);
        add(getButtons(), CC.xy(2, 4));
    }

    private JComponent getButtons() {
        FormLayout layout = new FormLayout("0dlu:grow,fill:default,4dlu,fill:default,4dlu,fill:default,0dlu", "0dlu,fill:default,0dlu");
        layout.setColumnGroups(new int[][] { { 2, 4, 6 } });
        JPanel p = new JPanel(layout);
        p.add(ok, CC.xy(2, 2));
        p.add(apply, CC.xy(4, 2));
        p.add(cancel, CC.xy(6, 2));
        return p;
    }

    private void initListeners() {
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                fireApply();
            }
        });
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                fireApply();
            }
        });
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

    protected void fireApply() {
        setChanged(false);
        ListIterator<OptionsListener> li = listeners.listIterator();
        while (li.hasNext()) {
            try {
                li.next().apply();
            } catch (RuntimeException re) {
                // Nothing to do
            }
        }
    }

    protected void fireCancel() {
        setVisible(false);
        if (changed) {
            setChanged(false);
            ListIterator<OptionsListener> li = listeners.listIterator();
            while (li.hasNext()) {
                try {
                    li.next().cancel();
                } catch (RuntimeException re) {
                    // Nothing to do
                }
            }
        }
    }

    public void setContent(JComponent c) {
        if (content != null) {
            remove(content);
        }
        if (c != null) {
            add(c, CC.xy(2, 2));
        }
        content = c;
    }

    public void setOk(boolean o) {
        isOk = o;
        updateButtons();
    }

    public synchronized void setChanged(boolean changed) {
        this.changed = changed;
        updateButtons();
    }

    private void updateButtons() {
        boolean o = changed && isOk;
        ok.setEnabled(o);
        apply.setEnabled(o);
        cancel.setText((changed ? JUtilsI18n.get("Cancel") : JUtilsI18n.get("Close")));
        cancel.setIcon((changed ? ib.getSmallIcon("cancel") : ib.getSmallIcon("close")));
    }

    public void addOptionsListener(OptionsListener listener) {
        listeners.addLast(listener);
    }

    public void removeOptionsListener(OptionsListener listener) {
        listeners.remove(listener);
    }

    public static interface OptionsListener {
        void apply();

        void cancel();
    }
}