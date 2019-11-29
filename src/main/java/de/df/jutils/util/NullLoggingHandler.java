/*
 * Created on 03.01.2006
 */
package de.df.jutils.util;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class NullLoggingHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        // Nothing to do
    }

    @Override
    public void flush() {
        // Nothing to do
    }

    @Override
    public void close() throws SecurityException {
        // Nothing to do
    }
}