/*
 * Created on 05.02.2006
 */
package de.df.jutils.io.csv;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.LinkedList;

public final class CsvManager {

    private static char                      separator = ',';

    private static LinkedList<CsvDataWriter> converters;

    static {
        NumberFormat nf = NumberFormat.getInstance();
        if (nf instanceof DecimalFormat) {
            DecimalFormat df = (DecimalFormat) nf;
            DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            separator = dfs.getPatternSeparator();
        }

        converters = new LinkedList<CsvDataWriter>();
        addWriterConverter(new CsvStringConverter());
    }

    private CsvManager() {
        // Hide
    }

    public static CsvWriter getWriterInstance() {
        return new CsvWriter(converters.toArray(new CsvDataWriter[converters.size()]), separator);
    }

    public static CsvReader getReaderInstance() {
        return new CsvReader(new CsvDataReader[0], separator);
    }

    public static void setSeparator(char s) {
        separator = s;
    }

    public static char getSeparator() {
        return separator;
    }

    public static void addWriterConverter(CsvDataWriter dc) {
        converters.addLast(dc);
    }

    public static boolean removeWriterConverter(CsvDataWriter dc) {
        return converters.remove(dc);
    }
}