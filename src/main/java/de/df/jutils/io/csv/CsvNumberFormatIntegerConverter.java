/*
 * Created on 05.02.2006
 */
package de.df.jutils.io.csv;

import java.text.NumberFormat;

public class CsvNumberFormatIntegerConverter implements CsvDataWriter {

    private NumberFormat format;

    public CsvNumberFormatIntegerConverter() {
        this(NumberFormat.getIntegerInstance());
    }

    public CsvNumberFormatIntegerConverter(NumberFormat nf) {
        format = nf;
    }

    @Override
    public boolean canConvert(Object o) {
        return (o instanceof Integer) || (o instanceof Long) || (o instanceof Short) || (o instanceof Byte);
    }

    @Override
    public String convert(Object o, String format1) {
        long l = 0;
        if (o instanceof Short) {
            l = (Short) o;
        }
        if (o instanceof Long) {
            l = (Long) o;
        }
        if (o instanceof Integer) {
            l = (Integer) o;
        }
        if (o instanceof Byte) {
            l = (Byte) o;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\"");
        sb.append(format.format(l));
        sb.append("\"");
        return sb.toString();
    }
}