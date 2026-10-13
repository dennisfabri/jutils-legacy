package de.df.jutils.print.printables;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

import org.junit.jupiter.api.Test;

class BannerPrintableTests {

    private static final double A4_WIDTH = 595;
    private static final double A4_HEIGHT = 842;

    private static PageFormat pageFormat(double width, double height, int orientation) {
        Paper paper = new Paper();
        paper.setSize(width, height);
        paper.setImageableArea(20, 20, width - 40, height - 40);
        PageFormat pf = new PageFormat();
        pf.setPaper(paper);
        pf.setOrientation(orientation);
        return pf;
    }

    @Test
    void portraitPageIsNotLandscape() {
        assertFalse(BannerPrintable.isLandscape(pageFormat(A4_WIDTH, A4_HEIGHT, PageFormat.PORTRAIT)));
    }

    @Test
    void landscapeOrientationFromThePrintDialogIsLandscape() {
        assertTrue(BannerPrintable.isLandscape(pageFormat(A4_WIDTH, A4_HEIGHT, PageFormat.LANDSCAPE)));
    }

    @Test
    void rotatedPaperFromThePdfExportIsLandscape() {
        assertTrue(BannerPrintable.isLandscape(pageFormat(A4_HEIGHT, A4_WIDTH, PageFormat.PORTRAIT)));
    }
}
