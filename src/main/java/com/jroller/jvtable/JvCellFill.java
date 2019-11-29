/*
 * Created on 09.02.2006
 */
package com.jroller.jvtable;

/**
 * @author Neil W. Weber neilweber@yahoo.com
 * @link http://jroller.com/trackback/nweber/Weblog/excel_like_zooming_in_a
 * @since 09.02.2006
 */
interface JvCellFill {

    void doFill(JvUndoableTableModel model);

    void undoFill(JvUndoableTableModel model);
}