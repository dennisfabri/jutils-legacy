package de.df.jutils.gui.jlist;

import java.util.Collections;

public class SortableListModel<T extends Comparable<? super T>> extends ModifiableListModel<T> {

    private static final long serialVersionUID = -888083025786242340L;

    public SortableListModel() {
        super();
    }

    public void sort() {
        Collections.sort(data);
        fireContentsChanged(this, 0, data.size() - 1);
    }
}