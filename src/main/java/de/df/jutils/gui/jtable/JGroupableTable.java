/*
 * Created on 17.10.2004
 */
package de.df.jutils.gui.jtable;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * @author Dennis Mueller
 * @date 17.10.2004
 */
public class JGroupableTable extends JTable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3977296633635747637L;

    /**
     * 
     */
    public JGroupableTable() {
        super();
        pack();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public JGroupableTable(int arg0, int arg1) {
        super(arg0, arg1);
        pack();
    }

    /**
     * @param arg0
     */
    public JGroupableTable(TableModel arg0) {
        super(arg0);
        pack();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public JGroupableTable(Object[][] arg0, Object[] arg1) {
        super(arg0, arg1);
        pack();
    }

    /**
     * @param arg0
     * @param arg1
     */
    /**
     * @param arg0
     * @param arg1
     */
    public JGroupableTable(TableModel arg0, TableColumnModel arg1) {
        super(arg0, arg1);
        pack();
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     */
    public JGroupableTable(TableModel arg0, TableColumnModel arg1, ListSelectionModel arg2) {
        super(arg0, arg1, arg2);
        pack();
    }

    @Override
    public JTableHeader createDefaultTableHeader() {
        return new GroupableTableHeader(columnModel);
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