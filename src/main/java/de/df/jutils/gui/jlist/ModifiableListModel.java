/*
 * Created on 23.10.2005
 */
package de.df.jutils.gui.jlist;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;

public class ModifiableListModel<T extends Object> extends AbstractListModel<T> {

    private static final long serialVersionUID = 8145547222092502695L;

    protected LinkedList<T>   data;

    public ModifiableListModel() {
        data = new LinkedList<>();
    }

    public ModifiableListModel(Collection<T> list) {
        data = new LinkedList<>(list);
    }

    public ModifiableListModel(T[] d) {
        data = new LinkedList<>(Arrays.asList(d));
    }

    public int size() {
        return data.size();
    }

    public boolean contains(T value) {
        return data.contains(value);
    }

    @Override
    public int getSize() {
        return size();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void sort(Comparator<T> c) {
        if (c == null) {
            Collections.sort((List<Comparable>) data);
        } else {
            Collections.sort(data, c);
        }
        fireContentsChanged(this, 0, data.size() - 1);
    }

    public LinkedList<T> getAllElements() {
        return new LinkedList<>(data);
    }

    @Override
    public T getElementAt(int index) {
        return data.get(index);
    }

    public void move(int from, int to) {
        T t = data.remove(from);
        data.add(to, t);
        int min = Math.min(from, to);
        int max = Math.max(from, to);

        fireContentsChanged(this, min, max);
    }

    public void add(T object, int index) {
        data.add(index, object);
        fireIntervalAdded(this, index, index);
    }

    public void addFirst(T object) {
        data.addFirst(object);
        fireIntervalAdded(this, 0, 0);
    }

    public void addLast(T object) {
        data.addLast(object);
        fireIntervalAdded(this, data.size() - 1, data.size() - 1);
    }

    public void addAll(T[] objects) {
        for (T object : objects) {
            addLast(object);
        }
    }

    public void addAll(Collection<T> objects) {
        if (objects.isEmpty()) {
            return;
        }
        data.addAll(objects);
        fireIntervalAdded(this, data.size() - objects.size(), data.size() - 1);
    }

    public void remove(T object) {
        if (data.isEmpty()) {
            return;
        }
        int pos = data.indexOf(object);
        if (pos >= 0) {
            data.remove(object);
            fireIntervalRemoved(this, pos, pos);
        }
    }

    public void removeAll() {
        if (data.isEmpty()) {
            return;
        }
        int size = data.size();
        data.clear();
        fireIntervalRemoved(this, 0, size - 1);
    }

    public void setValueAt(int index, T object) {
        if (data.size() <= index) {
            return;
        }
        data.set(index, object);
        fireContentsChanged(this, index, index);
    }

    public T remove(int index) {
        if (data.isEmpty()) {
            return null;
        }
        T t = data.remove(index);
        fireIntervalRemoved(this, index, index);
        return t;
    }

    public void clear() {
        int size = data.size();
        if (size == 0) {
            return;
        }
        data.clear();
        fireIntervalRemoved(this, 0, size - 1);
    }
}