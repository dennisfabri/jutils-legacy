/*
 * Created on 01.04.2006
 */
package de.df.jutils.gui.jtable;

import java.awt.Dimension;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ExtendedTableModel extends DefaultTableModel {

    private static final long serialVersionUID = -7569962895997629378L;

    private int[]             alignments;
    private String[]          formats;
    private String            name;
    private boolean           landscape;
    private TitleRow[]        extendedTitleRows;

    public ExtendedTableModel(Object[][] data, Object[] titles) {
        super(data, titles);
        alignments = new int[titles.length];
        formats = new String[titles.length];
        for (int x = 0; x < titles.length; x++) {
            alignments[x] = SwingConstants.LEFT;
            formats[x] = null;
        }
        name = "";
        landscape = false;
    }

    public ExtendedTableModel(TableModel tm) {
        super(getData(tm), getTitles(tm));
        alignments = new int[getColumnCount()];
        formats = new String[alignments.length];
        for (int x = 0; x < alignments.length; x++) {
            alignments[x] = SwingConstants.LEFT;
            formats[x] = null;
        }
        name = "";
        landscape = false;
    }

    private static Object[] getTitles(TableModel tm) {
        Object[] titles = new Object[tm.getColumnCount()];
        for (int x = 0; x < titles.length; x++) {
            titles[x] = tm.getColumnName(x);
        }
        return titles;
    }

    private static Object[][] getData(TableModel tm) {
        Object[][] data = new Object[tm.getRowCount()][tm.getColumnCount()];
        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[x].length; y++) {
                data[x][y] = tm.getValueAt(x, y);
            }
        }
        return data;
    }

    public int[] getColumnAlignments() {
        int[] result = new int[alignments.length];
        System.arraycopy(alignments, 0, result, 0, result.length);
        return result;
    }

    public void setColumnAlignment(int index, int align) {
        alignments[index] = align;
    }

    public void setColumnAlignments(int[] aligns) {
        System.arraycopy(aligns, 0, alignments, 0, alignments.length);
    }

    public void setColumnAlignments(Integer[] aligns) {
        for (int x = 0; x < alignments.length; x++) {
            alignments[x] = aligns[x];
        }
    }

    public int getColumnAlignment(int index) {
        return alignments[index];
    }

    public String getColumnFormat(int index) {
        String s = formats[index];
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return null;
        }
        return s;
    }

    public void setColumnFormat(int index, String f) {
        formats[index] = f;
    }

    public void setColumnFormats(String[] f) {
        System.arraycopy(f, 0, formats, 0, formats.length);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLandscape() {
        return landscape;
    }

    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
    }

    public static class TitleCell {
        public Dimension span;
        public String    title;

        public TitleCell(String title, int width, int height) {
            span = new Dimension(width, height);
            this.title = title;
        }
    }

    public static class TitleRow {
        public TitleCell[] cells;

        public TitleRow(TitleCell[] c) {
            cells = c;
        }
    }

    public TitleRow[] getExtendedTitleRows() {
        return extendedTitleRows;
    }

    public void setExtendedTitles(TitleRow[] extendedTitles) {
        this.extendedTitleRows = extendedTitles;
    }

    public int getExtendedTitlesRowCount() {
        if (extendedTitleRows == null) {
            return 0;
        }
        int max = 0;
        for (int x = 0; x < extendedTitleRows.length; x++) {
            for (int y = 0; y < extendedTitleRows[x].cells.length; y++) {
                TitleCell cell = extendedTitleRows[x].cells[y];
                int row = x + cell.span.height;
                if (row > max) {
                    max = row;
                }
            }
        }
        return max;
    }

    public int getExtendedTitlesColumnCount() {
        if (extendedTitleRows == null) {
            return 0;
        }
        int max = 0;
        for (TitleRow extendedTitleRow : extendedTitleRows) {
            int count = 0;
            for (int y = 0; y < extendedTitleRow.cells.length; y++) {
                TitleCell cell = extendedTitleRow.cells[y];
                count += cell.span.width;
            }
            if (count > max) {
                max = count;
            }
        }
        return max;
    }
}