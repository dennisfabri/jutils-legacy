/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public class JvUndoAction extends AbstractAction {

    private static final long   serialVersionUID = 8437480104052445257L;

    protected final UndoManager manager;

    public JvUndoAction(UndoManager manager) {
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            manager.undo();
        } catch (CannotUndoException ex) {
            ex.printStackTrace();
        }
    }
}