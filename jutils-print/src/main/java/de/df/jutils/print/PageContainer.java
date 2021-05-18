/*
 * Created on 23.12.2005
 */
package de.df.jutils.print;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.imageio.ImageIO;

class PageContainer implements AutoCloseable {

    private LinkedList<byte[]> files = new LinkedList<>();

    public PageContainer() {
        // Nothing to do
    }

    synchronized void clear() {
        files.clear();
    }

    synchronized int size() {
        return files.size();
    }

    synchronized void add(BufferedImage img) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", bos);
            files.add(bos.toByteArray());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
    }

    synchronized BufferedImage get(int index) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(files.get(index));
            return ImageIO.read(bis);
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
