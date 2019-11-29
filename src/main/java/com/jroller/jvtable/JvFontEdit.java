/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public class JvFontEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -339620973608329398L;

    protected JComponent      component;
    protected Font            oldValue;
    protected Font            newValue;

    public JvFontEdit(JComponent component, Font oldValue, Font newValue) {
        this.component = component;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String getPresentationName() {
        return "Font";
    }

    @Override
    public String getUndoPresentationName() {
        return super.getUndoPresentationName() + ' ' + getFontSpec(oldValue);
    }

    @Override
    public String getRedoPresentationName() {
        return super.getRedoPresentationName() + ' ' + getFontSpec(newValue);
    }

    protected String getFontSpec(Font font) {
        return font.getFontName();
    }

    public void doit() {
        setFont(newValue);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        setFont(oldValue);
    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();

        setFont(newValue);
    }

    protected void setFont(Font font) {
        component.setFont(font);
        if (component instanceof JTable) {
            ((JTable) component).getTableHeader().setFont(font);
        }
    }
}