/*
 * Created on 05.02.2006
 */
package de.df.jutils.io.csv;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import javax.swing.table.TableModel;

import de.df.jutils.gui.jtable.ExtendedTableModel;

public class CsvWriter {

    private char                      separator;
    private LinkedList<CsvDataWriter> converters;

    CsvWriter(CsvDataWriter[] cdc, char separator) {
        converters = new LinkedList<CsvDataWriter>();
        for (CsvDataWriter aCdc : cdc) {
            converters.addLast(aCdc);
        }
        this.separator = separator;
    }

    public void addConverter(CsvDataWriter cdc) {
        converters.addLast(cdc);
    }

    public void removeConverter(CsvDataWriter cdc) {
        converters.remove(cdc);
    }

    public boolean write(TableModel tm, OutputStream os) {
        if (tm == null) {
            throw new NullPointerException("TableModel must not be null!");
        }
        if (os == null) {
            throw new NullPointerException("OutputStream must not be null!");
        }
        PrintStream ps = new PrintStream(os);
        int cols = tm.getColumnCount();
        int rows = tm.getRowCount();
        if (cols == 0) {
            throw new IllegalStateException("TableModel must contain at least one column!");
        }
        ExtendedTableModel etm = null;
        if (tm instanceof ExtendedTableModel) {
            etm = (ExtendedTableModel) tm;
        }
        writeCell(ps, tm.getColumnName(0), null);
        for (int x = 1; x < cols; x++) {
            ps.print(separator);
            writeCell(ps, tm.getColumnName(x), null);
        }
        ps.println();
        for (int y = 0; y < rows; y++) {
            writeCell(ps, tm.getValueAt(y, 0), (etm == null ? null : etm.getColumnFormat(0)));
            for (int x = 1; x < cols; x++) {
                ps.print(separator);
                writeCell(ps, tm.getValueAt(y, x), (etm == null ? null : etm.getColumnFormat(x)));
            }
            ps.println();
        }
        return true;
    }

    private void writeCell(PrintStream ps, Object o, String format) {
        ps.print(convert(o, format));
    }

    private String convert(Object o, String format) {
        if (o == null) {
            return "";
        }
        for (CsvDataWriter converter : converters) {
            if (converter.canConvert(o)) {
                return converter.convert(o, format);
            }
        }
        return "\"" + o.toString() + "\"";
    }
}