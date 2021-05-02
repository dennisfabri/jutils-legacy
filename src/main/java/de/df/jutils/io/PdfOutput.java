/*
 * Created on 16.01.2005
 */
package de.df.jutils.io;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfGraphics2D;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import de.df.jutils.i18n.util.JUtilsI18n;
import de.df.jutils.util.Feedback;
import de.df.jutils.util.NullFeedback;

/**
 * @author Dennis Mueller
 * @date 16.01.2005
 */
public class PdfOutput {

    private static final String WINDOWS_FONTPATH = "C:/windows/fonts";

    private Printable           source;
    private OutputStream        out;
    private boolean             close            = false;
    private boolean             portrait;
    private Insets              margin           = new Insets(30, 30, 30, 30);

    public PdfOutput(OutputStream os, Printable p, boolean portrait) {
        if ((p == null) || (os == null)) {
            throw new NullPointerException();
        }
        source = p;
        out = os;
        this.portrait = portrait;
    }

    public PdfOutput(String filename, Printable p, boolean portrait) throws FileNotFoundException {
        if ((p == null) || (filename == null)) {
            throw new NullPointerException();
        }
        source = p;
        out = new FileOutputStream(new File(filename));
        close = true;
        this.portrait = portrait;
    }

    public boolean write() {
        boolean result = write(out, source, portrait, margin, new NullFeedback());
        if (close) {
            try {
                out.close();
            } catch (IOException ioe) {
                // Nothing to do
            }
        }
        return result;
    }

    private static DefaultFontMapper fontMapper = null;

    private static FontMapper getFontMapper() {
        if (fontMapper == null) {
            fontMapper = new DefaultFontMapper();
            if (new File(WINDOWS_FONTPATH).exists()) {
                fontMapper.insertDirectory(WINDOWS_FONTPATH);
            }
        }
        return fontMapper;
    }

    public static boolean write(OutputStream out, Printable source, boolean portrait, Insets margin, Feedback fb) {
        boolean success = true;

        int pages = 0;

        FontFactory.defaultEmbedding = true;

        ByteArrayOutputStream fos = new ByteArrayOutputStream(1024 * 1024);
        try {
            Document document;
            if (portrait) {
                document = new Document(PageSize.A4);
            } else {
                document = new Document(PageSize.A4.rotate());
            }
            Rectangle size = document.getPageSize();
            Paper paper = new Paper();
            paper.setSize(size.getWidth(), size.getHeight());
            paper.setImageableArea(margin.left, margin.top, size.getWidth() - margin.left - margin.right, size.getHeight() - margin.top - margin.bottom);
            PageFormat pf = new PageFormat();
            pf.setOrientation(PageFormat.PORTRAIT);
            pf.setPaper(paper);
            try {
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                document.open();
                PdfContentByte cb = writer.getDirectContent();

                int result = Printable.PAGE_EXISTS;
                int index = 0;

                while (result != Printable.NO_SUCH_PAGE) {
                    pages++;

                    fb.showFeedback(JUtilsI18n.get("PageNr", pages));

                    // Create the graphics with pdf fonts
                    if (index > 0) {
                        document.newPage();
                    }

                    Graphics2D g2 = new PdfGraphics2D(cb, size.getWidth(), size.getHeight(), getFontMapper(), false, false, 0);

                    // Print the table to the graphics
                    g2.clipRect(0, 0, (int) size.getWidth(), (int) size.getHeight());
                    result = source.print(g2, pf, index);

                    g2.dispose();

                    index++;
                }
            } catch (PrinterException pe) {
                pe.printStackTrace();
                success = false;
            } catch (DocumentException de) {
                de.printStackTrace();
                success = false;
            }

            document.close();

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                fos.close();
            } catch (IOException ioe) {
                // Nothing to do
            }
            return false;
        }

        if (!success) {
            return false;
        }

        if (pages == 1) {
            return false;
        }

        fb.showFeedback(JUtilsI18n.get("de.dm.io.pdf.WritingFile"));

        try {
            PdfReader reader = new PdfReader(fos.toByteArray());
            Document document = new Document(reader.getPageSizeWithRotation(1));
            PdfCopy copy = new PdfCopy(document, out);
            document.open();
            for (int x = 0; x < pages - 1; x++) {
                copy.addPage(copy.getImportedPage(reader, x + 1));
            }
            document.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean write(String filename, Printable printable, boolean portrait, Feedback fb) {
        try {
            FileOutputStream out = new FileOutputStream(new File(filename));
            boolean result = write(out, printable, portrait, fb);
            out.close();
            return result;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    public static boolean write(OutputStream filename, Printable printable, boolean portrait, Feedback fb) {
        return write(filename, printable, portrait, new Insets(30, 30, 30, 30), fb);
    }
}
