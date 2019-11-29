package com.jroller.jvtable;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public class JvZoomAction extends AbstractAction {

    private static final long serialVersionUID = 4770805923049010085L;

    protected JvTable         table;
    protected UndoManager     undoManager;
    protected float           scaleFactor;

    public JvZoomAction(JvTable table, UndoManager undoManager, int percent) {
        super("Zoom " + percent + "%");
        this.table = table;
        this.undoManager = undoManager;
        scaleFactor = percent / 100.0f;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        float oldScaleFactor = table.getZoom();
        if (Math.abs(oldScaleFactor - scaleFactor) < 0.001) {
            return;
        }

        JvTableZoomEdit edit = new JvTableZoomEdit(table, oldScaleFactor, scaleFactor);
        edit.doit();
        UndoableEditEvent editEvent = new UndoableEditEvent(table, edit);
        undoManager.undoableEditHappened(editEvent);
    }
}