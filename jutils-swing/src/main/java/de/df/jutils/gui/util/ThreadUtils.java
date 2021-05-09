/*
 * Created on 24.01.2006
 */
package de.df.jutils.gui.util;

public final class ThreadUtils {

    private ThreadUtils() {
        // Hide
    }

    public static boolean isRecursion() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        int x = 0;
        while ((!elements[x].getClassName().equals(ThreadUtils.class.getName())) || (!elements[x].getMethodName().equals("isRecursion"))) {
            x++;
        }
        x++;
        String klasse = elements[x].getClassName();
        String method = elements[x].getMethodName();
        x++;

        for (; x < elements.length; x++) {
            if ((elements[x].getClassName().equals(klasse)) && (elements[x].getMethodName().equals(method))) {
                return true;
            }
        }
        return false;
    }
}
