package de.df.jutils.util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.*;
import java.util.zip.CRC32;

public final class StringTools {

    private StringTools() {
        // Never used
    }

    public static String md5(String text) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        PrintStream ps = new PrintStream(bos);
        ps.print(text);

        MessageDigest complete;
        try {
            complete = MessageDigest.getInstance("MD5");
            complete.update(bos.toByteArray());
            byte[] md5 = complete.digest();
            return StringTools.bytesToHex(md5);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String shorten(String text, int length) {
        return shorten(text, length, "");
    }

    public static String shorten(String text, int length, String suffix) {
        if (text == null) {
            return null;
        }
        if (length <= 0) {
            return "";
        }
        if (text.length() <= length) {
            return text;
        }
        if (suffix == null) {
            suffix = "";
        }
        if (length - suffix.length() <= 0) {
            return suffix;
        }
        return text.substring(0, length - suffix.length()) + suffix;
    }

    public static String firstLine(String ort) {
        if (ort != null) {
            int x1 = ort.indexOf('\n');
            int x2 = ort.indexOf('\r');
            if (x1 == -1) {
                x1 = x2;
            }
            if (x2 > -1) {
                ort = ort.substring(0, Math.min(x1, x2));
            } else {
                if (x1 > -1) {
                    ort = ort.substring(0, x1);
                }
            }
        }
        return ort;
    }

    public static final String[] UNITS = new String[] { "B", "KB", "MB", "GB", "TB" };

    public static String sizeToString(long size) {
        long start = 1;
        int count = 0;
        while ((size > start * 10 * 1024) && (count < 4)) {
            start *= 1024;
            count++;
        }
        size /= start;
        StringBuffer sb = new StringBuffer();
        sb.append(size);
        sb.append(UNITS[count]);
        return sb.toString();
    }

    public static String toHtml(String text) {
        String[][] tokens = new String[][] { { "&", "&amp;" }, { "\u00e4", "&auml;" }, { "\u00f6", "&ouml;" }, { "\u00fc", "&uuml;" }, { "\u00c4", "&Auml;" },
                { "\u00d6", "&Ouml;" }, { "\u00dc", "&Uuml;" }, { "\u00df", "&szlig;" }, { "<", "&lt;" }, { ">", "&gt;" }

        };
        for (String[] token : tokens) {
            text = text.replace(token[0], token[1]);
        }
        return text;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte mybyte : bytes) {
            int i = mybyte;
            if (i < 0) {
                i = 256 + i;
            }
            int a = i % 16;
            int b = i / 16;
            sb.append(toHex((byte) b));
            sb.append(toHex((byte) a));
        }
        return sb.toString();
    }

    public static String bytesToHex2(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < bytes.length; x++) {
            int i = bytes[x];
            if (i < 0) {
                i = 256 + i;
            }
            int a = i % 16;
            int b = i / 16;
            sb.append(toHex((byte) b));
            sb.append(toHex((byte) a));
            if (x + 1 < bytes.length) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String bytesAsChar(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < bytes.length; x++) {
            char c = (char) bytes[x];
            sb.append(c);
            if (x + 1 < bytes.length) {
                sb.append("");
            }
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String s) {
        byte[] bytes = new byte[s.length() / 2];
        for (int x = 0; x < bytes.length; x++) {
            int a = hexToByte(s.charAt(x * 2));
            int b = hexToByte(s.charAt(x * 2 + 1));

            int i = a * 16 + b;
            if (b > 127) {
                i = -256 + i;
            }
            bytes[x] = (byte) i;
        }
        return bytes;
    }

    public static String byteToHex(byte b) {
        int value = b;
        if (value < 0) {
            value += 256;
        }
        return "" + toHex((byte) (value / 16)) + toHex((byte) (value % 16));
    }

    public static int hexToByte(char c) {
        switch (c) {
        default:
            return -1;
        case '0':
            return 0;
        case '1':
            return 1;
        case '2':
            return 2;
        case '3':
            return 3;
        case '4':
            return 4;
        case '5':
            return 5;
        case '6':
            return 6;
        case '7':
            return 7;
        case '8':
            return 8;
        case '9':
            return 9;
        case 'a':
            return 10;
        case 'b':
            return 11;
        case 'c':
            return 12;
        case 'd':
            return 13;
        case 'e':
            return 14;
        case 'f':
            return 15;
        }
    }

    public static String toHexText(int integer) {
        StringBuilder sb = new StringBuilder();
        long value = integer;
        if (value < 0) {
            long amount = ((long) Integer.MAX_VALUE) * 2 + 2;
            value = amount + value;
        }
        while (value > 0) {
            byte b = (byte) (value % 16);
            value = value / 16;
            sb.insert(0, b);
        }
        while (sb.length() < 8) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    public static char toHex(byte i) {
        switch (i) {
        default:
            return ' ';
        case 0:
            return '0';
        case 1:
            return '1';
        case 2:
            return '2';
        case 3:
            return '3';
        case 4:
            return '4';
        case 5:
            return '5';
        case 6:
            return '6';
        case 7:
            return '7';
        case 8:
            return '8';
        case 9:
            return '9';
        case 10:
            return 'a';
        case 11:
            return 'b';
        case 12:
            return 'c';
        case 13:
            return 'd';
        case 14:
            return 'e';
        case 15:
            return 'f';
        }
    }

    public static final char[] ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String asText(int i) {
        StringBuffer sb = new StringBuffer();
        if (i < ABC.length) {
            return "" + ABC[i];
        }

        while (i > 0) {
            int a = i % ABC.length;
            i = i / ABC.length;
            sb.insert(0, ABC[a]);
        }
        return sb.toString();
    }

    public static final char[] CRC = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();

    public static String asCode(long i) {
        StringBuffer sb = new StringBuffer();
        if (i == 0) {
            return "" + CRC[0];
        }

        while (i > 0) {
            int a = (int) (i % CRC.length);
            i = i / CRC.length;
            sb.insert(0, CRC[a]);
        }
        return sb.toString();
    }

    public static String getCellName(String sheet, int row, int column) {
        StringBuffer result = new StringBuffer();
        if (sheet != null) {
            result.append(sheet);
            result.append(' ');
        }
        if ((row < 0) || (column < 0)) {
            return result.toString();
        }
        if (column < 26) {
            result.append(ABC[column]);
        } else {
            if (column < 26 * 26) {
                int m = column / 26;
                result.append(ABC[m - 1]);
                result.append(ABC[column % 26]);
            } else {
                column++;
                result.append(column);
                result.append(":");
            }
        }
        row++;
        result.append(row);
        return result.toString();
    }

    public static String getRowName(String sheet, int row) {
        return getRowName(sheet, row, -1);
    }

    public static String getRowName(String sheet, int row, int column) {
        StringBuffer result = new StringBuffer();
        if (sheet != null) {
            result.append(sheet);
            result.append(' ');
        }
        if (row < 0) {
            return result.toString();
        }
        row++;
        result.append(row);
        return result.toString();
    }

    public static String removeFirst(String s, char x) {
        int index = s.indexOf(x);
        if (index < 0) {
            return s;
        }
        StringBuffer neu = new StringBuffer();
        if (index > 0) {
            neu.append(s.substring(0, index));
        }
        if (index < s.length() - 1) {
            neu.append(s.substring(index + 1));
        }
        return neu.toString();
    }

    public static int count(String s, char c) {
        char[] chars = s.toCharArray();
        int counter = 0;
        for (char aChar : chars) {
            if (aChar == c) {
                counter++;
            }
        }
        return counter;
    }

    public static String removeAll(String s, char c) {
        if (s == null) {
            return null;
        }
        char[] chars = s.toCharArray();
        StringBuffer neu = new StringBuffer();
        for (char aChar : chars) {
            if (aChar != c) {
                neu.append(aChar);
            }
        }
        return neu.toString();

    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (RuntimeException re) {
            return false;
        }
    }

    public static int skip(final char[] chars, final int pos, final char marker, final boolean after) {
        int position = pos;
        while (position < chars.length) {
            if (chars[position] == marker) {
                if (after) {
                    position++;
                }
                return position;
            }
            position++;
        }
        return chars.length;
    }

    public static String capitalize(final String s) {
        char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String[] split(String s, String sep) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (sep == null) {
            throw new NullPointerException();
        }
        LinkedList<String> result = new LinkedList<String>();
        while (s.indexOf(sep) > -1) {
            int index = s.indexOf(sep);
            result.addLast(s.substring(0, index));
            s = s.substring(index + 1);
        }
        result.addLast(s);
        return result.toArray(new String[result.size()]);
    }

    public static String uncapitalize(final String s) {
        char[] chars = s.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private static final String DOT = ".";

    public static String[] separatePackages(final String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (name.length() == 0) {
            return EMPTY_STRINGS;
        }
        if (name.endsWith(DOT)) {
            throw new IllegalArgumentException();
        }
        String temp = name;
        LinkedList<String> result = new LinkedList<String>();
        String segment;
        for (; temp.indexOf(DOT) > -1; result.addLast(segment)) {
            segment = temp.substring(0, temp.indexOf(DOT));
            temp = temp.substring(temp.indexOf(DOT) + 1);
        }

        result.addLast(temp);
        return result.toArray(new String[result.size()]);
    }

    public static String concatPackages(final String p1, final String p2) {
        if (p1 == null && p2 == null) {
            throw new NullPointerException();
        }
        if (p1 == null || p1.length() == 0) {
            if (p2 == null || p2.length() == 0) {
                return "";
            }
            return p2;
        }
        if (p2 == null || p2.length() == 0) {
            return p1;
        }
        return p1 + DOT + p2;
    }

    public static String concatFileName(final String dir, final String file) {
        if (dir == null || file == null) {
            throw new NullPointerException();
        }
        int count = 0;
        String fs = File.separator;
        if (dir.endsWith(fs)) {
            count++;
        }
        if (file.startsWith(fs)) {
            count++;
        }
        switch (count) {
        case 0:
            return dir + fs + file;
        case 1:
            return dir + file;
        case 2:
        default:
            return dir + file.substring(fs.length());
        }
    }

    public static String concatenateFilenames(String[] names) {
        if (names == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if (names.length > 0) {
            sb.append(names[0]);
            for (int x = 1; x < names.length; x++) {
                if (names[x].length() > 0) {
                    sb.append(";");
                    sb.append(names[x]);
                }
            }
        }
        return sb.toString();
    }

    public static String concatFileName(final String dir1, final String dir2, final String file) {
        String temp = concatFileName(dir1, dir2);
        return concatFileName(temp, file);
    }

    public static String concat(final String s1, final String s2, final String s3) {
        if (s1 == null && s2 == null) {
            if (s3 == null) {
                throw new NullPointerException();
            }
            return s3;
        }
        return concatPackages(concatPackages(s1, s2), s3);
    }

    public static String concat(String separator, String... s) {
        if (s.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(s[0]);
        for (int x = 1; x < s.length; x++) {
            sb.append(separator);
            sb.append(s[x]);
        }
        return sb.toString();
    }

    public static String lastPart(final String s) {
        int i = s.lastIndexOf('.');
        return s.substring(i + 1);
    }

    /**
     * Wandelt den in Sekunden gegebenen double in einen String vom Format mm:ss,ss um.
     * 
     * @param zeit
     *            Enth\u00e4\u00fc00e4lt die Zeit in Hundertstel-Sekunden. @return Liefert die Zeit im \u00fcblichen
     *            Format.
     */
    public static String zeitString(final long zeit) {
        return zeitString(zeit, ',');
    }

    public static String zeitString(final long zeit, final char sep) {
        long min = zeit / 6000;
        int sec = (int) ((zeit % 6000) / 100);
        int ten = (int) (zeit % 100);
        StringBuffer s = new StringBuffer();
        s.append(min);
        s.append(':');
        if (sec < 10) {
            s.append('0');
        }
        s.append(sec);
        s.append(sep);
        if (ten < 10) {
            s.append('0');
        }
        s.append(ten);
        return s.toString();
    }

    /**
     * Diese Methode wandelt eine Zahl (double) in die f\u00fcr Punkte korrekte Darstellen (2 Nachkommastellen) um.
     * 
     * @param punkte
     *            Die Punkte die formatiert werden sollen @return Liefert einen korrekt formatierten String f\u00fcr
     *            Punkte zur\u00fcck.
     */
    public static String punkteString(final double punkte) {
        return punkteString(punkte, true);
    }

    public static String punkteString(final double punkte, boolean nachkomma) {
        int punkteHundert = (int) Math.round(punkte * 100);
        int punkteGanz = punkteHundert / 100;
        if (nachkomma) {
            punkteHundert -= (punkteGanz * 100);
            String s = "" + punkteGanz + ',';
            if (punkteHundert < 10) {
                s += '0';
            }
            s += punkteHundert;
            return s;
        }
        return "" + Math.round(punkte);
    }

    /**
     * Erzeugt einen String, der die Zahl zahl enth\u00e4lt und insgesamt mindestens die L\u00e4nge laenge hat. Dabei
     * werden links Leerzeichen hinzugef\u00fcgt, bis die gew\u00fcnschte L\u00e4nge erreicht ist.
     * 
     * @param zahl
     *            Die umzuwandelnde Zahl @param laenge Die gew\u00fcnschte L\u00e4nge @return Die Zahl als String
     */
    public static String valueString(final int z, final int laenge) {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        return valueString(nf.format(z), laenge);
    }

    public static String valueString(final double z, final int laenge) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return valueString(nf.format(z), laenge);
    }

    /**
     * Erzeugt einen String, der die Zahl zahl enth\u00e4lt und insgesamt mindestens die L\u00e4nge laenge hat. Dabei
     * werden links Leerzeichen hinzugef\u00fcgt, bis die gew\u00fcnschte L\u00e4nge erreicht ist.
     * 
     * @param zahl
     *            Die umzuwandelnde Zahl @param laenge Die gew\u00fcnschte L\u00e4nge @return Die Zahl als String
     */
    public static String valueString(String zahl, final int laenge) {
        while (zahl.length() < laenge) {
            zahl = ' ' + zahl;
        }
        return zahl;
    }

    public static String fillWith(String zahl, char c, final int laenge) {
        StringBuffer sb = new StringBuffer(zahl);
        while (zahl.length() < laenge) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static final String[] EMPTY_STRINGS = { "" };

    public static String[] separateCsvLine(String data, char separator) {
        LinkedList<String> result = new LinkedList<String>();
        char[] chars = data.toCharArray();
        int x = 0;
        while (x < chars.length) {
            if (chars[x] == '"') {
                // Step over quote
                x++;
                StringBuffer entry = new StringBuffer();
                while ((x < chars.length) && (chars[x] != '"')) {
                    entry.append(chars[x]);
                    x++;
                }
                result.addLast(entry.toString().trim());
                // Step over quote
                x++;
                while ((x < chars.length) && (chars[x] != separator)) {
                    x++;
                }
            } else {
                StringBuffer entry = new StringBuffer();
                while ((x < chars.length) && (chars[x] != separator)) {
                    entry.append(chars[x]);
                    x++;
                }
                result.addLast(entry.toString().trim());
            }

            // Jump over separator
            x++;
        }

        return result.toArray(new String[result.size()]);
    }

    public static int countCsvColumns(String text, char s) {
        return separateCsvLine(text, s).length;
    }

    public static String replaceCaseInsensitive(String s, String id, String value) {
        String s1 = s.toLowerCase();
        String id1 = id.toLowerCase();

        int x = s1.indexOf(id1);

        while (x >= 0) {
            String t1 = s.substring(0, x);
            String t2 = s.substring(x + id.length());

            s = t1 + value + t2;
            s1 = s.toLowerCase();

            x = s1.indexOf(id1, x + value.length());
        }
        return s;
    }

    public static void fill(StringBuffer sb, int length) {
        for (int i = 0; i < length; i++) {
            sb.append(" ");
        }
    }

    public static String getCompactDateTimeString() {
        GregorianCalendar gc = new GregorianCalendar();
        int year = gc.get(Calendar.YEAR);
        int month = gc.get(Calendar.MONTH) + 1;
        int day = gc.get(Calendar.DAY_OF_MONTH);

        int hour = gc.get(Calendar.HOUR_OF_DAY);
        int min = gc.get(Calendar.MINUTE);

        StringBuffer sb = new StringBuffer();
        sb.append(year);
        if (month < 10) {
            sb.append("0");
        }
        sb.append(month);
        if (day < 10) {
            sb.append("0");
        }
        sb.append(day);
        sb.append("-");
        if (hour < 10) {
            sb.append("0");
        }
        sb.append(hour);
        if (min < 10) {
            sb.append("0");
        }
        sb.append(min);

        return sb.toString();
    }

    public static String CRC(String pre) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        PrintStream ps;
        try {
            ps = new PrintStream(bos, true, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            ps = new PrintStream(bos);
        }
        // ps = new PrintStream(bos);
        ps.print(pre);
        CRC32 crc = new CRC32();
        crc.update(bos.toByteArray());
        return StringTools.asCode(crc.getValue());
    }

    public static String[] Separate(String text, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be higher than 0 but was " + amount);
        }
        if (text == null) {
            throw new IllegalArgumentException("text must not be null.");
        }

        String[] parts = new String[amount];
        if (amount == 1) {
            parts[0] = text;
            return parts;
        }
        int size = text.length() / amount;
        int offset = 0;
        for (int x = 0; x < amount; x++) {
            int s = size + (x < text.length() % amount ? 1 : 0);
            parts[x] = text.substring(offset, offset + s);
            offset += s;
        }

        return parts;
    }
}