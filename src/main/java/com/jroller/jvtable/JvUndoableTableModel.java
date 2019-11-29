/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.undo.UndoableEdit;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public class JvUndoableTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 5974708649628020369L;

    public JvUndoableTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column >= 0 && column < getColumnCount()) {
            return getValueAt(0, column).getClass();
        }

        return Object.class;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        setValueAt(value, row, column, true);
    }

    public void setValueAt(Object value, int row, int column, boolean undoable) {
        UndoableEditListener[] listeners = getListeners(UndoableEditListener.class);
        if ((!undoable) || listeners == null) {
            super.setValueAt(value, row, column);
            return;
        }

        Object oldValue = getValueAt(row, column);
        super.setValueAt(value, row, column);
        JvCellEdit cellEdit = new JvCellEdit(this, oldValue, value, row, column);
        fireUndoableEdit(listeners, cellEdit);
    }

    public void addUndoableEditListener(UndoableEditListener listener) {
        listenerList.add(UndoableEditListener.class, listener);
    }

    public void fireUndoableEdit(UndoableEdit edit) {
        UndoableEditListener[] listeners = getListeners(UndoableEditListener.class);
        fireUndoableEdit(listeners, edit);
    }

    protected void fireUndoableEdit(UndoableEditListener[] listeners, UndoableEdit edit) {
        UndoableEditEvent editEvent = new UndoableEditEvent(this, edit);
        for (UndoableEditListener listener : listeners) {
            listener.undoableEditHappened(editEvent);
        }
    }
}