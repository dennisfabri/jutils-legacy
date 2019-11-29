/*
 * Created on 29.11.2003
 */
package de.df.jutils.gui;

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * @author Dennis Mueller
 */
public class JProperties extends JPanel {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256437002110316854L;
    private ResourceBundle    rb               = null;

    public JProperties(final ResourceBundle bundle) {
        this();
        rb = bundle;
    }

    public JProperties() {
        JPropertiesTable pt = new JPropertiesTable();

        setLayout(new BorderLayout());
        add(pt.getTableHeader(), BorderLayout.NORTH);
        add(new JScrollPane(pt), BorderLayout.CENTER);
    }

    String translate(final String key) {
        if (rb == null) {
            return key;
        }
        try {
            return rb.getString(key);
        } catch (RuntimeException re) {
            return key;
        }
    }

    private final class JPropertiesTable extends JTable {

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3256442516780692273L;

        public JPropertiesTable() {
            setModel(new PropertiesTableModel());
            getTableHeader().setReorderingAllowed(false);
        }

        private final class PropertiesTableModel implements TableModel {
            private String[]   titles = new String[] { translate("Property"), translate("Value") };
            private String[][] data   = null;

            public PropertiesTableModel() {
                int size = System.getProperties().size();
                data = new String[size][2];
                Enumeration<Object> keys = System.getProperties().keys();
                int y = 0;
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    String value = System.getProperty(key);
                    data[y][0] = key;
                    data[y][1] = value;
                    y++;
                }
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public int getRowCount() {
                return data.length;
            }

            @Override
            public boolean isCellEditable(final int y, final int x) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(final int x) {
                return String.class;
            }

            @Override
            public Object getValueAt(final int y, final int x) {
                return data[y][x];
            }

            @Override
            public void setValueAt(final Object val, final int y, final int x) {
                throw new SecurityException();
            }

            @Override
            public String getColumnName(final int x) {
                return titles[x];
            }

            @Override
            public void addTableModelListener(final TableModelListener tml) {
                // Nothing to do
            }

            @Override
            public void removeTableModelListener(final TableModelListener tml) {
                // Nothing to do
            }
        }
    }

    public static void main(final String[] args) {
        JFrame frame = new JFrame("Test");
        frame.getContentPane().add(new JProperties());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
