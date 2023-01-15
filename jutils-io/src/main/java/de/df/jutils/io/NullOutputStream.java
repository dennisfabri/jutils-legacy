/*
 * Created on 26.10.2005
 */
package de.df.jutils.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class simply consumes every output without writing it anywhere.
 * 
 * @author Dennis Fabri
 */
public class NullOutputStream extends OutputStream {

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // Nothing to do
    }

    @Override
    public void write(byte[] b) throws IOException {
        // Nothing to do
    }

    @Override
    public void write(int b) throws IOException {
        // Nothing to do
    }
}
