package de.df.jutils.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
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

    private static final String[] UNITS = new String[] { "B", "KB", "MB", "GB", "TB" };

    public static String sizeToString(long size) {
        long start = 1;
        int count = 0;
        while ((size > start * 10 * 1024) && (count < 4)) {
            start *= 1024;
            count++;
        }
        size /= start;
        StringBuilder sb = new StringBuilder();
        sb.append(size);
        sb.append(UNITS[count]);
        return sb.toString();
    }

    public static String toHtml(String text) {
        String[][] tokens = new String[][] { { "&", "&amp;" }, { "\u00e4", "&auml;" }, { "\u00f6", "&ouml;" },
                { "\u00fc", "&uuml;" }, { "\u00c4", "&Auml;" },
                { "\u00d6", "&Ouml;" }, { "\u00dc", "&Uuml;" }, { "\u00df", "&szlig;" }, { "<", "&lt;" },
                { ">", "&gt;" }

        };
        for (String[] token : tokens) {
            text = text.replace(token[0], token[1]);
        }
        return text;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
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

    private static int hexToByte(char c) {
        switch (c) {
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
        default:
            return -1;
        }
    }

    private static char toHex(byte i) {
        switch (i) {
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
        default:
            return ' ';
        }
    }

    public static final char[] ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String asText(int i) {
        StringBuilder sb = new StringBuilder();
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

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Erzeugt aus einer Zahl den entsprechenden Buchstaben des Alphabets.
     * 
     * @param zahl Nummer des Buchstaben im Alphabet
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

    private static final char[] CRC = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();

    public static String asCode(long i) {
        StringBuilder sb = new StringBuilder();
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
        StringBuilder result = new StringBuilder();
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
        StringBuilder result = new StringBuilder();
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

    public static String removeAll(String s, char c) {
        if (s == null) {
            return null;
        }
        char[] chars = s.toCharArray();
        StringBuilder neu = new StringBuilder();
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

    static int skip(final char[] chars, final int pos, final char marker, final boolean after) {
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
        LinkedList<String> result = new LinkedList<>();
        while (s.contains(sep)) {
            int index = s.indexOf(sep);
            result.addLast(s.substring(0, index));
            s = s.substring(index + 1);
        }
        result.addLast(s);
        return result.toArray(new String[result.size()]);
    }

    static String concatFileName(final String dir, final String file) {
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
        StringBuilder sb = new StringBuilder();
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

    static String concatFileName(final String dir1, final String dir2, final String file) {
        String temp = concatFileName(dir1, dir2);
        return concatFileName(temp, file);
    }

    public static String concat(String separator, String... s) {
        if (s.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s[0]);
        for (int x = 1; x < s.length; x++) {
            sb.append(separator);
            sb.append(s[x]);
        }
        return sb.toString();
    }

    /**
     * Wandelt den in Sekunden gegebenen double in einen String vom Format mm:ss,ss
     * um.
     * 
     * @param zeit Enth\u00e4\u00fc00e4lt die Zeit in Hundertstel-Sekunden. @return
     *             Liefert die Zeit im \u00fcblichen Format.
     */
    public static String zeitString(final long zeit) {
        return zeitString(zeit, ',');
    }

    public static String zeitString(final long zeit, final char sep) {
        long min = zeit / 6000;
        int sec = (int) ((zeit % 6000) / 100);
        int ten = (int) (zeit % 100);
        StringBuilder s = new StringBuilder();
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
     * Diese Methode wandelt eine Zahl (double) in die f\u00fcr Punkte korrekte
     * Darstellen (2 Nachkommastellen) um.
     * 
     * @param punkte Die Punkte die formatiert werden sollen @return Liefert einen
     *               korrekt formatierten String f\u00fcr Punkte zur\u00fcck.
     */
    public static String punkteString(final double punkte) {
        return punkteString(punkte, true);
    }

    public static String punkteString(final double punkte, boolean nachkomma) {
        int punkteHundert = (int) Math.round(punkte * 100);
        int punkteGanz = punkteHundert / 100;
        if (nachkomma) {
            punkteHundert -= punkteGanz * 100;
            String s = "" + punkteGanz + ',';
            if (punkteHundert < 10) {
                s += '0';
            }
            s += punkteHundert;
            return s;
        }
        return "" + Math.round(punkte);
    }

    public static String[] separateCsvLine(String data, char separator) {
        LinkedList<String> result = new LinkedList<>();
        char[] chars = data.toCharArray();
        int x = 0;
        while (x < chars.length) {
            if (chars[x] == '"') {
                // Step over quote
                x++;
                StringBuilder entry = new StringBuilder();
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
                StringBuilder entry = new StringBuilder();
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

    public static void fill(StringBuilder sb, int length) {
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

        StringBuilder sb = new StringBuilder();
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

    public static String crc(String pre) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        PrintStream ps;
        try {
            ps = new PrintStream(bos, true, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            ps = new PrintStream(bos);
        }
        ps.print(pre);
        CRC32 crc = new CRC32();
        crc.update(bos.toByteArray());
        return StringTools.asCode(crc.getValue());
    }
}
