/*
 * Created on 03.01.2006
 */
package de.df.jutils.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.ListIterator;

public class BytesOutputStream extends OutputStream {

    private static final int SIZE    = 1024 * 1024;

    LinkedList<byte[]>       bytes   = new LinkedList<byte[]>();
    private byte[]           current = new byte[SIZE];
    private int              index   = 0;

    @Override
    public synchronized void write(int byt) throws IOException {
        current[index] = (byte) byt;
        index++;
        if (index == SIZE) {
            bytes.addLast(current);
            current = new byte[SIZE];
            index = 0;
        }
    }

    public synchronized byte[] getData() {
        byte[] result = new byte[bytes.size() * SIZE + index];
        ListIterator<byte[]> li = bytes.listIterator();
        int x = 0;
        while (li.hasNext()) {
            byte[] line = li.next();
            for (int i = 0; i < SIZE; i++) {
                result[x] = line[i];
                x++;
            }
        }
        for (int y = 0; y < index; y++) {
            result[x] = current[y];
            x++;
        }
        return result;
    }
}