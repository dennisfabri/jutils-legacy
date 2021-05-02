/*
 * Created on 12.01.2005
 */
package de.df.jutils.gui.window;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import de.df.jutils.gui.util.WindowUtils;

/**
 * @author Mueller
 */
public class JProgressDialog extends JDialog {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256719593694181424L;

    private long              delay            = 500;
    private boolean           finished         = true;
    private JProgressBar      progress         = new JProgressBar();

    public JProgressDialog(final JFrame parent, final String title) {
        super(parent, title, false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(progress);
        setResizable(false);
        pack();
        setSize(Math.max(200, getWidth()), getHeight());
        WindowUtils.center(this);
    }

    public void setDelay(final long d) {
        if (d < 0) {
            return;
        }
        delay = d;
    }

    public long getDelay() {
        return delay;
    }

    @Override
    public void setVisible(final boolean visible) {
        if (finished) {
            super.setVisible(false);
        } else {
            super.setVisible(visible);
        }
    }

    public synchronized void finish() {
        finished = true;
        setVisible(false);
    }

    public synchronized void start() {
        finished = false;
        Runner r = new Runner(this);
        r.start();
    }

    public void setMaximum(int m) {
        progress.setMaximum(m);
    }

    public void setValue(int v) {
        progress.setValue(v);
    }

    public void increase() {
        progress.setValue(progress.getValue() + 1);
    }

    private static class Runner extends Thread {

        private JProgressDialog parent = null;

        public Runner(JProgressDialog parent) {
            super(Runner.class.getName());
            this.parent = parent;
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(parent.getDelay());
            } catch (InterruptedException e) {
                // Nothing to do
            }
            synchronized (this) {
                if (!parent.isFinished()) {
                    parent.setVisible(true);
                }
            }
        }
    }

    /**
     * @return Returns the finished.
     */
    public boolean isFinished() {
        return finished;
    }
}