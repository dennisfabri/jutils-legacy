package de.df.jutils.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class ArrayUtils {
    public static boolean isInArray(byte element, byte[] array) {
        for (byte anArray : array) {
            if (anArray == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates an array of Integers with amount values starting with value
     * offset and increasing by 1.
     */
    public static Integer[] createIntegerArray(int amount, int offset) {
        Integer[] result = new Integer[amount];
        for (int x = 0; x < amount; x++) {
            result[x] = offset + x;
        }
        return result;
    }

    public static String[] toArray(Enumeration<String> keys) {
        return Collections.list(keys).toArray(new String[0]);
    }

    public static ArrayList<String> toList(Enumeration<String> keys) {
        return Collections.list(keys);
    }
}
