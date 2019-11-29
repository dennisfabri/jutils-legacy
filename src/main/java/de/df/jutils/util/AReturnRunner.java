/**
 * 
 */
package de.df.jutils.util;

public abstract class AReturnRunner<T extends Object> implements ReturnRunnable<T> {

    T result = null;

    public abstract T runme();

    @Override
    public final void run() {
        result = runme();
    }

    @Override
    public final T getResult() {
        return result;
    }
}