/*
 * This file is based on a tutorial "How do I create a JTable with nested
 * headers?" by Nobuo Tamemasa first posted at
 * http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html and found
 * at http://www.esus.com/docs/GetQuestionPage.jsp?uid=1272
 * Since the sources were not working with J2SE 1.4 I had to make a few changes
 * 5th July 2004 Dennis Mueller
 */
package de.df.jutils.gui.jtable;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * * GroupableTableHeader * *
 * 
 * @version 1.0 10/20/98 *
 * @author Nobuo Tamemasa
 */
public class GroupableTableHeader extends JTableHeader {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3905245628094035000L;

    static {
        UIManager.put("GroupableTableHeaderUI", "de.df.jutils.gui.jtable.GroupableTableHeaderUI");
        UIManager.put("de.df.jutils.gui.jtable.GroupableTableHeaderUI", GroupableTableHeaderUI.class);
    }

    private Vector<Object> columnGroups = null;

    @Override
    public String getUIClassID() {
        return "GroupableTableHeaderUI";
    }

    public GroupableTableHeader(TableColumnModel model) {
        super(model);
        setUI(new GroupableTableHeaderUI());
        super.setReorderingAllowed(false);
    }

    @Override
    public void setReorderingAllowed(boolean b) {
        // super.setReorderingAllowed(false);
    }

    public void addColumnGroup(ColumnGroup g) {
        if (columnGroups == null) {
            columnGroups = new Vector<Object>();
        }
        columnGroups.addElement(g);
    }

    public Enumeration<Object> getColumnGroups(TableColumn col) {
        if (columnGroups == null) {
            return null;
        }
        Enumeration<Object> e = columnGroups.elements();
        while (e.hasMoreElements()) {
            ColumnGroup cGroup = (ColumnGroup) e.nextElement();
            Vector<Object> cGroups = cGroup.getColumnGroups(col, new Vector<Object>());
            if (cGroups != null) {
                return cGroups.elements();
            }
        }
        return null;
    }

    public void setColumnMargin() {
        if (columnGroups == null) {
            return;
        }
        int columnMargin = getColumnModel().getColumnMargin();
        Enumeration<Object> e = columnGroups.elements();
        while (e.hasMoreElements()) {
            ColumnGroup cGroup = (ColumnGroup) e.nextElement();
            cGroup.setColumnMargin(columnMargin);
        }
    }

    @Override
    public void setDefaultRenderer(TableCellRenderer renderer) {
        super.setDefaultRenderer(renderer);
        if (columnGroups == null) {
            return;
        }
        Enumeration<Object> enumeration = columnGroups.elements();
        while (enumeration.hasMoreElements()) {
            ColumnGroup cg = (ColumnGroup) enumeration.nextElement();
            cg.setHeaderRenderer(renderer);
        }

        TableColumnModel tcm = getColumnModel();
        for (int x = 0; x < tcm.getColumnCount(); x++) {
            tcm.getColumn(x).setHeaderRenderer(renderer);
        }
    }
}