/*
 * Created on 24.02.2006
 */
package com.eit.easyprint;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.StringReader;

import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;

public class TextPrintable implements Printable {

    private final String text;
    private final Font font;
    private Book pages;

    public TextPrintable(String text, Font f) {
        this.text = text;
        this.font = f;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int index) throws PrinterException {
        if (pages == null) {
            pages = createPlainPageable(text, pf, font);
        }
        try {
            return pages.getPrintable(index).print(g, pf, 0);
        } catch (RuntimeException re) {
            return NO_SUCH_PAGE;
        }
    }

    /**
     * Print the plain text given as String parameter.
     * 
     * @param content html code that should be printed
     * @param pf      format of the page
     */
    private static Book createPlainPageable(String content, PageFormat pf, Font font) {
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

}
