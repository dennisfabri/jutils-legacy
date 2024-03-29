/*
 * jPageSetup.java Created on 23. Oktober 2001, 22:19
 */

package de.df.jutils.print;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.HashMap;
import java.util.Map;

import javax.print.PrintService;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize.ISO;

import de.df.jutils.gui.util.DialogUtils;

public final class PageSetup {

    public static final float DPI = 72;

    private static Map<String, PageFormat> prasTable;
    private static PrintService ps;
    private static Printable psPrintable;
    private static PageFormat defaultPageFormat;

    static {
        ps = null;
        defaultPageFormat = createPageFormat();
        psPrintable = new PageSetupPrintable();
        prasTable = new HashMap<>();
    }

    private static void setPRASTable(Map<String, PageFormat> pt) {
        if (pt == null) {
            pt = new HashMap<>();
        }
        prasTable = new HashMap<>(pt);
    }

    public static void setPageSettings(PageSetting[] pageSettings) {
        Map<String, PageFormat> settings = new HashMap<>();
        if (pageSettings != null) {
            for (PageSetting ps : pageSettings) {
                settings.put(ps.getName(), ps.toPageFormat());
            }
        }
        setPRASTable(settings);
    }

    public static PageSetting[] getPageSettings() {
        return prasTable.entrySet().stream().map(e -> new PageSetting(e.getKey(), e.getValue()))
                .toArray(PageSetting[]::new);
    }

    private static PageFormat createPageFormat() {
        return createPageFormat(PageFormat.PORTRAIT);
    }

    private static final float FACTOR = (float) (10.0 / 25.4);

    private static PageFormat createPageFormat(int orientation) {
        PageFormat pf = null;
        if (defaultPageFormat == null) {
            pf = new PageFormat();
            Paper paper = new Paper();
            paper.setSize(ISO.A4.getX(Size2DSyntax.INCH) * DPI, ISO.A4.getY(Size2DSyntax.INCH) * DPI);
            paper.setImageableArea(FACTOR * DPI, FACTOR * DPI, paper.getWidth() - (2 * FACTOR * DPI),
                    paper.getHeight() - (2 * FACTOR * DPI));
            pf.setPaper(paper);
        } else {
            pf = copyPageFormat(defaultPageFormat);
        }

        pf.setOrientation(orientation);
        return pf;
    }

    private static PageFormat copyPageFormat(PageFormat orig) {
        PageFormat pf = new PageFormat();
        Paper paper = new Paper();
        paper.setSize(orig.getWidth(), orig.getHeight());
        paper.setImageableArea(orig.getImageableX(), orig.getImageableY(), orig.getImageableWidth(),
                orig.getImageableHeight());
        pf.setPaper(paper);
        pf.setOrientation(orig.getOrientation());

        return pf;
    }

    private static void setDefaultPageFormat(PageFormat pras) {
        prasTable.clear();
        defaultPageFormat = pras;
    }

    /** Creates new jPageSetup */
    private PageSetup() {
        // never used
    }

    public static boolean show(String jobname) {
        /* Create a print job */
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(psPrintable);
        PrintService printService = getLastUsedPrinter();
        if (printService != null) {
            try {
                pj.setPrintService(printService);
            } catch (PrinterException pe) {
                pe.printStackTrace();
            }
            PageFormat pf = getPageFormat(jobname);
            PageFormat pf2 = pj.pageDialog(pf);
            if (pf != pf2) {
                setPageFormat(jobname, pf2);
                if (jobname == null) {
                    setDefaultPageFormat(getPageFormat(null));
                }
                return true;
            }
        } else {
            DialogUtils.wichtigeMeldung(null, "Kein Drucker gefunden!");
        }
        return false;
    }

    private static final class PageFormatGetter implements Printable {

        private boolean done;
        private Printable printable;
        private String jobname;

        private PageFormatGetter(String jobname, Printable p) {
            this.jobname = jobname;
            this.printable = p;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (!done) {
                setPageFormat(jobname, pageFormat);
                done = true;
            }
            return printable.print(graphics, pageFormat, pageIndex);
        }
    }

    static PrinterJob getPrinterJob(String jobname, Printable p) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(jobname);
        job.setPrintService(getLastUsedPrinter());
        PageFormat pf = getPageFormat(jobname);
        job.setPrintable(new PageFormatGetter(jobname, p), pf);
        return job;
    }

    static PrinterJob printDialog(String jobname, Printable p) throws PrinterException {
        PrinterJob job = getPrinterJob(jobname, p);
        boolean result = job.printDialog();
        if (result) {
            PageSetup.setLastUsedPrinter(job.getPrintService());
            return job;
        }
        return null;
    }

    static void print(PrinterJob job) throws PrinterException {
        if (job.getPrintService() == null) {
            job.setPrintService(getLastUsedPrinter());
        }
        job.print();
    }

    private static void setPageFormat(String jobname, PageFormat pf) {
        if (jobname == null) {
            jobname = "JUtils";
        }
        prasTable.put(jobname, pf);
    }

    private static synchronized PrintService getLastUsedPrinter() {
        /* Create a print job */
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(psPrintable);
        /* locate a print service that can handle the request */
        PrintService[] services = PrinterJob.lookupPrintServices();
        if (services == null) {
            return null;
        }
        if (services.length == 0) {
            return null;
        }
        if (ps == null) {
            try {
                ps = pj.getPrintService();
            } catch (Exception pe) {
                return services[0];
            }
        }
        if (ps == null) {
            return services[0];
        }
        for (PrintService service1 : services) {
            if (ps.equals(service1)) {
                return ps;
            }
        }
        ps = null;
        return services[0];
    }

    private static synchronized void setLastUsedPrinter(PrintService printer) {
        if (printer == null) {
            return;
        }
        ps = printer;
    }

    public static PageFormat getPageFormat(String jobname) {
        if (jobname == null) {
            jobname = "JUtils";
        }
        PageFormat pras = prasTable.get(jobname);
        if (pras == null) {
            if (defaultPageFormat == null) {
                pras = createPageFormat();
                prasTable.put(jobname, pras);
            } else {
                pras = copyPageFormat(defaultPageFormat);
                prasTable.put(jobname, pras);
            }
        }
        return pras;
    }

    static boolean isPortrait(String jobname) {
        return getOrientation(jobname) == PageFormat.PORTRAIT;
    }

    private static int getOrientation(String jobname) {
        return getPageFormat(jobname).getOrientation();
    }

    private static final class PageSetupPrintable implements Printable {
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            return NO_SUCH_PAGE;
        }

        private PageSetupPrintable() {
        }
    }

    public static void setOrientation(String name, int orientation) {
        getPageFormat(name).setOrientation(orientation);
    }
}
