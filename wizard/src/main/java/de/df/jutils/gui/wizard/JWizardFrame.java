/*
 * Created on 15.04.2005
 */
package de.df.jutils.gui.wizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import de.df.jutils.gui.JInfiniteProgressFrame;
import de.df.jutils.gui.util.ModalFrameUtil;
import de.df.jutils.i18n.UIElementsProvider;

public class JWizardFrame extends JInfiniteProgressFrame {

    private static final long serialVersionUID = 3761970462556764212L;

    final JWizard             wizard;

    private final JFrame      parent;

    public JWizardFrame(JFrame parent, String title) {
        this(parent, title, null, false);
    }

    public JWizardFrame(JFrame parent, String title, UIElementsProvider provider, boolean autoclose) {
        super(title);
        wizard = new JWizard(provider);
        this.parent = parent;
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 3257572818995525944L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
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
                setVisible(false);
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

    public void start() {
        start(0, true);
    }

    public void start(int steps) {
        start(steps, true);
    }

    public void start(boolean modal) {
        start(0, modal);
    }

    public void start(int steps, boolean modal) {
        wizard.start(steps);
        if (modal) {
            ModalFrameUtil.showAsModal(this, parent);
        } else {
            setVisible(true);
        }
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