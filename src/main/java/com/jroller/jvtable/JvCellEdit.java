/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public class JvCellEdit extends AbstractUndoableEdit {

    private static final long      serialVersionUID = 3240230179265118361L;

    protected JvUndoableTableModel tableModel;
    protected Object               oldValue;
    protected Object               newValue;
    protected int                  row;
    protected int                  column;

    public JvCellEdit(JvUndoableTableModel tableModel, Object oldValue, Object newValue, int row, int column) {
        this.tableModel = tableModel;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.row = row;
        this.column = column;
    }

    @Override
    public String getPresentationName() {
        return "Cell Edit";
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        tableModel.setValueAt(oldValue, row, column, false);
    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();

        tableModel.setValueAt(newValue, row, column, false);
    }
}