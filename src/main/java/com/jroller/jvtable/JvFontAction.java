/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public class JvFontAction extends AbstractAction {

    private static final long serialVersionUID = 4020028400365174592L;

    protected JComponent      component;
    protected UndoManager     undoManager;
    protected String          fontSpec;

    public JvFontAction(JComponent component, UndoManager undoManager, String fontSpec) {
        super("Font " + fontSpec);
        this.component = component;
        this.undoManager = undoManager;
        this.fontSpec = fontSpec.replace(" ", "-");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Font oldFont = component.getFont();
        Font font = Font.decode(fontSpec);

        JvFontEdit edit = new JvFontEdit(component, oldFont, font);
        edit.doit();
        UndoableEditEvent editEvent = new UndoableEditEvent(component, edit);
        undoManager.undoableEditHappened(editEvent);
    }
}