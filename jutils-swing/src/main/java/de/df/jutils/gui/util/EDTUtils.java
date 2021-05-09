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
		executeOnEDT(new Runnable() {
			@Override
			public void run() {
				// Nothing to do
			}
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
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					System.exit(0);
				}
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
			} catch (InterruptedException e) {
				return false;
			} catch (InvocationTargetException e) {
				return false;
			}
		}
		return true;
	}

	public static void executeOnEDTAsync(Runnable r) {
		SwingUtilities.invokeLater(r);
	}

	private static class ExceptionRunnable implements Runnable {

		private Runnable r = null;
		private Throwable t = null;

		public ExceptionRunnable(Runnable r) {
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

	private static class RunnableWithSupplier<T> implements Runnable {

		private final Supplier<T> supplier;
		private T result;

		public RunnableWithSupplier(Supplier<T> rr) {
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

	private static class VisibilityRunnable implements Runnable {

		private Component w;
		private boolean v;

		public VisibilityRunnable(Component w, boolean v) {
			this.w = w;
			this.v = v;
		}

		@Override
		public void run() {
			w.setVisible(v);
		}

	}

	private static class FontRunnable implements Runnable {

		private Component w;
		private Font v;

		public FontRunnable(Component w, Font v) {
			this.w = w;
			this.v = v;
		}

		@Override
		public void run() {
			w.setFont(v);
		}
	}

	private static class BackgroundRunnable implements Runnable {

		private Component w;
		private Color v;

		public BackgroundRunnable(Component w, Color v) {
			this.w = w;
			this.v = v;
		}

		@Override
		public void run() {
			w.setBackground(v);
		}
	}

	private static class RepaintRunnable implements Runnable {

		private Component w;

		public RepaintRunnable(Component w) {
			this.w = w;
		}

		@Override
		public void run() {
			w.repaint();
		}
	}

	private static class EnablingRunnable implements Runnable {

		private Component w;
		private boolean v;

		public EnablingRunnable(Component w, boolean v) {
			this.w = w;
			this.v = v;
		}

		@Override
		public void run() {
			w.setEnabled(v);
		}
	}

	private static class PackRunnable implements Runnable {

		private Window w;

		public PackRunnable(Window w) {
			this.w = w;
		}

		@Override
		public void run() {
			w.pack();
		}
	}

	private static class RemoveAllItemsRunnable implements Runnable {

		private JComboBox<?> w;

		public RemoveAllItemsRunnable(JComboBox<?> w) {
			this.w = w;
		}

		@Override
		public void run() {
			w.removeAllItems();
		}
	}

	private static class AddItemRunnable<T extends Object> implements Runnable {

		private JComboBox<T> w;
		private T o;

		public AddItemRunnable(JComboBox<T> w, T o) {
			this.w = w;
			this.o = o;
		}

		@Override
		public void run() {
			w.addItem(o);
		}
	}

	private static class IndexSelectionRunnable implements Runnable {

		private JComboBox<?> w;
		private int i;

		public IndexSelectionRunnable(JComboBox<?> w, int i) {
			this.w = w;
			this.i = i;
		}

		@Override
		public void run() {
			w.setSelectedIndex(i);
		}
	}

	private static class RowHeightRunnable implements Runnable {

		private JTable w;
		private int height;
		private int index;

		public RowHeightRunnable(JTable w, int height, int index) {
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

	private static class PrintRunnable implements Runnable {

		private Printable p;
		private Graphics g;
		private PageFormat pF;
		private int pageIndex;
		private int result;

		public PrintRunnable(Printable p, Graphics g, PageFormat pF, int pageIndex) {
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

	private static class PaintRunnable implements Runnable {

		private Graphics g;
		private Component p;

		public PaintRunnable(Component p, Graphics g) {
			this.g = g;
			this.p = p;
		}

		@Override
		public void run() {
			p.paint(g);
		}
	}

	private static class Print2Runnable implements Runnable {

		private Graphics g;
		private Component p;

		public Print2Runnable(Component p, Graphics g) {
			this.g = g;
			this.p = p;
		}

		@Override
		public void run() {
			p.print(g);
		}
	}

	private static class ColumnPrefWidthRunnable implements Runnable {

		private TableColumn c;
		private int width;

		public ColumnPrefWidthRunnable(TableColumn c, int w) {
			this.c = c;
			width = w;
		}

		@Override
		public void run() {
			c.setPreferredWidth(width);
		}
	}

	private static class ColumnWidthRunnable implements Runnable {

		private TableColumn c;
		private int width;

		public ColumnWidthRunnable(TableColumn c, int w) {
			this.c = c;
			width = w;
		}

		@Override
		public void run() {
			c.setWidth(width);
		}
	}

	private static class RowSelectionIntervalRunnable implements Runnable {

		private JTable t;
		private int min;
		private int max;

		public RowSelectionIntervalRunnable(JTable t, int min, int max) {
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
		executeOnEDT(new Runnable() {
			@Override
			public void run() {
			}
		});
	}
}