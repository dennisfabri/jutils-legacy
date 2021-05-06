/*
 * FormatError.java Created on 31. March 2002, 21:16
 */

package de.df.jutils.exception;

/**
 * @author dennis
 */
public class FormatErrorException extends java.lang.Exception {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257006544854986807L;

    public FormatErrorException(int iD, String msg) {
        super("" + iD + ": " + msg);
    }
}