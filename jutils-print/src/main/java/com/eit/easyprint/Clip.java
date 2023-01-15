/*
 * Modulname: Clip
 * Autor: Eyer Leander
 * Datum: 04.05.2005
 * (c) Copyright 2005 by
 * Eyer IT Services, Naters
 */

/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.eit.easyprint;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * When printing multipage documents, we have to create Clips for every page.
 * 
 * @author Leander Eyer <leander@eyerit.ch>
 * @version 1.01
 */
class Clip implements Printable {

    /** The component we delegate the painting to */
    private Printable component;
    /** Top of the page */
    private int start;
    /** Height of the page */
    private int height;

    /**
     * Class Constructor
     * 
     * @param component Component that is clipped
     * @param start     top coordinate of the page
     * @param height    height of the page
     */
    public Clip(Printable component, int start, int height) {
        this.component = component;
        this.start = start;
        this.height = height;
    }

    /**
     * Print the page described by this clip.
     * 
     * @param graphics Graphics object we paint the component upon
     * @param format   The format of the page
     * @param page     The index of the page
     * @return A status code: Printable.PAGE_EXISTS if the page exists
     * @see java.awt.print.Printable
     */
    @Override
    public int print(Graphics graphics, PageFormat format, int page) throws PrinterException {

        // create a new graphic instance so that changes don't propagate
        Graphics g = graphics.create();

        // compensate for page borders
        g.translate((int) format.getImageableX(), (int) format.getImageableY());

        // move the clip to the requested page
        g.translate(0, -1 * start);

        // restrict the paintable area
        int width = (int) Math.ceil(format.getWidth());
        g.setClip(0, start, width, height);

        // Perform the printing
        component.print(g, format, page);

        // we don't need the graphic any more, destory it
        g.dispose();

        // indicate that everything went right
        return Printable.PAGE_EXISTS;
    }

}
