/*
 * Created on 01.04.2006
 */
package de.df.jutils.io.csv;

public class FixedDecimal extends Number implements Comparable<FixedDecimal> {

    private double  value;
    private int     length;
    private boolean empty;

    public FixedDecimal(double v, int length) {
        value = v;
        this.length = length;
        empty = false;
    }

    public FixedDecimal() {
        empty = true;
        value = 0;
        length = 0;
    }

    public int getLength() {
        return length;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (empty) {
            return " ";
        }
        return punkteStringCSV(value, length);
    }

    private static String punkteStringCSV(final double punkte, final int length) {
        long mult = Math.round(Math.pow(10, length));
        long punkteHundert = Math.round(punkte * mult);
        long punkteGanz = punkteHundert / mult;
        punkteHundert = punkteHundert % mult;

        StringBuilder sb = new StringBuilder();

        // Set first digits
        sb.append(punkteGanz);
        sb.append('.');

        if (punkteHundert == 0) {
            mult = mult / 10;
        }
        // Fill with zeros
        mult = mult / 10;
        while (punkteHundert < mult) {
            sb.append("0");
            mult = mult / 10;
        }

        // append value
        sb.append(punkteHundert);

        // Return result
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FixedDecimal && compareTo((FixedDecimal) obj) == 0;
    }

    @Override
    public int hashCode() {
        return (int) value * 1000;
    }

    @Override
    public int compareTo(FixedDecimal fd) {
        return (int) ((value - fd.value) * 1000);
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }
}