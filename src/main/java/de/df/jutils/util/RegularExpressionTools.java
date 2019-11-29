/*
 * Created on 12.12.2003
 */
package de.df.jutils.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dennis
 */
public final class RegularExpressionTools {

    private RegularExpressionTools() {
        // Never used
    }

    public static boolean match(final String regex, final String match) {
        Pattern pat = Pattern.compile(regex);
        Matcher matcher = pat.matcher(match);
        return matcher.matches();
    }
}