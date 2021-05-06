/*
 * Created on 28.02.2006
 */
package de.df.jutils.io.csv;

import java.io.InputStream;
import java.util.LinkedList;

import de.df.jutils.io.FileUtils;
import de.df.jutils.util.StringTools;

public class CsvReader {
    private char                      separator;
    private LinkedList<CsvDataReader> converters;

    CsvReader(CsvDataReader[] cdc, char separator) {
        converters = new LinkedList<CsvDataReader>();
        for (CsvDataReader aCdc : cdc) {
            converters.addLast(aCdc);
        }
        this.separator = separator;
    }

    public void addConverter(CsvDataReader cdc) {
        converters.addLast(cdc);
    }

    public void removeConverter(CsvDataReader cdc) {
        converters.remove(cdc);
    }

    public CsvTableModel read(InputStream is) {
        if (is == null) {
            throw new NullPointerException("InputStream must not be null!");
        }

        String[] lines = FileUtils.readTextFile(is);
        int height = lines.length - 1;
        int width = 0;
        for (int x = 0; x < lines.length; x++) {
            int count = StringTools.countCsvColumns(lines[0], separator);
            if (count > width) {
                width = count;
            }
        }

        String[] titles = new String[width];
        Object[][] data = new Object[width][height];

        String[] line = StringTools.separateCsvLine(lines[0], separator);
        for (int x = 0; x < width; x++) {
            if (x < line.length) {
                titles[x] = line[x];
            } else {
                titles[x] = "";
            }
        }

        for (int y = 0; y < height; y++) {
            line = StringTools.separateCsvLine(lines[y + 1], separator);
            for (int x = 0; x < width; x++) {
                if (x < line.length) {
                    data[x][y] = convert(line[x]);
                } else {
                    data[x][y] = "";
                }
            }
        }
        return new CsvTableModel(data, titles);
    }

    private Object convert(String o) {
        if (o == null) {
            return "";
        }
        for (CsvDataReader converter : converters) {
            if (converter.canConvert(o)) {
                return converter.convert(o);
            }
        }
        return o;
    }

}
