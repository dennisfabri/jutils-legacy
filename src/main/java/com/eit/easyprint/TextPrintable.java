/*
 * Created on 24.02.2006
 */
package com.eit.easyprint;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.*;

public class TextPrintable implements Printable {

    private final String text;
    private final Font   font;
    private Book         pages = null;

    public TextPrintable(String text) {
        this(text, null);
    }

    public TextPrintable(String text, Font f) {
        this.text = text;
        this.font = f;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int index) throws PrinterException {
        if (pages == null) {
            pages = EasyPrint.createPlainPageable(text, pf, font);
        }
        try {
            return pages.getPrintable(index).print(g, pf, 0);
        } catch (RuntimeException re) {
            return NO_SUCH_PAGE;
        }
    }

}