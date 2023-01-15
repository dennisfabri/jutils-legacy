/*
 * Created on 05.04.2004
 */
package de.df.jutils.gui.jtable;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * @author Dennis Fabri
 * @date 05.04.2004
 */
public class SimpleTableModel extends DefaultTableModel {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256727260227908406L;

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return false;
    }

    public void removeColumn(int i) {
        Vector<String> header = new Vector<>();
        for (int x = 0; x < getColumnCount(); x++) {
            if (x != i) {
                header.add(getColumnName(x));
            }
        }
        @SuppressWarnings("rawtypes")
        Vector<? extends Vector> data = getDataVector();
        for (Object aData : data) {
            Vector<?> row = (Vector<?>) aData;
            row.remove(i);
        }
        setDataVector(data, header);
    }

    public void removeColumn() {
        removeColumn(getColumnCount() - 1);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SimpleTableModel(Object[][] arg0, Object[] arg1) {
        super(arg0, arg1);
    }
}
