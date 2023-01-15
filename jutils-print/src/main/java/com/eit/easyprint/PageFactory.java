/*
 * Modulname: PageFactory
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

import java.awt.Insets;
import java.awt.Shape;
import java.awt.print.Book;
import java.awt.print.PageFormat;

import javax.swing.text.View;

/**
 * This class performs the partition of the component to the single pages.
 * 
 * @author Leander Eyer <leander@eyerit.ch>
 * @version 1.01
 */
class PageFactory {

    /** Component to be split up */
    private PrintableTextPane component;
    /** Format of the page we print upon */
    private PageFormat pageFormat;
    /** Book with the pages for the component */
    private Book book;

    /** Maximum height of a page */
    private int pageHeight;
    /** Height of the component */
    private int componentHeight;

    /** Y start coordinate of the current Page */
    private int pageStart;
    /** Current End of the page */
    private int pageEnd;

    /**
     * Create the PageFactory. The construction of the Factory will already trigger
     * the creation of the pages. They can be retrieved using the getPages() method
     * 
     * @see #getPages()
     * @param component  The Component beeing printed
     * @param pageFormat The format of the Pages used for printing
     */
    PageFactory(PrintableTextPane component, PageFormat pageFormat) {
        this.component = component;
        this.pageFormat = pageFormat;

        prepareComponent();
        distributePages();
    }

    /** Prepare the component for being distributed into single pages */
    private void prepareComponent() {
        // preconfig the text component
        component.setMargin(new Insets(0, 0, 0, 0)); // no extra margin

        // resize to an optimal dimension
        int avaiableWidth = (int) Math.ceil(pageFormat.getImageableWidth());
        component.setSize(avaiableWidth, 50);
        componentHeight = component.getPreferredSize().height;
        component.setSize(avaiableWidth, componentHeight);
    }

    /** Distribute the Component on several pages */
    private void distributePages() {
        // initialize the book
        book = new Book();

        // set Object global variables that control the recursive algorithm
        pageHeight = (int) pageFormat.getImageableHeight();
        pageStart = 0;
        pageEnd = 0;

        // dump the structure
        dumpViewStructure("", component.getUI().getRootView(component), component.getBounds());

        // iterate recursively the view structure
        try {
            splitView(component.getUI().getRootView(component), component.getBounds());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // add last page if neccessary
        if (pageStart < componentHeight) {
            book.append(new Clip(component, pageStart, pageHeight), pageFormat);
        }
    }

    /**
     * This method traverses recursively the view hirarchy in order to split the
     * component between views onto several pages.
     * 
     * @param view  View to split up
     * @param shape Shape of the the view
     */
    private void splitView(View view, Shape shape) throws Exception {
        // we don't handle views which are not shaped
        if (shape == null) {
            return;
        }

        if (view.getViewCount() == 0) {
            // add the view to a page
            int viewHeight = shape.getBounds().width;
            int viewTop = shape.getBounds().y;
            int viewBottom = viewTop + shape.getBounds().height;

            // test if the view fits on the current page
            if (viewBottom <= pageStart + pageHeight) {
                // => It does fit on the current page
                if (pageEnd < viewBottom) {
                    pageEnd = viewBottom;
                }
            } else {
                // => It does not fit on the current page

                /*
                 * if this single view is higher than a single page, we distribute it on several
                 * pages. This is the only case where we split an atomic view.
                 */
                if (viewHeight > pageHeight) {
                    while (pageEnd < viewBottom) { // as long as we haven't put
                        // the whole view on pages
                        // ...
                        int remaining = viewBottom - pageEnd; // amount we
                        // still have to
                        // place

                        if (remaining >= pageHeight) {
                            pageEnd = pageStart + pageHeight;
                            Clip page = new Clip(component, pageStart, pageEnd - pageStart);
                            book.append(page, pageFormat);
                            pageStart = pageEnd;
                        } else {
                            pageEnd = viewBottom;
                        }
                    }
                } else {
                    Clip page = new Clip(component, pageStart, pageEnd - pageStart);
                    book.append(page, pageFormat);

                    // adjust page start value
                    pageStart = pageEnd;
                    pageEnd = viewBottom;
                }
            }
        } else {
            // Iterate through all child elements
            for (int i = 0; i < view.getViewCount(); i++) {
                View childView = view.getView(i);
                Shape childShape = view.getChildAllocation(i, shape);
                splitView(childView, childShape);
            }
        }
    }

    /** Dump the View Structure */
    private void dumpViewStructure(String prefix, View view, Shape shape) {
        if (shape == null) {
            return;
        }

        // String line = prefix + "-" + view.toString();
        // line += " [HEIGHT=" + view.getMinimumSpan(View.Y_AXIS);
        // line += " YOFFSET=:" + shape.getBounds().y + "]";

        for (int i = 0; i < view.getViewCount(); i++) {
            View child = view.getView(i);
            if (i == view.getViewCount() - 1) {
                dumpViewStructure(prefix + "  ", child, view.getChildAllocation(i, shape));
            } else {
                dumpViewStructure(prefix + " |", child, view.getChildAllocation(i, shape));
            }
        }
    }

    /**
     * Return a book containing the pages for the component
     * 
     * @return A book for the pages
     */
    public Book getPages() {
        return book;
    }

}
