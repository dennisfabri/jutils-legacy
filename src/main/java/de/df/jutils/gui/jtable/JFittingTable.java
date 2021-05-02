package de.df.jutils.gui.jtable;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class JFittingTable extends JTable {

    /**
     * 
     */
    private static final long serialVersionUID = -6410377655547595169L;

    public JFittingTable() {
        pack();
    }

    private boolean dopack;

    private void pack() {
        dopack = true;
        if (isShowing()) {
            JTableUtils.setPreferredCellSizes(this);
            dopack = false;
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (dopack) {
            JTableUtils.setPreferredCellSizes(this);
            dopack = false;
        }
    }
}