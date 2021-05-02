/*
 * Created on 28.02.2006
 */
package de.df.jutils.io.csv;

import javax.swing.table.DefaultTableModel;

public class CsvTableModel extends DefaultTableModel {

    private static final long serialVersionUID = -1698293297858884386L;

    private final Object[]    title;
    private final Object[][]  data;

    CsvTableModel(Object[][] d, Object[] t) {
        super(d, t);
        data = d;
        title = t;
    }

    public Object[] getTitles() {
        return title;
    }

    public Object[][] getData() {
        return data;
    }
}