/*
 * Created on 05.02.2006
 */
package de.df.jutils.io.csv;

class CsvStringConverter implements CsvDataWriter {
    @Override
    public boolean canConvert(Object o) {
        return o instanceof String || o instanceof String[];
    }

    @Override
    public String convert(Object o, String format) {
        if (o instanceof String[]) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            String[] s = (String[]) o;
            boolean first = true;
            for (String line : s) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(line.replace("\"", "\\\""));
                first = false;
            }
            sb.append("\"");
            return sb.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(o.toString().replace("\"", "\\\""));
        sb.append("\"");
        return sb.toString();
    }
}
