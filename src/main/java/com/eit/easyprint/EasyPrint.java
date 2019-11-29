/*
 * Modulname: EasyPrint Autor: Eyer Leander Datum: 04.05.2005 (c) Copyright 2005
 * by Eyer IT Services, Naters
 */

/**
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA
 */

package com.eit.easyprint;

import java.awt.Font;
import java.awt.print.*;
import java.io.*;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * A utility class for printing HTML formated text and plain text (as well as
 * rtf).
 * 
 * @author Leander Eyer
 * @version 1.01
 */
public final class EasyPrint implements Runnable {

    /** The component that paints the content for us */
    private PrintableTextPane component;

    /** Print Job used for printing */
    private PrinterJob        job;
    /** Name of the print job */
    private String            printJobName;

    /**
     * Print the plain text given as String parameter.
     * 
     * @param content
     *            html code that should be printed
     * @param printJobName
     *            Name of the print job
     */
    public static void printPlain(String content, String printJobName) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        EditorKit editorKit = new StyledEditorKit();
        component.setEditorKit(editorKit);

        // read in the text given as parameter
        Document doc = editorKit.createDefaultDocument();
        try {
            editorKit.read(new StringReader(content), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        new EasyPrint(component, printJobName);
    }

    /**
     * Print the plain text given as String parameter.
     * 
     * @param content
     *            html code that should be printed
     * @param pf
     *            format of the page
     */
    public static Book createPlainPageable(String content, PageFormat pf, Font font) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        if (font != null) {
            component.setFont(font);
        }
        EditorKit editorKit = new StyledEditorKit();
        component.setEditorKit(editorKit);

        // read in the text given as parameter
        Document doc = editorKit.createDefaultDocument();
        try {
            editorKit.read(new StringReader(content), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        return new PageFactory(component, pf).getPages();
    }

    public static Book createHTMLPageable(String content, PageFormat pf, Font font) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        if (font != null) {
            component.setFont(font);
        }
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        component.setEditorKit(htmlKit);

        // read in the text given as parameter
        Document doc = htmlKit.createDefaultDocument();
        try {
            htmlKit.read(new StringReader(content), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        return new PageFactory(component, pf).getPages();
    }

    /**
     * Print the rtf content given as String parameter.
     * 
     * @param content
     *            rtf code that should be printed
     * @param printJobName
     *            Name of the print job
     */
    public static void printRTF(String content, String printJobName) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        RTFEditorKit rtfKit = new RTFEditorKit();
        component.setEditorKit(rtfKit);

        // read in the text given as parameter
        Document doc = rtfKit.createDefaultDocument();
        try {
            rtfKit.read(new StringReader(content), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        new EasyPrint(component, printJobName);
    }

    /**
     * Print the html content given as String parameter.
     * 
     * @param content
     *            html code that should be printed
     * @param printJobName
     *            Name of the print job
     */
    public static void printHTML(String content, String printJobName) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        component.setEditorKit(htmlKit);

        // read in the text given as parameter
        Document doc = htmlKit.createDefaultDocument();
        try {
            htmlKit.read(new StringReader(content), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        new EasyPrint(component, printJobName);
    }

    /**
     * Print the plain text referenced by the URL given as parameter.
     * 
     * @param content
     *            url for the plain text that should be printed
     * @param printJobName
     *            Name of the print job
     */
    public static void printPlain(URL content, String printJobName) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        StyledEditorKit styledKit = new StyledEditorKit();
        component.setEditorKit(styledKit);

        // read in the text given as parameter
        Document doc = styledKit.createDefaultDocument();
        try {
            styledKit.read(new InputStreamReader(content.openStream()), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        new EasyPrint(component, printJobName);
    }

    /**
     * Print the rtf content referenced by the URL given as parameter.
     * 
     * @param content
     *            url for the rtf code that should be printed
     * @param printJobName
     *            Name of the print job
     */
    public static void printRTF(URL content, String printJobName) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        RTFEditorKit rtfKit = new RTFEditorKit();
        component.setEditorKit(rtfKit);

        // read in the text given as parameter
        Document doc = rtfKit.createDefaultDocument();
        try {
            rtfKit.read(new InputStreamReader(content.openStream()), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        new EasyPrint(component, printJobName);
    }

    /**
     * Print the html content referenced by the URL given as parameter.
     * 
     * @param content
     *            url to the html code that should be printed
     * @param printJobName
     *            Name of the print job
     */
    public static void printHTML(URL content, String printJobName) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        component.setEditorKit(htmlKit);

        // read in the text given as parameter
        Document doc = htmlKit.createDefaultDocument();
        try {
            htmlKit.read(new InputStreamReader(content.openStream()), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        new EasyPrint(component, printJobName);
    }

    /**
     * Print the plain text delivered by the InputStream given as parameter.
     * 
     * @param content
     *            input stream for the plain textthat should be printed
     * @param printJobName
     *            Name of the print job
     */
    public static void printPlain(InputStream content, String printJobName) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        StyledEditorKit styledKit = new StyledEditorKit();
        component.setEditorKit(styledKit);

        // read in the text given as parameter
        Document doc = styledKit.createDefaultDocument();
        try {
            styledKit.read(new InputStreamReader(content), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        new EasyPrint(component, printJobName);
    }

    /**
     * Print the rtf content delivered by the InputStream given as parameter.
     * 
     * @param content
     *            input stream for the rtf code that should be printed
     * @param printJobName
     *            Name of the print job
     */
    public static void printRTF(InputStream content, String printJobName) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        RTFEditorKit rtfKit = new RTFEditorKit();
        component.setEditorKit(rtfKit);

        // read in the text given as parameter
        Document doc = rtfKit.createDefaultDocument();
        try {
            rtfKit.read(new InputStreamReader(content), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        new EasyPrint(component, printJobName);
    }

    /**
     * Print the html content delivered by the InputStream given as parameter.
     * 
     * @param content
     *            input stream for the html code that should be printed
     * @param printJobName
     *            Name of the print job
     */
    public static void printHTML(InputStream content, String printJobName) {
        // create the TextPane and install the printable editor kit
        PrintableTextPane component = new PrintableTextPane();
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        component.setEditorKit(htmlKit);

        // read in the text given as parameter
        Document doc = htmlKit.createDefaultDocument();
        try {
            htmlKit.read(new InputStreamReader(content), doc, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        component.setDocument(doc);

        // now perform the printing
        new EasyPrint(component, printJobName);
    }

    /**
     * Create the Printer. It receives a preconfigured JTextPane instance, with
     * a loaded document and an EditorKit installed which returns ExposedViews.
     * 
     * @param component
     *            Component to be printed
     * @param printJobName
     *            Name of the print job
     */
    private EasyPrint(PrintableTextPane component, String printJobName) {
        this.printJobName = printJobName;
        this.component = component;

        SwingUtilities.invokeLater(this);
    }

    /** Prepare and perform the printing in a separate Thread */
    @Override
    public void run() {
        preparePrinting();
        print();
    }

    /** Prepare the printing (Page break etc) */
    private void preparePrinting() {
        // Initialize Printing Classes
        job = PrinterJob.getPrinterJob();
        job.setJobName(printJobName);

        PageFormat pageFormat = job.defaultPage();
        pageFormat.setOrientation(PageFormat.PORTRAIT);

        // Parse the PrinterJob into its Pages
        PageFactory pageFactory = new PageFactory(component, pageFormat);
        job.setPageable(pageFactory.getPages());
    }

    /** Perform the actuall printing */
    private void print() {
        try {
            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
