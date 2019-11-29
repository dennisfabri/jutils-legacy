/*
 * Created on 05.02.2006
 */
package de.df.jutils.io.csv;

import java.text.NumberFormat;

public class CsvNumberFormatDoubleConverter implements CsvDataWriter {

    private NumberFormat format;

    public CsvNumberFormatDoubleConverter() {
        this(NumberFormat.getNumberInstance());
    }

    public CsvNumberFormatDoubleConverter(NumberFormat nf) {
        format = nf;
    }

    @Override
    public boolean canConvert(Object o) {
        return (o instanceof Double) || (o instanceof Float);
    }

    @Override
    public String convert(Object o, String format1) {
        double l = 0;
        if (o instanceof Float) {
            l = (Float) o;
        }
        if (o instanceof Double) {
            l = (Double) o;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\"");
        sb.append(format.format(l));
        sb.append("\"");
        return sb.toString();
    }
}