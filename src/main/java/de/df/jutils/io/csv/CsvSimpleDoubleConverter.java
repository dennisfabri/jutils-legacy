/*
 * Created on 05.02.2006
 */
package de.df.jutils.io.csv;

public class CsvSimpleDoubleConverter implements CsvDataWriter {

    private char decimalPoint   = '.';
    private int  fractionDigits = 2;

    public CsvSimpleDoubleConverter() {
        this('.', 2);
    }

    public CsvSimpleDoubleConverter(char decimalPoint, int fractionDigits) {
        this.decimalPoint = decimalPoint;
        this.fractionDigits = fractionDigits;
    }

    @Override
    public boolean canConvert(Object o) {
        return (o instanceof Double) || (o instanceof Float) || (o instanceof FixedDecimal);
    }

    private String punkteStringCSV(final double punkte, final int length) {
        long mult = Math.round(Math.pow(10, length));
        long punkteHundert = Math.round(punkte * mult);
        long punkteGanz = punkteHundert / mult;
        punkteHundert = punkteHundert % mult;

        StringBuilder sb = new StringBuilder();

        // Set first digits
        sb.append(punkteGanz);

        if (length == 0) {
            return sb.toString();
        }

        sb.append(decimalPoint);

        // Fill with zeros
        mult = mult / 10;
        while (punkteHundert < mult) {
            sb.append("0");
            mult = mult / 10;
        }

        if (punkteHundert != 0) {
            // append value
            sb.append(punkteHundert);
        }

        // Return result
        return sb.toString();
    }

    @Override
    public String convert(Object o, String format) {
        if (o instanceof Number) {
            return punkteStringCSV(((Number) o).doubleValue(), fractionDigits);
        }
        if (o instanceof FixedDecimal) {
            FixedDecimal fd = (FixedDecimal) o;
            return punkteStringCSV(fd.getValue(), fd.getLength());
        }
        return "";
    }
}