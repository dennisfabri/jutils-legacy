package de.df.jutils.print;

import java.awt.print.*;
import java.util.LinkedList;

import de.df.jutils.i18n.util.JUtilsI18n;

public class PrintQueue {

    private static PrintQueue instance = null;

    public static PrintQueue getInstance() {
        synchronized (PrintQueue.class) {
            if (instance == null) {
                instance = new PrintQueue();
            }
        }
        return instance;
    }

    private LinkedList<PrintQueueElement> jobs = new LinkedList<PrintQueueElement>();
    private JobPrinter                    printer;
    private LinkedList<IPrintCallback>    ipcs = new LinkedList<IPrintCallback>();

    void runPrinter(JobPrinter parent) {
        while (true) {
            PrintQueueElement pqe = null;
            synchronized (PrintQueue.class) {
                if (!jobs.isEmpty()) {
                    pqe = jobs.removeFirst();
                }
                if (pqe == null) {
                    synchronized (this) {
                        printer = null;
                        notifyNoJobs();
                        parent.setCurrentJob(null);
                    }
                    return;
                }
            }
            PrinterJob current = pqe.getJob();
            parent.setCurrentJob(current);
            notifyJobStart(current.getJobName());
            pqe.print();
            notifyJobEnd(current.getJobName());
        }
    }

    private final class JobPrinter extends Thread {

        private PrinterJob current = null;

        public JobPrinter() {
            setName("PrintQueue.JobPrinter");
            setDaemon(true);
        }

        public PrinterJob getCurrentJob() {
            return current;
        }

        public void setCurrentJob(PrinterJob c) {
            current = c;
        }

        @Override
        public void run() {
            runPrinter(this);
        }

    }

    public static interface IPrintCallback {
        void jobStarted(String job, int jobs);

        void jobAdded(String job, int jobs);

        void jobFinished(String job, int jobs);

        void jobError(String job, int jobs, String title, String message, String note);

        void queueEmpty();
    }

    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    public static abstract class APrintCallback implements IPrintCallback {
        @Override
        public void jobStarted(String job, int jobs) {
            // Nothing to do
        }

        @Override
        public void jobAdded(String job, int jobs) {
            // Nothing to do
        }

        @Override
        public void jobFinished(String job, int jobs) {
            // Nothing to do
        }

        @Override
        public void jobError(String job, int jobs, String title, String message, String note) {
            // Nothing to do
        }

        @Override
        public void queueEmpty() {
            // Nothing to do
        }
    }

    public void addPrintCallback(IPrintCallback ipc) {
        synchronized (ipcs) {
            if (ipcs.contains(ipc)) {
                return;
            }
            ipcs.addLast(ipc);
        }
    }

    public boolean removePrintCallback(IPrintCallback ipc) {
        synchronized (ipcs) {
            return ipcs.remove(ipc);
        }
    }

    private PrintQueue() {
        // Nothing to do
    }

    public void print(PrinterJob job) {
        synchronized (this) {
            jobs.addLast(new PrintQueueElement(job));
            if (printer == null) {
                printer = new JobPrinter();
                printer.start();
            } else {
                notifyJobAdded(printer.getCurrentJob().getJobName());
            }
        }
    }

    void notifyNoJobs() {
        synchronized (ipcs) {
            for (IPrintCallback ipc : ipcs) {
                try {
                    ipc.queueEmpty();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void notifyJobStart(String job) {
        int size = 0;
        synchronized (this) {
            size = jobs.size();
        }
        synchronized (ipcs) {
            for (IPrintCallback ipc : ipcs) {
                try {
                    ipc.jobStarted(job, size);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void notifyJobAdded(String job) {
        int size = 0;
        synchronized (this) {
            size = jobs.size();
        }
        synchronized (ipcs) {
            for (IPrintCallback ipc : ipcs) {
                try {
                    ipc.jobAdded(job, size);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void notifyJobEnd(String job) {
        int size = 0;
        synchronized (this) {
            size = jobs.size();
        }
        synchronized (ipcs) {
            for (IPrintCallback ipc : ipcs) {
                try {
                    ipc.jobFinished(job, size);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void notifyJobError(String job, String title, String message, String note) {
        int size = 0;
        synchronized (this) {
            size = jobs.size();
        }
        synchronized (ipcs) {
            for (IPrintCallback ipc : ipcs) {
                try {
                    ipc.jobError(job, size, title, message, note);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class PrintQueueElement {

        private final PrinterJob job;

        public PrintQueueElement(PrinterJob job) {
            this.job = job;
        }

        public void print() {
            try {
                PageSetup.print(job);
            } catch (PrinterIOException e) {
                notifyJobError(job.getJobName(), JUtilsI18n.get("de.dm.print.error.io.title"), JUtilsI18n.get("de.dm.print.error.io.text", e.getMessage()),
                        JUtilsI18n.get("de.dm.print.error.io.note", e.getMessage()));
                e.printStackTrace();
            } catch (PrinterAbortException e) {
                notifyJobError(job.getJobName(), JUtilsI18n.get("de.dm.print.error.abort.title"),
                        JUtilsI18n.get("de.dm.print.error.abort.text", e.getMessage()), JUtilsI18n.get("de.dm.print.error.abort.note", e.getMessage()));
                e.printStackTrace();
            } catch (PrinterException e) {
                notifyJobError(job.getJobName(), JUtilsI18n.get("de.dm.print.error.general.title"),
                        JUtilsI18n.get("de.dm.print.error.general.text", e.getMessage()), JUtilsI18n.get("de.dm.print.error.general.note", e.getMessage()));
                e.printStackTrace();
            }
        }

        public PrinterJob getJob() {
            return job;
        }
    }
}