/*
 * (swing1.1beta3)
 */

package com.codeguru.tame.jtable;

import java.awt.*;
import java.io.Serializable;

import javax.swing.JTable;

/**
 * Based on
 * 
 * @link http://www.codeguru.com/java/articles/139.shtml
 * @version 1.0 11/22/98
 */

public class DefaultCellAttribute implements CellAttribute, CellSpan, Serializable {

    private static final long serialVersionUID = 1179508536420887658L;
    
    //
    // !!!! CAUTION !!!!!
    // these values must be synchronized to Table data
    //
    protected int       rowSize;
    protected int       columnSize;
    protected int[][][] span;      // CellSpan
    protected Color[][] foreground; // ColoredCell
    protected Color[][] background; //
    protected Font[][]  font;      // CellFont

    public DefaultCellAttribute() {
        this(1, 1);
    }

    public DefaultCellAttribute(int numRows, int numColumns) {
        setSize(new Dimension(numColumns, numRows));
    }

    protected void initValue() {
        for (int i = 0; i < span.length; i++) {
            for (int j = 0; j < span[i].length; j++) {
                span[i][j][CellSpan.COLUMN] = 1;
                span[i][j][CellSpan.ROW] = 1;
            }
        }
    }

    //
    // CellSpan
    //
    @Override
    public int[] getSpan(int row, int column) {
        if (isOutOfBounds(row, column)) {
            return new int[] { 1, 1 };
        }
        return span[row][column];
    }

    @Override
    public void setSpan(int[] span, int row, int column) {
        if (isOutOfBounds(row, column))
            return;
        this.span[row][column] = span;
    }

    @Override
    public boolean isVisible(int row, int column) {
        return !isOutOfBounds(row, column) && !((span[row][column][CellSpan.COLUMN] < 1) || (span[row][column][CellSpan.ROW] < 1));
    }

    @Override
    public void combine(int[] rows, int[] columns, JTable table) {
        if (isOutOfBounds(rows, columns))
            return;
        int rowSpan = rows.length;
        int columnSpan = columns.length;
        int startRow = rows[0];
        int startColumn = columns[0];
        for (int i = 0; i < rowSpan; i++) {
            for (int j = 0; j < columnSpan; j++) {
                if ((span[startRow + i][startColumn + j][CellSpan.COLUMN] != 1) || (span[startRow + i][startColumn + j][CellSpan.ROW] != 1)) {
                    return;
                }
            }
        }
        for (int i = 0, ii = 0; i < rowSpan; i++, ii--) {
            for (int j = 0, jj = 0; j < columnSpan; j++, jj--) {
                span[startRow + i][startColumn + j][CellSpan.COLUMN] = jj;
                span[startRow + i][startColumn + j][CellSpan.ROW] = ii;
            }
        }
        span[startRow][startColumn][CellSpan.COLUMN] = columnSpan;
        span[startRow][startColumn][CellSpan.ROW] = rowSpan;

        table.revalidate();
        table.repaint();
    }

    @Override
    public void split(int row, int column, JTable table) {
        if (isOutOfBounds(row, column))
            return;
        int columnSpan = span[row][column][CellSpan.COLUMN];
        int rowSpan = span[row][column][CellSpan.ROW];
        for (int i = 0; i < rowSpan; i++) {
            for (int j = 0; j < columnSpan; j++) {
                span[row + i][column + j][CellSpan.COLUMN] = 1;
                span[row + i][column + j][CellSpan.ROW] = 1;
            }
        }
        table.revalidate();
        table.repaint();
    }

    //
    // CellAttribute
    //
    @Override
    public void addColumn() {
        int[][][] oldSpan = span;
        int numRows = oldSpan.length;
        int numColumns = oldSpan[0].length;
        span = new int[numRows][numColumns + 1][2];
        System.arraycopy(oldSpan, 0, span, 0, numRows);
        for (int i = 0; i < numRows; i++) {
            span[i][numColumns][CellSpan.COLUMN] = 1;
            span[i][numColumns][CellSpan.ROW] = 1;
        }
    }

    @Override
    public void addRow() {
        int[][][] oldSpan = span;
        int numRows = oldSpan.length;
        int numColumns = oldSpan[0].length;
        span = new int[numRows + 1][numColumns][2];
        System.arraycopy(oldSpan, 0, span, 0, numRows);
        for (int i = 0; i < numColumns; i++) {
            span[numRows][i][CellSpan.COLUMN] = 1;
            span[numRows][i][CellSpan.ROW] = 1;
        }
    }

    @Override
    public void insertRow(int row) {
        int[][][] oldSpan = span;
        int numRows = oldSpan.length;
        int numColumns = oldSpan[0].length;
        span = new int[numRows + 1][numColumns][2];
        if (0 < row) {
            System.arraycopy(oldSpan, 0, span, 0, row - 1);
        }
        System.arraycopy(oldSpan, 0, span, row, numRows - row);
        for (int i = 0; i < numColumns; i++) {
            span[row][i][CellSpan.COLUMN] = 1;
            span[row][i][CellSpan.ROW] = 1;
        }
    }

    @Override
    public Dimension getSize() {
        return new Dimension(rowSize, columnSize);
    }

    @Override
    public void setSize(Dimension size) {
        columnSize = size.width;
        rowSize = size.height;
        span = new int[rowSize][columnSize][2]; // 2: COLUMN,ROW
        foreground = new Color[rowSize][columnSize];
        background = new Color[rowSize][columnSize];
        font = new Font[rowSize][columnSize];
        initValue();
    }

    /*
     * public void changeAttribute(int row, int column, Object command) { }
     * public void changeAttribute(int[] rows, int[] columns, Object command) {
     * }
     */

    protected boolean isOutOfBounds(int row, int column) {
        return (row < 0) || (rowSize <= row) || (column < 0) || (columnSize <= column);
    }

    protected boolean isOutOfBounds(int[] rows, int[] columns) {
        for (int row : rows) {
            if ((row < 0) || (rowSize <= row))
                return true;
        }
        for (int column : columns) {
            if ((column < 0) || (columnSize <= column))
                return true;
        }
        return false;
    }

    protected void setValues(Object[][] target, Object value, int[] rows, int[] columns) {
        for (int row : rows) {
            for (int column : columns) {
                target[row][column] = value;
            }
        }
    }
}
