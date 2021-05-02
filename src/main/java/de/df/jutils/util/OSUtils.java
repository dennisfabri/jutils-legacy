package de.df.jutils.util;

import org.apache.commons.lang3.SystemUtils;

public final class OSUtils {

    private static final String VERSION = System.getProperty("os.version");

    public static boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    public static boolean isWindows2000() {
        return SystemUtils.IS_OS_WINDOWS_2000;
    }

    public static boolean isWindowsXP() {
        return SystemUtils.IS_OS_WINDOWS_XP;
    }

    public static boolean isWindowsVista() {
        return SystemUtils.IS_OS_WINDOWS_VISTA;
    }

    public static boolean isWindows7() {
        return SystemUtils.IS_OS_WINDOWS_7;
    }

    public static boolean isWindows8() {
        return SystemUtils.IS_OS_WINDOWS_8;
    }

    public static boolean isWindows10() {
        return SystemUtils.IS_OS_WINDOWS_10;
    }

    public static boolean isWindows8OrHigher() {
        return isWindows() && Double.valueOf(VERSION) >= 6.2;
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
