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

    public JFittingTable(TableModel dm) {
        super(dm);
        pack();
    }

    public JFittingTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        pack();
    }

    public JFittingTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        pack();
    }

    public JFittingTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        pack();
    }

    public JFittingTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        pack();
    }

    private boolean dopack;

    public void pack() {
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