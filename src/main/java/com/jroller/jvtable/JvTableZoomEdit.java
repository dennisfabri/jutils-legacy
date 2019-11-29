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
public class JvTableZoomEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -344791447080743506L;

    protected JvTable         table;
    protected float           oldValue;
    protected float           newValue;

    public JvTableZoomEdit(JvTable table, float oldValue, float newValue) {
        this.table = table;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String getPresentationName() {
        return "Zoom";
    }

    public void doit() {
        table.setZoom(newValue);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        table.setZoom(oldValue);
    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();

        table.setZoom(newValue);
    }
}