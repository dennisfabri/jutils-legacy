/*
 * Created on 01.12.2003
 */
package de.df.jutils.exception;

/**
 * @author Dennis Mueller
 */
public class ParserException extends Exception {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3546926896031215664L;

    public ParserException() {
        super();
    }

    public ParserException(final String text) {
        super(text);
    }

    public ParserException(final Throwable cause) {
        super(cause);
    }

    public ParserException(final String text, final Throwable cause) {
        super(text, cause);
    }
}