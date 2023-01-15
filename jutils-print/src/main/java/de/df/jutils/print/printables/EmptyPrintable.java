/*
 * Created on 24.02.2006
 */
package de.df.jutils.print.printables;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public final class EmptyPrintable implements Printable {

    public static final Printable Instance = new EmptyPrintable();

    private EmptyPrintable() {
    }

    @Override
    public int print(Graphics arg0, PageFormat arg1, int arg2) throws PrinterException {
        return NO_SUCH_PAGE;
    }

}
