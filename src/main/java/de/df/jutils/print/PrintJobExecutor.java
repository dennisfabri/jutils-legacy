/**
 * 
 */
package de.df.jutils.print;

import java.awt.print.*;

import javax.swing.JFrame;

import de.df.jutils.gui.JInfiniteProgressDialog;
import de.df.jutils.gui.util.*;
import de.df.jutils.i18n.util.JUtilsI18n;

public class PrintJobExecutor extends Thread {

    private PrinterJob              job;
    private JFrame                  parent;
    JInfiniteProgressDialog         dialog;
    private ISimpleCallback<Object> callback;

    public PrintJobExecutor(PrinterJob job, JFrame parent, JInfiniteProgressDialog dialog, ISimpleCallback<Object> cb) {
        setName("PrintManager.print");
        setPriority(Thread.NORM_PRIORITY);
        setDaemon(true);

        this.job = job;
        this.parent = parent;
        this.dialog = dialog;
        callback = cb;
    }

    @Override
    public void run() {
        EDTUtils.executeOnEDT(new Runnable() {
            @Override
            public void run() {
                dialog.setVisible(true);
                dialog.startProgress();
            }
        });
        try {
            // do the printing (may need to handle PrinterException)
            PageSetup.print(job);
        } catch (PrinterIOException e) {
            DialogUtils.warn(parent, JUtilsI18n.get("de.dm.print.error.io.text"), JUtilsI18n.get("de.dm.print.error.io.note"));
            e.printStackTrace();
        } catch (PrinterAbortException e) {
            DialogUtils.warn(parent, JUtilsI18n.get("de.dm.print.error.abort.text"), JUtilsI18n.get("de.dm.print.error.abort.note"));
            e.printStackTrace();
        } catch (PrinterException e) {
            DialogUtils.warn(parent, JUtilsI18n.get("de.dm.print.error.general.text"), JUtilsI18n.get("de.dm.print.error.general.note"));
            e.printStackTrace();
        }
        EDTUtils.setEnabled(parent, true);
        EDTUtils.setVisible(dialog, false);
        if (callback != null) {
            callback.callback(null);
        }
    }
}