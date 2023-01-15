/*
 * Created on 01.10.2004
 */
package de.df.jutils.gui.jtable;

/**
 * @author Dennis Fabri
 * @date 01.10.2004
 */
public class BasicTableModel extends SimpleTableModel {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3760562001079775794L;

    private Class<?>[] types;

    public BasicTableModel(Object[] titles, Object[][] data, Class<?>[] ctypes) {
        super(data, titles);
        types = ctypes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int arg0) {
        return types[arg0];
    }
}
