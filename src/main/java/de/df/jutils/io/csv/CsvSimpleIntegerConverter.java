/*
 * Created on 05.02.2006
 */
package de.df.jutils.io.csv;

public class CsvSimpleIntegerConverter implements CsvDataWriter {

    public CsvSimpleIntegerConverter() {
        // Nothing to do
    }

    @Override
    public boolean canConvert(Object o) {
        return (o instanceof Integer) || (o instanceof Long) || (o instanceof Short) || (o instanceof Byte);
    }

    @Override
    public String convert(Object o, String format) {
        if (o instanceof Number) {
            int digits = 0;
            if (format != null) {
                for (int x = 0; x < format.length(); x++) {
                    if (format.charAt(x) == '0') {
                        digits++;
                    } else {
                        digits = 1;
                        break;
                    }
                }
            }
            long value = ((Number) o).longValue();
            StringBuilder s = new StringBuilder();
            s.append(value);
            while (s.length() < digits) {
                s.insert(0, '0');
            }
            return s.toString();
        }
        return "";
    }
}