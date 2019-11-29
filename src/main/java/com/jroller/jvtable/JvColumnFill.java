/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public class JvColumnFill implements JvCellFill {

    protected int      columnIndex;
    protected int[]    rowIndices;
    protected Object[] values;

    public JvColumnFill(JvUndoableTableModel model, int columnIndex, int[] rowIndices) {
        this.columnIndex = columnIndex;
        this.rowIndices = rowIndices;

        values = new Object[rowIndices.length];
        for (int i = 0; i < rowIndices.length; i++) {
            values[i] = model.getValueAt(rowIndices[i], columnIndex);
        }
    }

    @Override
    public void doFill(JvUndoableTableModel model) {
        for (int i = 1; i < rowIndices.length; i++) {
            if (model.isCellEditable(rowIndices[i], columnIndex)) {
                model.setValueAt(values[0], rowIndices[i], columnIndex, false);
            }
        }
    }

    @Override
    public void undoFill(JvUndoableTableModel model) {
        for (int i = 1; i < rowIndices.length; i++) {
            if (model.isCellEditable(rowIndices[i], columnIndex)) {
                model.setValueAt(values[i], rowIndices[i], columnIndex, false);
            }
        }
    }
}