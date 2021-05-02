/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

import javax.swing.Action;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public final class JvUndoManager extends UndoManager {

    private static final long serialVersionUID = -4176632480848201958L;

    protected Action          undoAction;
    protected Action          redoAction;

    public JvUndoManager() {
        this.undoAction = new JvUndoAction(this);
        this.redoAction = new JvRedoAction(this);

        synchronizeActions(); // to set initial names
    }

    public Action getUndoAction() {
        return undoAction;
    }

    public Action getRedoAction() {
        return redoAction;
    }

    @Override
    public synchronized boolean addEdit(UndoableEdit anEdit) {
        try {
            return super.addEdit(anEdit);
        } finally {
            synchronizeActions();
        }
    }

    @Override
    protected void undoTo(UndoableEdit edit) throws CannotUndoException {
        try {
            super.undoTo(edit);
        } finally {
            synchronizeActions();
        }
    }

    @Override
    protected void redoTo(UndoableEdit edit) throws CannotRedoException {
        try {
            super.redoTo(edit);
        } finally {
            synchronizeActions();
        }
    }

    protected void synchronizeActions() {
        undoAction.setEnabled(canUndo());
        undoAction.putValue(Action.NAME, getUndoPresentationName());

        redoAction.setEnabled(canRedo());
        redoAction.putValue(Action.NAME, getRedoPresentationName());
    }
}