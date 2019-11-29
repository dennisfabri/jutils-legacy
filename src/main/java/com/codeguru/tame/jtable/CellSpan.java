/*
 * (swing1.1beta3)
 */

package com.codeguru.tame.jtable;

import javax.swing.JTable;

/**
 * Based on
 * 
 * @link http://www.codeguru.com/java/articles/139.shtml
 * @version 1.0 11/22/98
 */

public interface CellSpan {
    public final int ROW    = 0;
    public final int COLUMN = 1;

    public int[] getSpan(int row, int column);

    public void setSpan(int[] span, int row, int column);

    public boolean isVisible(int row, int column);

    public void combine(int[] rows, int[] columns, JTable table);

    public void split(int row, int column, JTable table);

}
