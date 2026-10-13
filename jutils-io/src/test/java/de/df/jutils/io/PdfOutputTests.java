package de.df.jutils.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.print.Printable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.parser.PdfTextExtractor;

import de.df.jutils.util.NullFeedback;

class PdfOutputTests {

    private static final String FONT = "Liberation Sans";
    private static final String NAMES = "Crêpé Ćirić Łukasz Dvořák Şahin Ős Müller Šarić";

    @Test
    void namesOutsideOfWindows1252AreKept() throws IOException {
        assumeTrue(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
                .contains(FONT));
        Printable page = (g, pf, index) -> {
            if (index > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            g.setFont(new Font(FONT, Font.PLAIN, 12));
            g.drawString(NAMES, 72, 100);
            return Printable.PAGE_EXISTS;
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        assertTrue(PdfOutput.write(out, page, true, new Insets(30, 30, 30, 30), new NullFeedback()));

        try (PdfReader reader = new PdfReader(out.toByteArray())) {
            assertEquals(NAMES, new PdfTextExtractor(reader).getTextFromPage(1).strip());
        }
    }
}
