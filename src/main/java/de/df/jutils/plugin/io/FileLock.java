package de.df.jutils.plugin.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileLock {

    private FileOutputStream lock     = null;
    private String           filename = null;

    public FileLock(String name) {
        String tmp = System.getProperty("java.io.tmpdir");
        if (!tmp.endsWith(File.separator)) {
            tmp += File.separator;
        }
        filename = tmp + name + ".lock";
    }

    public boolean lock() {
        if (lock != null) {
            return true;
        }
        File file = new File(filename);
        if (file.exists()) {
            boolean result = file.delete();
            if (!result) {
                return false;
            }
        }
        try {
            lock = new FileOutputStream(filename);
            // Make sure that the file is at least deleted on exit
            file.deleteOnExit();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void unlock() {
        if (lock == null) {
            return;
        }
        try {
            lock.close();
        } catch (IOException e) {
            // Nothing to do
        }
        lock = null;
        File file = new File(filename);
        if (file.exists()) {
            try {
                boolean d = file.delete();
                if (!d) {
                    file.deleteOnExit();
                }
            } catch (SecurityException se) {
                // Nothing to do
            }
        }
    }
}
