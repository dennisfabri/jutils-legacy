package de.df.jutils.print;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageSetting implements Serializable {

    private static Logger log = LoggerFactory.getLogger(PageSetting.class);

    private static final long serialVersionUID = 1283270490564385699L;

    private final String name;
    private final int orientation;
    private final double pageWidth;
    private final double pageHeight;
    private final double imageableX;
    private final double imageableY;
    private final double imageableWidth;
    private final double imageableHeight;

    public PageSetting(String name, PageFormat pf) {
        this.name = name;
        this.orientation = pf.getOrientation();
        
        pf.setOrientation(PageFormat.PORTRAIT);
        
        this.pageWidth = pf.getPaper().getWidth();
        this.pageHeight = pf.getPaper().getHeight();
        this.imageableX = pf.getImageableX();
        this.imageableY = pf.getImageableY();
        this.imageableWidth = pf.getImageableWidth();
        this.imageableHeight = pf.getImageableHeight();

        pf.setOrientation(this.orientation);
    }

    public PageFormat toPageFormat() {
        Paper paper = new Paper();
        paper.setSize(pageWidth, pageHeight);
        paper.setImageableArea(imageableX, imageableY, imageableWidth, imageableHeight);

        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);
        pf.setOrientation(orientation);

        return pf;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return String.format("PageSetting '%s': %d (%.2f %.2f) (%.2f %.2f %.2f %.2f)", name, orientation, pageWidth, pageHeight,
                imageableX, imageableY, imageableWidth, imageableHeight);
    }
}
