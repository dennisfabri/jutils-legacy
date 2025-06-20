package de.df.jutils.gui.jtable;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.io.Serial;

/**
 * @author Dennis Fabri
 * Created on 17.10.2004
 */
public class JGroupableTable extends JTable {

    @Serial
    private static final long serialVersionUID = 3977296633635747637L;

    /**
     * 
     */
    public JGroupableTable() {
        super();
        pack();
    }

    public JGroupableTable(TableModel arg0) {
        super(arg0);
        pack();
    }

    @Override
    public JTableHeader createDefaultTableHeader() {
        return new GroupableTableHeader(columnModel);
    }

    private boolean doPack;

    private void pack() {
        doPack = true;
        if (isShowing()) {
            JTableUtils.setPreferredCellSizes(this);
            doPack = false;
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (doPack) {
            JTableUtils.setPreferredCellSizes(this);
            doPack = false;
        }
    }
}
