/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.*;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
public class JvFillDownAction extends AbstractAction {

    private static final long serialVersionUID = -7378149930498281690L;

    protected JTable          table;

    public JvFillDownAction(JTable table) {
        super("Fill Down");
        this.table = table;

        // As far as I can tell, in Excel the "Fill Down" action is always
        // enabled.
        setEnabled(true);

        KeyStroke accel = KeyStroke.getKeyStroke('D', InputEvent.CTRL_MASK);
        putValue(ACCELERATOR_KEY, accel);

        table.getInputMap().put(accel, "fillDown");
        table.getActionMap().put("fillDown", this);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Cell selection in JTable looks like a hack. Basically it looks like
        // the union of column and row selection.
        int[] rowIndices = table.getSelectedRows();
        if (rowIndices.length == 0) {
            return;
        }

        JvUndoableTableModel model = (JvUndoableTableModel) table.getModel();
        JvCellFillEdit fillEdit = new JvCellFillEdit(model);

        for (int columnIndex : table.getSelectedColumns()) {
            int index = table.convertColumnIndexToModel(columnIndex);
            JvColumnFill fill = new JvColumnFill(model, index, rowIndices);
            fillEdit.addFill(fill);
        }

        fillEdit.fill();
        model.fireUndoableEdit(fillEdit);
    }
}