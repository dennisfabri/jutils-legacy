package de.df.jutils.util;

import org.apache.commons.lang3.SystemUtils;

public final class OSUtils {

    public static boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    public static boolean isLinux() {
        return SystemUtils.IS_OS_LINUX;
    }

    public static boolean isMacOSX() {
        return SystemUtils.IS_OS_MAC_OSX;
    }

    private OSUtils() {
        // Hide
    }

}
