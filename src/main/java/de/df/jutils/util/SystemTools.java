/*
 * Created on 04.11.2003
 */
package de.df.jutils.util;

import java.io.File;

/**
 * @author Dennis Mueller
 */
public final class SystemTools {

    private SystemTools() {
        // Never used
    }

    public static String getSubdir(final String parent, final String dir) {
        String result = parent;
        if (!parent.endsWith(File.separator)) {
            result += File.separator;
        }
        if (dir.startsWith(File.separator)) {
            result += dir.substring(1);
        } else {
            result += dir;
        }
        return result;
    }

    public static String getJavaSubdir(final String dir) {
        return getSubdir(System.getProperty("java.home"), dir);
    }

    public static String getUserSubdir(final String dir) {
        return getSubdir(System.getProperty("user.home"), dir);
    }
}
