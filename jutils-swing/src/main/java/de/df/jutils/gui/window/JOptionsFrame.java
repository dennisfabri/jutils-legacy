/*
 * Created on 02.05.2005
 */
package de.df.jutils.gui.window;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.util.AIconBundle;
import de.df.jutils.i18n.util.JUtilsI18n;

public class JOptionsFrame extends JFrame {

    private static final long serialVersionUID = 3256728398460891959L;

    private JButton cancel;
    private JButton ok;
    private JButton apply;
    private boolean changed;
    private boolean isOk;
    private JComponent content;
    private LinkedList<OptionsListener> listeners;
    private AIconBundle ib;

    private final JFrame parent;

    public JOptionsFrame(JFrame parent, String title, JComponent c, AIconBundle ib) {
        this(parent, title, ib);
        setContent(c);
    }

    public JOptionsFrame(JFrame parent, String title, AIconBundle ib) {
        super(title);
        this.ib = ib;
        content = null;
        changed = false;
        isOk = true;
        listeners = new LinkedList<>();

        this.parent = parent;

        initUI();
        initListeners();
        addActions();
        setChanged(false);
    }

    @Override
    public void setVisible(boolean b) {
        parent.setEnabled(!b);
        super.setVisible(b);
    }

    private void initUI() {
        cancel = new JButton(JUtilsI18n.get("Close"), ib.getSmallIcon("close"));
        ok = new JButton(JUtilsI18n.get("Ok"), ib.getSmallIcon("ok"));
        apply = new JButton(JUtilsI18n.get("Apply"), ib.getSmallIcon("apply"));

        FormLayout layout = new FormLayout("4dlu,0px:grow,fill:default,4dlu,fill:default,4dlu,fill:default,4dlu",
                "4dlu,fill:default:grow,4dlu,fill:default,4dlu");
        layout.setColumnGroups(new int[][] { { 3, 5, 7 } });
        setLayout(layout);
        add(ok, CC.xy(3, 4));
        add(apply, CC.xy(5, 4));
        add(cancel, CC.xy(7, 4));
    }

    private void initListeners() {
        ok.addActionListener(arg0 -> {
            setVisible(false);
            fireApply();
        });
        apply.addActionListener(arg0 -> {
            fireApply();
        });
        cancel.addActionListener(arg0 -> {
            fireCancel();
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
            add(c, CC.xyw(2, 2, 6));
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
