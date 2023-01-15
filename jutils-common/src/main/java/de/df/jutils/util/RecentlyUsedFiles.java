/*
 * Created on 10.12.2004
 */
package de.df.jutils.util;

import java.util.LinkedList;
import java.util.prefs.Preferences;

public class RecentlyUsedFiles {

    private String key;
    private String[] names;
    private LinkedList<ChangeListener> listeners;

    public RecentlyUsedFiles() {
        this(null, null);
    }

    private RecentlyUsedFiles(Preferences pref, String key) {
        if (key == null || key.trim().isEmpty()) {
            this.key = "Recent";
        } else {
            this.key = "Recent." + key;
        }

        int size1 = 8;
        int size2 = 8;
        if (pref != null) {
            size2 = pref.getInt(this.key, 8);
        }
        if (size1 < size2) {
            size1 = size2;
        }
        names = new String[size1];
        listeners = new LinkedList<>();

        if (pref != null) {
            for (int x = size2 - 1; x >= 0; x--) {
                add(pref.get(this.key + x, null));
            }
        }
    }

    public int getLength() {
        return names.length;
    }

    public synchronized void update(int index) {
        if (index < 0) {
            return;
        }
        if (index >= names.length) {
            return;
        }
        String name = names[index];
        for (int x = index; x > 0; x--) {
            names[x] = names[x - 1];
        }
        names[0] = name;

        invokeListeners();
    }

    public synchronized void add(String name) {
        if (name == null) {
            return;
        }
        if (name.length() == 0) {
            return;
        }
        for (int x = 0; x < names.length; x++) {
            if (name.equals(names[x])) {
                update(x);
                return;
            }
        }
        names[names.length - 1] = name;
        update(names.length - 1);
    }

    public String getName(int x) {
        return names[x];
    }

    public void save(Preferences pref) {
        pref.putInt(key, names.length);
        for (int x = 0; x < names.length; x++) {
            pref.put(key + x, (names[x] == null ? "" : names[x]));
        }
    }

    public static RecentlyUsedFiles open(Preferences pref) {
        return open(pref, null);
    }

    public static RecentlyUsedFiles open(Preferences pref, String key) {
        return new RecentlyUsedFiles(pref, key);
    }

    private void invokeListeners() {
        for (ChangeListener cl : listeners) {
            try {
                cl.changed();
            } catch (Exception t) {
                // Safety
                t.printStackTrace();
            }
        }
    }

    public void addListener(ChangeListener cl) {
        listeners.addLast(cl);
    }

    public static interface ChangeListener {
        void changed();
    }
}
