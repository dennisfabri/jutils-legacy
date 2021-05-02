/*
 * Created on 01.11.2003
 */
package de.df.jutils.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;

// :extssh:maverix@cvs.sourceforge.net:/cvsroot/jaus

/**
 * @author Dennis Mueller
 */
public final class Converter {

    public static final String ABCDEF   = "ABCDEF";
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private Converter() {
        // Never used
    }

    /**
     * Wandelt einen String in eine int um, dabei werden stoerende Zeichen
     * ignoriert.
     * 
     * @param s
     *            Der String, der die Zahl enthaelt
     * @return Die Zahl als int.
     */
    public static int integer(final String s) {
        // Diese Methode entfernt saemtliche falschen Zeichen
        // und wandelt den Rest dann in eine ganze Zahl um.

        StringBuffer ergebnis = new StringBuffer("");
        // hier ist am Ende das Ergebnis
        int wert = 0; // hier wird am Ende der Wert gespeichert

        for (int zahl = 0; zahl < s.length(); zahl++) { // Alle Zeichen
            // durchgehen
            char c = s.charAt(zahl); // Zeichen holen

            if (Character.isDigit(c)) { // Ist c eine Zahl?
                ergebnis.append(c); // Dann anhaengen
            }
        }
        try { // Ergebnis in Zahl umwandeln
            wert = Integer.valueOf(ergebnis.toString());
        } catch (NumberFormatException nfe) {
            // kann eigentlich nicht vorkommen, aber zur Sicherheit
            wert = 0;
        }
        return (wert); // Ergebnis zurueckgeben
    }

    public static double getDouble(final String value) throws ParseException {
        DecimalFormat df = new DecimalFormat();
        ParsePosition pp = new ParsePosition(0);
        Number number = df.parse(value, pp);
        if (pp.getErrorIndex() > -1) {
            throw new ParseException("Cannot parse " + value + " at " + pp.getIndex(), pp.getErrorIndex());
        }
        return number.doubleValue();
    }

    public static int getInteger(final String value) throws ParseException {
        DecimalFormat df = new DecimalFormat();
        df.setParseIntegerOnly(true);
        ParsePosition pp = new ParsePosition(0);
        Number number = df.parse(value, pp);
        if (pp.getErrorIndex() > -1) {
            throw new ParseException("Cannot parse " + value + " at " + pp.getIndex(), pp.getErrorIndex());
        }
        return number.intValue();
    }

    public static int hexToInt(final String hex) {
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