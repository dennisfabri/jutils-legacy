package de.df.jutils.util;

public class UniqueKeyProvider {

    private long time  = 0;
    private long count = -1;

    public UniqueKeyProvider() {
        // Nothing to do
    }

    public String getNextKey() {
        synchronized (this) {
            long current = System.nanoTime();
            if (current == time) {
                count++;
            } else {
                time = current;
                count = -1;
            }
            return time + (count > 0 ? "x" + count : "");
        }
    }
}
