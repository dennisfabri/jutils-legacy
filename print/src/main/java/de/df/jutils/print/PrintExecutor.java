package de.df.jutils.print;

import java.awt.Image;
import java.awt.Window;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterIOException;
import java.awt.print.PrinterJob;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.df.jutils.gui.JInfiniteProgressDialog;
import de.df.jutils.gui.util.AIconBundle;
import de.df.jutils.gui.util.DialogUtils;
import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.gui.util.ModalFrameUtil;
import de.df.jutils.gui.util.WindowUtils;
import de.df.jutils.i18n.util.JUtilsI18n;
import de.df.jutils.print.api.PrintableCreator;

public class PrintExecutor {

    private PrintExecutor() {
    }

    public static void preview(JFrame parent, PrintableCreator p, String jobname, AIconBundle ib, Image icon) {
        JFrame jd = new JPrintPreview(parent, p, jobname, ib, icon);
        ModalFrameUtil.showAsModal(jd, parent);
    }

    public static void preview(JFrame parent, PrintableCreator p, String jobname, AIconBundle ib, List<Image> icons) {
        JFrame jd = new JPrintPreview(parent, p, jobname, ib, icons);
        ModalFrameUtil.showAsModal(jd, parent);
    }

    public static void print(Printable printable, String jobname, JFrame parent) {
        print(printable, jobname, true, parent);
    }

    public static void print(Printable printable, String jobname, boolean showDialog, JFrame parent) {
        if (printable == null) {
            throw new IllegalArgumentException("printable must not be null!");
        }

        JInfiniteProgressDialog idialog = EDTUtils.executeOnEDTwithReturn(() -> initializeDialog(parent));

        printable = new NotifyingPrintable(printable, new PrintResponder(idialog));

        // wrap in a try/finally so table can be restored even if something
        // fails
        try {
            // EDTUtils.setEnabled(parent, false);

            PrinterJob job = null;

            // display a print dialog
            if (showDialog) {
                job = PageSetup.printDialog(jobname, printable);
                if (job == null) {
                    EDTUtils.setEnabled(parent, true);
                    return;
                }
            } else {
                job = PageSetup.getPrinterJob(jobname, printable);
            }
            // new Print(job, parent, idialog, cb).start();
            PrintQueue.getInstance().print(job);
            EDTUtils.setEnabled(parent, true);
        } catch (PrinterIOException e) {
            DialogUtils.warn(parent, JUtilsI18n.get("de.dm.print.error.io.text"),
                    JUtilsI18n.get("de.dm.print.error.io.note"));
            e.printStackTrace();
            EDTUtils.setEnabled(parent, true);
        } catch (PrinterAbortException e) {
            DialogUtils.warn(parent, JUtilsI18n.get("de.dm.print.error.abort.text"),
                    JUtilsI18n.get("de.dm.print.error.abort.note"));
            e.printStackTrace();
            EDTUtils.setEnabled(parent, true);
        } catch (PrinterException e) {
            DialogUtils.warn(parent, JUtilsI18n.get("de.dm.print.error.general.text"),
                    JUtilsI18n.get("de.dm.print.error.general.note"));
            e.printStackTrace();
            EDTUtils.setEnabled(parent, true);
        }
    }

    private static JInfiniteProgressDialog initializeDialog(JFrame parent) {
        JInfiniteProgressDialog idialog = new JInfiniteProgressDialog(parent, JUtilsI18n.get("de.dm.print.Printing"),
                false) {

            private static final long serialVersionUID = 4541113093908459198L;

            @Override
            public void setVisible(boolean b) {
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w != null) {
                    w.setEnabled(!b);
                }
                super.setVisible(b);
            }
        };
        idialog.setEnabled(false);
        idialog.setText(JUtilsI18n.get("de.dm.print.Printing"));
        idialog.pack();
        idialog.setSize(300, 300);
        WindowUtils.center(idialog);
        return idialog;
    }

    private static class PrintResponder implements NotifyingPrintable.PrintListener {

        JInfiniteProgressDialog dialog = null;

        public PrintResponder(JInfiniteProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void printingPage(int index) {
            // Nothing to do
        }

        @Override
        public void finishedPage(int index, int result) {
            if (result == Printable.PAGE_EXISTS) {
                EDTUtils.executeOnEDT(new TextRunnable(index));
            }
        }

        private class TextRunnable implements Runnable {

            private int index;

            public TextRunnable(int i) {
                index = i;
            }

            @Override
            public void run() {
                dialog.setText(JUtilsI18n.get("de.dm.print.PrintingPageNr", (index + 1)));
            }
        }
    }
}
