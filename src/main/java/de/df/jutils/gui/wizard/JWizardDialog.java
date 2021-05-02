/*
 * Created on 15.04.2005
 */
package de.df.jutils.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import de.df.jutils.gui.JInfiniteProgressDialog;
import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.i18n.UIElementsProvider;

public class JWizardDialog extends JInfiniteProgressDialog {

    private static final long serialVersionUID = 4050206323331314483L;

    final JWizard             wizard;
    private final Window      parent;

    public JWizardDialog(JFrame parent, String title, JWizard wizard) {
        this(parent, title, wizard, false);
    }

    public JWizardDialog(JFrame parent, String title, JWizard wizard, boolean autoclose) {
        super(parent, title, parent != null);
        this.wizard = wizard;
        this.parent = parent;
        init(autoclose);
    }

    public JWizardDialog(JFrame parent, String title) {
        this(parent, title, (UIElementsProvider) null, false);
    }

    public JWizardDialog(JFrame parent, String title, UIElementsProvider provider, boolean autoclose) {
        super(parent, title, parent != null);
        wizard = new JWizard(provider);
        this.parent = parent;
        init(autoclose);
    }

    public JWizardDialog(JDialog parent, String title, JWizard wizard) {
        this(parent, title, wizard, false);
    }

    public JWizardDialog(JDialog parent, String title, JWizard wizard, boolean autoclose) {
        super(parent, title, parent != null);
        this.wizard = wizard;
        this.parent = parent;
        init(autoclose);
    }

    public JWizardDialog(JDialog parent, String title) {
        this(parent, title, (UIElementsProvider) null, false);
    }

    public JWizardDialog(JDialog parent, String title, UIElementsProvider provider, boolean autoclose) {
        super(parent, title, parent != null);
        wizard = new JWizard(provider);
        this.parent = parent;
        init(autoclose);
    }

    private void init(boolean autoclose) {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 3257572818995525944L;

            @Override
            public void actionPerformed(ActionEvent e) {
                wizard.notifyCancel();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        Action enterAction = new AbstractAction() {
            private static final long serialVersionUID = 3257572818995525934L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (wizard.next.isEnabled()) {
                    wizard.nextPage();
                } else {
                    if (wizard.finish.isEnabled()) {
                        wizard.notifyFinish();
                    }
                }
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, "ENTER");
        getRootPane().getActionMap().put("ENTER", enterAction);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                wizard.notifyCancel();
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(wizard, BorderLayout.CENTER);

        if (autoclose) {
            wizard.addListener(new Listener());
        }

        pack();
    }

    @Override
    public void setVisible(boolean enable) {
        if (parent != null) {
            parent.setEnabled(!enable);
        }
        super.setVisible(enable);
    }

    public boolean start() {
        wizard.start();
        EDTUtils.setVisible(this, true);
        return wizard.isFinished();
    }

    public void start(int steps) {
        wizard.start(steps);
        EDTUtils.setVisible(this, true);
    }

    public JWizard getWizard() {
        return wizard;
    }

    class Listener implements FinishListener, CancelListener {
        @Override
        public void finish() {
            setVisible(false);
        }

        @Override
        public void cancel() {
            setVisible(false);
        }
    }
}