/*
 * Created on 19.04.2005
 */
package de.df.jutils.i18n;

import java.io.File;
import java.util.Locale;

public final class I18nUtils {

    private I18nUtils() {
        // Hide
    }

    public static File getLocalizedFile(String name) {
        int lastDot = name.lastIndexOf(".");
        int lastSeparator = Math.max(name.lastIndexOf(File.separator), name.lastIndexOf("/"));
        String prefix;
        String suffix;
        if (lastDot <= lastSeparator) {
            prefix = name;
            suffix = "";
        } else {
            prefix = name.substring(0, lastDot);
            suffix = name.substring(lastDot);
        }

        Locale locale = Locale.getDefault();
        String country = locale.getCountry().toLowerCase();
        String variant = locale.getVariant().toLowerCase();

        File result = new File(prefix + "_" + country + "_" + variant + suffix);
        if (result.exists()) {
            return result;
        }
        result = new File(prefix + "_" + country + suffix);
        if (result.exists()) {
            return result;
        }
        result = new File(prefix + suffix);
        if (result.exists()) {
            return result;
        }
        return null;
    }
}