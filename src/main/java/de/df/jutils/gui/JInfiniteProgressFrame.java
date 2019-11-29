/*
 * Created on 23.02.2005
 */
package de.df.jutils.gui;

import java.awt.GraphicsConfiguration;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.java.swingfx.waitwithstyle.InfiniteProgressPanel;
import net.java.swingfx.waitwithstyle.InfiniteProgressUtils;

public class JInfiniteProgressFrame extends JFrame {

    private static final long serialVersionUID = 3257564027247736368L;

    private JComponent        glass;

    public JInfiniteProgressFrame() {
        super();
        init();
    }

    public JInfiniteProgressFrame(GraphicsConfiguration arg0) {
        super(arg0);
        init();
    }

    public JInfiniteProgressFrame(String arg0) {
        super(arg0);
        init();
    }

    public JInfiniteProgressFrame(String arg0, GraphicsConfiguration arg1) {
        super(arg0, arg1);
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
        // super.setEnabled(enabled);
        if (enabled) {
            stopProgress();
        } else {
            startProgress();
        }
    }

    @Override
    public boolean isEnabled() {
        return !glass.isVisible();
    }

    public void setAnimated(boolean a) {
        if (a != animated) {
            boolean r = running;

            stopProgress();

            if (a) {
                glass = InfiniteProgressUtils.createPanel();
            } else {
                glass = new JGlassPane();
            }
            setGlassPane(glass);

            if (r) {
                startProgress();
            }

            animated = a;
        }
    }

    private boolean animated = true;
    private boolean running  = false;

    public void stopProgress() {
        super.setEnabled(true);
        glass.setVisible(false);
        if (glass instanceof InfiniteProgressPanel) {
            ((InfiniteProgressPanel) glass).interrupt();
        }
        running = false;
    }

    public void startProgress() {
        boolean r = running;
        super.setEnabled(false);
        glass.setVisible(true);
        running = true;
        if (!r) {
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