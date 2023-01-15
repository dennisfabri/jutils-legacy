/*
 * Modulname: PrintableTextPane Autor: Eyer Leander Datum: 04.05.2005
 * (c) Copyright 2005 by Eyer IT Services, Naters
 */

/**
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.eit.easyprint;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JTextPane;

/**
 * The JTextPane enhanced with Printablility (we essentially implement the Print
 * Method and deactivate Double Buffering).
 * 
 * @author Leander Eyer <leander@eyerit.ch>
 * @version 1.01
 */
class PrintableTextPane extends JTextPane implements Printable {

    private static final long serialVersionUID = 5395957181114217722L;

    /** Create the Printable Text Pane */
    public PrintableTextPane() {
        super();
        setDoubleBuffered(false);
    }

    /**
     * Since all the work is done in the PageFactory, we just simply print the
     * component.
     * 
     * @param g      Preclipped and translated graphics instance
     * @param format Format of the page on which we print
     * @param page   Index of the page beeing printed
     * @return the status code Printable.PAGE_EXISTS
     */
    @Override
    public int print(Graphics g, PageFormat format, int page) throws PrinterException {
        if (g != null) {
            paint(g);
        }
        return Printable.PAGE_EXISTS;
    }

}
