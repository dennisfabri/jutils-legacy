/*
 * Created on 01.11.2003
 */
package de.df.jutils.graphics;

/**
 * @author Dennis Fabri
 */
final class Calculator {

    private Calculator() {
        // Never used
    }

    private static int calculateValue(final double start, final double end, final double percent) {
        return (int) (start + (Math.max(Math.min(percent, 1.0), -1.0) * (end - start)));
    }

    public static int calculateValue(final double start, final double end, final double percent, int min, int max) {
        int value = calculateValue(start, end, percent);
        value = Math.min(max, value);
        value = Math.max(min, value);
        return value;
    }
}
