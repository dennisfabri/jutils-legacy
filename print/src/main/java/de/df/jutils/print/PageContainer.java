/*
 * Created on 23.12.2005
 */
package de.df.jutils.print;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.imageio.ImageIO;

class PageContainer implements AutoCloseable {

    private LinkedList<File> files = new LinkedList<>();

    public PageContainer() {
        // Nothing to do
    }

    synchronized void clear() {
        if (!files.isEmpty()) {
            ListIterator<File> li = files.listIterator();
            while (li.hasNext()) {
                File file = li.next();
                if (file.exists()) {
                    try {
                        boolean d = file.delete();
                        if (!d) {
                            file.deleteOnExit();
                        }
                    } catch (SecurityException se) {
                        // We are not allowed to delete
                        // Maybe it can be done during exit
                    }
                }
                li.remove();
            }
        }
    }

    synchronized int size() {
        return files.size();
    }

    synchronized boolean add(BufferedImage img) {
        try {
            File file = File.createTempFile("jauswertung", ".preview");
            file.deleteOnExit();
            files.add(file);

            ImageIO.write(img, "png", file);
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
        return false;
    }

    synchronized BufferedImage get(int index) {
        try {
            File file = files.get(index);
            return ImageIO.read(file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // Nothing to do
        } catch (RuntimeException re) {
            re.printStackTrace();
            // Nothing to do
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        clear();        
    }
}
