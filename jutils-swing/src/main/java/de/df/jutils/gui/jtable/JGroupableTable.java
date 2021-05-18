/*
 * Created on 17.10.2004
 */
package de.df.jutils.gui.jtable;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

/**
 * @author Dennis Fabri
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
     */
    public JGroupableTable(TableModel arg0) {
        super(arg0);
        pack();
    }

    @Override
    public JTableHeader createDefaultTableHeader() {
        return new GroupableTableHeader(columnModel);
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