/*
 * Created on 08.12.2005
 */
package de.df.jutils.gui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

public final class EDTUtils {

    private EDTUtils() {
        // Hide
    }

    public static void sleep() {
        executeOnEDT(() -> {
            // Nothing to do
        });
    }

    public static void setVisible(Component w, boolean v) {
        executeOnEDT(new VisibilityRunnable(w, v));
    }

    public static void setFont(Component w, Font v) {
        executeOnEDT(new FontRunnable(w, v));
    }

    public static void setBackground(Component w, Color v) {
        executeOnEDT(new BackgroundRunnable(w, v));
    }

    public static void repaint(Component w) {
        executeOnEDT(new RepaintRunnable(w));
    }

    public static void repaintLater(Component w) {
        SwingUtilities.invokeLater(new RepaintRunnable(w));
    }

    public static int print(Printable p, Graphics g, PageFormat pF, int pageIndex) {
        PrintRunnable pr = new PrintRunnable(p, g, pF, pageIndex);
        executeOnEDT(pr);
        sleep();
        return pr.getResult();
    }

    public static void print(Component c, Graphics g) {
        Print2Runnable pr = new Print2Runnable(c, g);
        executeOnEDT(pr);
        sleep();
    }

    public static void setEnabled(Component c, boolean e) {
        executeOnEDT(new EnablingRunnable(c, e));
    }

    public static void removeAllItems(JComboBox<?> jbc) {
        executeOnEDT(new RemoveAllItemsRunnable(jbc));
    }

    public static <T extends Object> void addItem(JComboBox<T> jbc, T o) {
        executeOnEDT(new AddItemRunnable<T>(jbc, o));
    }

    public static void setSelctedIndex(JComboBox<?> jbc, int i) {
        executeOnEDT(new IndexSelectionRunnable(jbc, i));
    }

    public static void setRowHeight(JTable t, int h) {
        executeOnEDT(new RowHeightRunnable(t, h, -1));
    }

    public static void setRowHeight(JTable t, int index, int h) {
        executeOnEDT(new RowHeightRunnable(t, h, index));
    }

    public static void pack(Window w) {
        executeOnEDT(new PackRunnable(w));
    }

    public static void paint(Component p, Graphics g) {
        executeOnEDT(new PaintRunnable(p, g));
    }

    public static void setPreferredWidth(TableColumn c, int w) {
        executeOnEDT(new ColumnPrefWidthRunnable(c, w));
    }

    public static void setWidth(TableColumn c, int w) {
        executeOnEDT(new ColumnWidthRunnable(c, w));
    }

    public static void setRowSelectionInterval(JTable c, int min, int max) {
        executeOnEDT(new RowSelectionIntervalRunnable(c, min, max));
    }

    /**
     * Give all UI related stuff the chance to be executed before exit
     */
    public static void niceExit() {
        if (SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> {
                System.exit(0);
            });
        } else {
            System.exit(0);
        }
    }

    public static boolean executeOnEDT(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                ExceptionRunnable er = new ExceptionRunnable(r);
                SwingUtilities.invokeAndWait(er);
                if (er.getThrowable() != null) {
                    throw new RuntimeException("An exception occured while in EDT.", er.getThrowable());
                }
            } catch (InterruptedException | InvocationTargetException e) {
                return false;
            }
        }
        return true;
    }

    public static void executeOnEDTAsync(Runnable r) {
        SwingUtilities.invokeLater(r);
    }

    private static final class ExceptionRunnable implements Runnable {

        private Runnable r;
        private Throwable t;

        private ExceptionRunnable(Runnable r) {
            this.r = r;
        }

        @Override
        public void run() {
            try {
                r.run();
            } catch (Exception re) {
                t = re;
            }
        }

        public Throwable getThrowable() {
            return t;
        }
    }

    public static <T extends Object> T executeOnEDTwithReturn(Supplier<T> rr) {
        RunnableWithSupplier<T> rx = new RunnableWithSupplier<>(rr);
        executeOnEDT(rx);
        return rx.getResult();
    }

    private static final class RunnableWithSupplier<T> implements Runnable {

        private final Supplier<T> supplier;
        private T result;

        private RunnableWithSupplier(Supplier<T> rr) {
            supplier = rr;
        }

        @Override
        public void run() {
            result = supplier.get();
        }

        public T getResult() {
            return result;
        }
    }

    private static final class VisibilityRunnable implements Runnable {

        private Component w;
        private boolean v;

        private VisibilityRunnable(Component w, boolean v) {
            this.w = w;
            this.v = v;
        }

        @Override
        public void run() {
            w.setVisible(v);
        }

    }

    private static final class FontRunnable implements Runnable {

        private Component w;
        private Font v;

        private FontRunnable(Component w, Font v) {
            this.w = w;
            this.v = v;
        }

        @Override
        public void run() {
            w.setFont(v);
        }
    }

    private static final class BackgroundRunnable implements Runnable {

        private Component w;
        private Color v;

        private BackgroundRunnable(Component w, Color v) {
            this.w = w;
            this.v = v;
        }

        @Override
        public void run() {
            w.setBackground(v);
        }
    }

    private static final class RepaintRunnable implements Runnable {

        private Component w;

        private RepaintRunnable(Component w) {
            this.w = w;
        }

        @Override
        public void run() {
            w.repaint();
        }
    }

    private static final class EnablingRunnable implements Runnable {

        private Component w;
        private boolean v;

        private EnablingRunnable(Component w, boolean v) {
            this.w = w;
            this.v = v;
        }

        @Override
        public void run() {
            w.setEnabled(v);
        }
    }

    private static final class PackRunnable implements Runnable {

        private Window w;

        private PackRunnable(Window w) {
            this.w = w;
        }

        @Override
        public void run() {
            w.pack();
        }
    }

    private static final class RemoveAllItemsRunnable implements Runnable {

        private JComboBox<?> w;

        private RemoveAllItemsRunnable(JComboBox<?> w) {
            this.w = w;
        }

        @Override
        public void run() {
            w.removeAllItems();
        }
    }

    private static final class AddItemRunnable<T extends Object> implements Runnable {

        private JComboBox<T> w;
        private T o;

        private AddItemRunnable(JComboBox<T> w, T o) {
            this.w = w;
            this.o = o;
        }

        @Override
        public void run() {
            w.addItem(o);
        }
    }

    private static final class IndexSelectionRunnable implements Runnable {

        private JComboBox<?> w;
        private int i;

        private IndexSelectionRunnable(JComboBox<?> w, int i) {
            this.w = w;
            this.i = i;
        }

        @Override
        public void run() {
            w.setSelectedIndex(i);
        }
    }

    private static final class RowHeightRunnable implements Runnable {

        private JTable w;
        private int height;
        private int index;

        private RowHeightRunnable(JTable w, int height, int index) {
            this.w = w;
            this.height = height;
            this.index = index;
        }

        @Override
        public void run() {
            if (index < 0) {
                w.setRowHeight(height);
            } else {
                w.setRowHeight(index, height);
            }
        }
    }

    private static final class PrintRunnable implements Runnable {

        private Printable p;
        private Graphics g;
        private PageFormat pF;
        private int pageIndex;
        private int result;

        private PrintRunnable(Printable p, Graphics g, PageFormat pF, int pageIndex) {
            this.p = p;
            this.g = g;
            this.pF = pF;
            this.pageIndex = pageIndex;
            result = Printable.NO_SUCH_PAGE;
        }

        @Override
        public void run() {
            try {
                result = p.print(g, pF, pageIndex);
            } catch (PrinterException e) {
                // Nothing to do
            }
        }

        public int getResult() {
            return result;
        }
    }

    private static final class PaintRunnable implements Runnable {

        private Graphics g;
        private Component p;

        private PaintRunnable(Component p, Graphics g) {
            this.g = g;
            this.p = p;
        }

        @Override
        public void run() {
            p.paint(g);
        }
    }

    private static final class Print2Runnable implements Runnable {

        private Graphics g;
        private Component p;

        private Print2Runnable(Component p, Graphics g) {
            this.g = g;
            this.p = p;
        }

        @Override
        public void run() {
            p.print(g);
        }
    }

    private static final class ColumnPrefWidthRunnable implements Runnable {

        private TableColumn c;
        private int width;

        private ColumnPrefWidthRunnable(TableColumn c, int w) {
            this.c = c;
            width = w;
        }

        @Override
        public void run() {
            c.setPreferredWidth(width);
        }
    }

    private static final class ColumnWidthRunnable implements Runnable {

        private TableColumn c;
        private int width;

        private ColumnWidthRunnable(TableColumn c, int w) {
            this.c = c;
            width = w;
        }

        @Override
        public void run() {
            c.setWidth(width);
        }
    }

    private static final class RowSelectionIntervalRunnable implements Runnable {

        private JTable t;
        private int min;
        private int max;

        private RowSelectionIntervalRunnable(JTable t, int min, int max) {
            this.t = t;
            this.min = min;
            this.max = max;
        }

        @Override
        public void run() {
            t.setRowSelectionInterval(min, max);
        }
    }

    public static void waitOnEDT() {
        executeOnEDT(() -> {
        });
    }
}
