/*
 * Created on 01.11.2003
 */
package de.df.jutils.util;

// :extssh:maverix@cvs.sourceforge.net:/cvsroot/jaus

/**
 * @author Dennis Mueller
 */
public final class Converter {

    private static final String ABCDEF   = "ABCDEF";
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private Converter() {
        // Never used
    }

    static int hexToInt(final String hex) {
        if (hex == null) {
            throw new NullPointerException();
        }
        char[] chars = hex.toCharArray();
        int result = 0;
        for (char aChar : chars) {
            int value = 0;
            if (Character.isDigit(aChar)) {
                value = Integer.valueOf(String.valueOf(aChar));
            } else {
                int pos = ABCDEF.indexOf(String.valueOf(Character.toUpperCase(aChar)));
                if (pos == -1) {
                    throw new IllegalStateException();
                }
                value = 10 + pos;
            }
            result = (result * 16) + value;
        }
        return result;
    }

    /**
     * Erzeugt aus einer Zahl den entsprechenden Buchstaben des Alphabets.
     * 
     * @param zahl
     *            Nummer des Buchstaben im Alphabet
     * @return Der zahl-te Buchstabe im Alphabet
     */
    public static String characterString(final int zahl) {
        if (zahl < 1) {
            return "";
        }
        if (zahl > 26) {
            return "-" + zahl;
        }
        return String.valueOf(ALPHABET.charAt(zahl - 1));
    }
}