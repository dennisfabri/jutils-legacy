/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public class JvCellFillEdit extends AbstractUndoableEdit {

    private static final long       serialVersionUID = 5710374060137631303L;

    protected JvUndoableTableModel  tableModel;
    protected ArrayList<JvCellFill> fills            = new ArrayList<JvCellFill>();

    public JvCellFillEdit(JvUndoableTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public void addFill(JvCellFill fill) {
        fills.add(fill);
    }

    @Override
    public String getPresentationName() {
        return "Cell Fill";
    }

    public void fill() {
        for (JvCellFill fill : fills) {
            fill.doFill(tableModel);
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        for (JvCellFill fill : fills) {
            fill.undoFill(tableModel);
        }
    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();
        fill();
    }
}