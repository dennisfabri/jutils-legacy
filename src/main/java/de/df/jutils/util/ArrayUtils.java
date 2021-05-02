package de.df.jutils.util;

import java.util.Collections;
import java.util.Enumeration;

public class ArrayUtils {
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
        return Collections.list(keys).toArray(String[]::new);
    }
}
