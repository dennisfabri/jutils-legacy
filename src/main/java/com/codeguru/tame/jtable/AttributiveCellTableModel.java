/*
 * (swing1.1beta3)
 */

package com.codeguru.tame.jtable;

import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

/**
 * Based on
 * 
 * @link http://www.codeguru.com/java/articles/139.shtml
 * @version 1.0 11/22/98
 */
public class AttributiveCellTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 2530848981917692228L;
    
    protected CellAttribute   cellAtt;

    @SuppressWarnings("rawtypes")
    public AttributiveCellTableModel(int numRows, int numColumns) {
        Vector<Object> names = new Vector<Object>(numColumns);
        names.setSize(numColumns);
        setColumnIdentifiers(names);
        dataVector = new Vector<Vector>();
        setNumRows(numRows);
        cellAtt = new DefaultCellAttribute(numRows, numColumns);
    }

    @SuppressWarnings("rawtypes")
    public AttributiveCellTableModel(Vector<Object> columnNames, int numRows) {
        setColumnIdentifiers(columnNames);
        dataVector = new Vector<Vector>();
        setNumRows(numRows);
        cellAtt = new DefaultCellAttribute(numRows, columnNames.size());
    }

    public AttributiveCellTableModel(Object[] columnNames, int numRows) {
        this(convertToVector(columnNames), numRows);
    }

    public AttributiveCellTableModel(Vector<Object[]> data, Vector<String> columnNames) {
        setDataVector(data.toArray(new Object[data.size()][0]), columnNames.toArray(new Object[columnNames.size()]));
    }

    public AttributiveCellTableModel(Object[][] data, Object[] columnNames) {
        setDataVector(data, columnNames);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setDataVector(Vector<? extends Vector> newData, Vector<?> columnNames) {
        super.setDataVector(newData, columnNames);
        cellAtt = new DefaultCellAttribute(dataVector.size(), columnIdentifiers.size());
        newRowsAdded(
                new TableModelEvent(this, 0, getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addColumn(Object columnName, Vector<?> columnData) {
        if (columnName == null)
            throw new IllegalArgumentException("addColumn() - null parameter");
        columnIdentifiers.addElement(columnName);
        int index = 0;
        Enumeration<?> enumeration = dataVector.elements();
        while (enumeration.hasMoreElements()) {
            Object value;
            if ((columnData != null) && (index < columnData.size()))
                value = columnData.elementAt(index);
            else
                value = null;
            ((Vector<Object>) enumeration.nextElement()).addElement(value);
            index++;
        }

        //
        cellAtt.addColumn();

        fireTableStructureChanged();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void addRow(Vector rowData) {
        Vector<?> newData = null;
        if (rowData == null) {
            newData = new Vector<Object>(getColumnCount());
        } else {
            rowData.setSize(getColumnCount());
        }
        dataVector.addElement(newData);

        //
        cellAtt.addRow();

        newRowsAdded(new TableModelEvent(this, getRowCount() - 1, getRowCount() - 1, TableModelEvent.ALL_COLUMNS,
                TableModelEvent.INSERT));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void insertRow(int row, Vector rowData) {
        if (rowData == null) {
            rowData = new Vector<Object>(getColumnCount());
        } else {
            rowData.setSize(getColumnCount());
        }

        dataVector.insertElementAt(rowData, row);
        cellAtt.insertRow(row);

        newRowsAdded(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    public CellAttribute getCellAttribute() {
        return cellAtt;
    }

    public void setCellAttribute(CellAttribute newCellAtt) {
        int numColumns = getColumnCount();
        int numRows = getRowCount();
        if ((newCellAtt.getSize().width != numColumns) || (newCellAtt.getSize().height != numRows)) {
            newCellAtt.setSize(new Dimension(numRows, numColumns));
        }
        cellAtt = newCellAtt;
        fireTableDataChanged();
    }
}