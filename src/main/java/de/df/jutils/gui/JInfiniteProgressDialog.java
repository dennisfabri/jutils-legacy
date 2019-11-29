/*
 * Created on 23.02.2005
 */
package de.df.jutils.gui;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JDialog;

import net.java.swingfx.waitwithstyle.InfiniteProgressPanel;
import net.java.swingfx.waitwithstyle.InfiniteProgressUtils;

public class JInfiniteProgressDialog extends JDialog {

    private static final long serialVersionUID = 4050486728878141494L;

    private JComponent        glass;

    public JInfiniteProgressDialog() {
        super();
        init();
    }

    public JInfiniteProgressDialog(Frame owner) {
        super(owner);
        init();
    }

    public JInfiniteProgressDialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
    }

    public JInfiniteProgressDialog(Frame owner, String title) {
        super(owner, title);
        init();
    }

    public JInfiniteProgressDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }

    public JInfiniteProgressDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gcfg) {
        super(owner, title, modal, gcfg);
        init();
    }

    public JInfiniteProgressDialog(Dialog owner) {
        super(owner);
        init();
    }

    public JInfiniteProgressDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        init();
    }

    public JInfiniteProgressDialog(Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public JInfiniteProgressDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }

    public JInfiniteProgressDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gcfg) {
        super(owner, title, modal, gcfg);
        init();
    }

    private void init() {
        glass = InfiniteProgressUtils.createPanel();
        setGlassPane(glass);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) {
            return;
        }
        super.setEnabled(enabled);
        if (enabled) {
            stopProgress();
        } else {
            startProgress();
        }
    }

    public void setAnimated(boolean a) {
        if (a != animated) {
            stopProgress();

            if (a) {
                glass = InfiniteProgressUtils.createPanel();
            } else {
                glass = new JGlassPane();
            }
            setGlassPane(glass);

            if (a) {
                startProgress();
            }

            animated = a;
        }
    }

    private boolean animated = true;
    private boolean running  = false;

    public synchronized void stopProgress() {
        glass.setVisible(false);
        if (glass instanceof InfiniteProgressPanel) {
            ((InfiniteProgressPanel) glass).interrupt();
        }
        running = false;
    }

    public synchronized void startProgress() {
        if (!running) {
            glass.setVisible(true);
            running = true;
            if (glass instanceof InfiniteProgressPanel) {
                ((InfiniteProgressPanel) glass).start();
            }
        }
    }

    public void setText(String text) {
        if (glass instanceof InfiniteProgressPanel) {
            ((InfiniteProgressPanel) glass).setText(text);
        }
    }
}