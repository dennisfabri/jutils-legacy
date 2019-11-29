/*
 * Created on 05.02.2006
 */
package de.df.jutils.io.csv;

public class CsvSimpleSecondsConverter implements CsvDataWriter {

    private char decimalPoint = '.';

    public CsvSimpleSecondsConverter() {
        this('.');
    }

    public CsvSimpleSecondsConverter(char decimalPoint) {
        this.decimalPoint = decimalPoint;
    }

    @Override
    public boolean canConvert(Object o) {
        return (o instanceof Seconds);
    }

    @Override
    public String convert(Object o, String format) {
        if (o instanceof Seconds) {
            return ((Seconds) o).getString(decimalPoint);
        }
        return "";
    }
}