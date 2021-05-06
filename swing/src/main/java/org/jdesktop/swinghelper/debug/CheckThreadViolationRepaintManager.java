package org.jdesktop.swinghelper.debug;

import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * <p>
 * This class is used to detect Event Dispatch Thread rule violations<br>
 * See <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
 * to Use Threads</a> for more info
 * </p>
 * <p>
 * This is a modification of original idea of Scott Delap<br>
 * Initial version of ThreadCheckingRepaintManager can be found here<br>
 * <a href="http://www.clientjava.com/blog/2004/08/20/1093059428000.html">Easily
 * Find Swing Threading Mistakes</a>
 * </p>
 * 
 * @author Scott Delap some improvements from Alexander Potochkin
 */
public class CheckThreadViolationRepaintManager extends RepaintManager {
    // it is recommended to pass the complete check
    private boolean                   completeCheck = false;
    private WeakReference<JComponent> lastComponent;

    public boolean isCompleteCheck() {
        return completeCheck;
    }

    public void setCompleteCheck(boolean completeCheck) {
        this.completeCheck = completeCheck;
    }

    @Override
    public synchronized void addInvalidComponent(JComponent component) {
        checkThreadViolations(component);
        super.addInvalidComponent(component);
    }

    @Override
    public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
        checkThreadViolations(component);
        super.addDirtyRegion(component, x, y, w, h);
    }

    private void checkThreadViolations(JComponent c) {
        if (!SwingUtilities.isEventDispatchThread() && (completeCheck || c.isShowing())) {
            boolean repaint = false;
            boolean fromSwing = false;
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement st : stackTrace) {
                if (repaint && st.getClassName().startsWith("javax.swing.")) {
                    fromSwing = true;
                }
                if ("repaint".equals(st.getMethodName())) {
                    repaint = true;
                }
            }
            if (repaint && !fromSwing) {
                // no problems here, since repaint() is thread safe
                return;
            }
            // ignore the last processed component
            if (lastComponent != null && c == lastComponent.get()) {
                return;
            }
            lastComponent = new WeakReference<JComponent>(c);
            System.err.println();
            System.err.println("EDT violation detected");
            System.err.println(c);
            for (StackTraceElement st : stackTrace) {
                System.err.println("\tat " + st);
            }
        }
    }

    public static void init() {
        // set CheckThreadViolationRepaintManager
        RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
    }
}