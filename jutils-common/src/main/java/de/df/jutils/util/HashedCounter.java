package de.df.jutils.util;

import java.util.Hashtable;
import java.util.Map;

public class HashedCounter {

    private Map<String, Integer> data = new Hashtable<>();

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
