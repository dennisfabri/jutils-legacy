/**
 * 
 */
package de.df.jutils.util;

public interface ReturnRunnable<T extends Object> extends Runnable {
    T getResult();
}