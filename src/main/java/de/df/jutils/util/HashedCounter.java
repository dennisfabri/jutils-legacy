package de.df.jutils.util;

import java.util.Hashtable;

public class HashedCounter {

    private Hashtable<String, Integer> data = new Hashtable<String, Integer>();

    public HashedCounter() {
    }

    public int get(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        }
        return 0;
    }

    public void inc(String key) {
        data.put(key, get(key) + 1);
    }

}
