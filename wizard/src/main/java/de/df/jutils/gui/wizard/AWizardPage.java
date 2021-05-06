/*
 * Created on 15.04.2005
 */
package de.df.jutils.gui.wizard;

import javax.swing.JComponent;

public abstract class AWizardPage {

    public static final int NO_NEXT_PAGE       = -1;
    public static final int NEXT_PAGE_BY_INDEX = -2;

    private final String    title;
    private final String    note;
    private int             index              = NEXT_PAGE_BY_INDEX;
    private boolean         enabled            = true;

    public AWizardPage() {
        this(null, null);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean b) {
        enabled = b;
    }

    public AWizardPage(String title, String note) {
        if (title == null) {
            title = "";
        }
        if (note != null && note.length() == 0) {
            note = null;
        }
        this.title = title;
        this.note = note;
    }

    public final String getTitle() {
        return title;
    }

    public final String getNote() {
        return note;
    }

    public int getNextPageIndex() {
        return index;
    }

    public void setNextPageIndex(int index) {
        this.index = index;
    }

    public abstract JComponent getPage();

    public boolean leavePage(boolean forward) {
        return true;
    }
}