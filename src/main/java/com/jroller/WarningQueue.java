package com.jroller;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @link http://jroller.com/page/tackline?entry=detecting_invokeandwait_abuse
 * @since 20.01.2006
 * @author Tom Hawtin
 */
public class WarningQueue extends java.awt.EventQueue {

    /** The top few frames will just be us, and so are not of interest. */
    private static final int          IGNORED_FRAMES = 5;
    private static final ThreadMXBean threadBean     = ManagementFactory.getThreadMXBean();

    public static void install() {
        java.awt.EventQueue oldQueue = java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue();
        oldQueue.push(new WarningQueue());
    }

    @Override
    public void postEvent(java.awt.AWTEvent event) {
        if (event instanceof java.awt.event.InvocationEvent) {
            check();
        }
        super.postEvent(event);
    }

    private static void check() {
        long id = Thread.currentThread().getId();
        // Request sufficient stack frames to find invokeAndWait.
        ThreadInfo thread = threadBean.getThreadInfo(id, 10);
        for (StackTraceElement trace : thread.getStackTrace()) {
            if (isInvokeAndWait(trace)) {
                check(id);
                return;
            }
        }
    }

    private static void check(long id) {
        ThreadInfo thread = threadBean.getThreadInfo(new long[] { id }, true, true)[0];
        StringBuilder buff = new StringBuilder();
        for (MonitorInfo lock : thread.getLockedMonitors()) {
            if (!isInvokeAndWait(lock.getLockedStackFrame())) {
                buff.append("  monitor: ").append(lock.getClassName()).append("@").append(Integer.toHexString(lock.getIdentityHashCode())).append(" at ")
                        .append(lock.getLockedStackFrame()).append(" [").append(lock.getLockedStackDepth() - IGNORED_FRAMES).append("]\n");
            }
        }
        for (LockInfo lock : thread.getLockedSynchronizers()) {
            buff.append("  lock:").append(lock.getClassName()).append("@").append(Integer.toHexString(lock.getIdentityHashCode())).append("\n");
        }
        if (buff.length() != 0) {
            buff.insert(0, "EventQueue.invokeAndWait called with locks held:\n");
            // Append stack trace, but not top few frames that are ours.
            StackTraceElement[] traces = thread.getStackTrace();
            final int num = traces.length;
            for (int ct = IGNORED_FRAMES; ct < num; ++ct) {
                buff.append("    ").append(traces[ct]).append("\n");
            }
            System.err.println(buff);
        }
    }

    private static boolean isInvokeAndWait(StackTraceElement trace) {
        return "java.awt.EventQueue".equals(trace.getClassName()) && "invokeAndWait".equals(trace.getMethodName());
    }
}