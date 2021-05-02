package de.df.jutils.print;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterIOException;
import java.awt.print.PrinterJob;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import de.df.jutils.gui.JInfiniteProgressDialog;
import de.df.jutils.gui.jtable.ExtendedTableModel;
import de.df.jutils.gui.jtable.JPrintTable;
import de.df.jutils.gui.util.AIconBundle;
import de.df.jutils.gui.util.DialogUtils;
import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.gui.util.ModalFrameUtil;
import de.df.jutils.gui.util.WindowUtils;
import de.df.jutils.i18n.util.JUtilsI18n;
import de.df.jutils.util.StringTools;

/**
 * @author Dennis Mueller
 * @date 17.10.2004
 */
public final class PrintManager {

	private static MessageFormat centerFooter;
	private static MessageFormat rightFooter;
	private static MessageFormat leftFooter;
	private static MessageFormat rightHeader;
	private static MessageFormat leftHeader;

	private static Font font;

	private static String name = "";
	private static String location = "";
	private static String date = "";

	private static BufferedImage adsPTop = null;
	private static BufferedImage adsPBottom = null;
	private static BufferedImage adsLTop = null;
	private static BufferedImage adsLBottom = null;
	private static String[] adsJobs = new String[0];

	static {
		leftFooter = new MessageFormat("");
		centerFooter = new MessageFormat("");
		rightFooter = new MessageFormat("");

		rightHeader = new MessageFormat("");
		leftHeader = new MessageFormat("");

		font = getDefaultFont();
	}

	private static String fontlog = null;

	public static Font getDefaultFont() {
	    StringBuilder log = new StringBuilder();

		Font defaultfont1 = null;
		Font defaultfont2 = null;
		Font defaultfont3 = null;

		Font dialog = null;

		// Try different fonts
		// 1. DLRG-Font
		// 2. DLRG-Jugend-Font
		// 3. OS-StandardFonts
		// 4. Dialog
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (int x = 0; x < fonts.length; x++) {
			String fontname = fonts[x].getName();

			if (fontname.equals("DLRG Univers 55 Roman")) {
				log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
				defaultfont1 = fonts[x];
			}
			if (fontname.equals("DLRG-Jugend Text")) {
				log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
				defaultfont2 = fonts[x];
			}
			if (fontname.equals("Arial")) {
				log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
				defaultfont3 = fonts[x];
			}
			if (fontname.equals("Tahoma")) {
				log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
				defaultfont3 = fonts[x];
			}
			if (fontname.equals("Helvetica")) {
				log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
				defaultfont3 = fonts[x];
			}
			if (fontname.equals("Lucida")) {
				log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
				defaultfont3 = fonts[x];
			}
			if (fontname.equals("Dialog")) {
				log.append("Found ").append(fontname).append(" at index ").append(x).append("\n");
				dialog = fonts[x];
			}
		}

		log.append("defaultfont1: ").append(defaultfont1).append("\n");
		log.append("defaultfont2: ").append(defaultfont2).append("\n");
		log.append("defaultfont3: ").append(defaultfont3).append("\n");

		if (defaultfont1 == null) {
			defaultfont1 = defaultfont2;
		}
		if (defaultfont1 == null) {
			defaultfont1 = defaultfont3;
		}

		log.append("Current default: ").append(defaultfont1).append("\n");

		// Try Window default font
		if (defaultfont1 == null) {
			JFrame f = new JFrame();
			f.pack();
			defaultfont1 = f.getFont();
			f.dispose();
			log.append("JFrame: ").append(defaultfont1).append("\n");
		}

		// Choose first one
		if (defaultfont1 == null) {
			defaultfont1 = fonts[0];
			log.append("fonts[0]: ").append(defaultfont1).append("\n");
		}

		// If nothing helps use DIALOG.
		if (defaultfont1 == null) {
			defaultfont1 = dialog;
			log.append("dialog: ").append(defaultfont1).append("\n");
		}

		if (defaultfont1 != null) {
			defaultfont1 = defaultfont1.deriveFont(Font.PLAIN, 10);
			log.append("Derived Font: ").append(defaultfont1).append("\n");
		}
		log.append("Result: ").append(defaultfont1).append("\n");

		if (fontlog == null) {
			fontlog = log.toString();
		}
		return defaultfont1;
	}

	public static String getDefaultFontLog() {
		if (fontlog == null) {
			return "No log";
		}
		return fontlog;
	}

	public static void registerAds(BufferedImage iptop, BufferedImage ipbottom, BufferedImage iltop,
			BufferedImage ilbottom, String... jobs) {
		adsJobs = jobs;
		adsPTop = iptop;
		adsPBottom = ipbottom;
		adsLTop = iltop;
		adsLBottom = ilbottom;
	}

	private PrintManager() {
		// Never used
	}

	public static void setHeaderMessages(MessageFormat left, MessageFormat right) {
		if (left == null) {
			left = new MessageFormat("");
		}
		if (right == null) {
			right = new MessageFormat("");
		}
		leftHeader = left;
		rightHeader = right;
	}

	public static void setFooterMessages(MessageFormat left, MessageFormat center, MessageFormat right) {
		if (left == null) {
			left = new MessageFormat("");
		}
		if (center == null) {
			center = new MessageFormat("");
		}
		if (right == null) {
			right = new MessageFormat("");
		}
		leftFooter = left;
		centerFooter = center;
		rightFooter = right;
	}

	public static MessageFormat getFooterMessage(int align) {
		switch (align) {
		case HeaderFooterPrintable.CENTER:
			return centerFooter;
		case HeaderFooterPrintable.LEFT:
			return leftFooter;
		case HeaderFooterPrintable.RIGHT:
			return rightFooter;
		default:
			throw new IndexOutOfBoundsException();

		}
	}

	public static MessageFormat getHeaderMessage(int align) {
		switch (align) {
		case HeaderFooterPrintable.CENTER:
			throw new IllegalArgumentException("Value HeaderFooterPrintable.CENTER not allowed!");
		// return centerHeader;
		case HeaderFooterPrintable.LEFT:
			return leftHeader;
		case HeaderFooterPrintable.RIGHT:
			return rightHeader;
		default:
			throw new IndexOutOfBoundsException();

		}
	}

	public static Printable getHeaderPrintable(Printable source, String header) {
		if (header == null) {
			return source;
		}
		return getHeaderPrintable(source, new MessageFormat(header));
	}

	public static Printable getHeaderPrintable(Printable source, MessageFormat header) {
		if (header == null) {
			return source;
		}
		return new HeaderFooterPrintable(source, header, null, font);
	}

	public static void setFont(Font f) {
		if (f == null) {
			throw new NullPointerException("Font must not be null");
		}
		font = f;
	}

	public static Font getFont() {
		if (font == null) {
			font = getDefaultFont();
		}
		return font;
	}

	public static Printable getPrintable(JTable table, String title, int optimize, boolean shrink, boolean enlarge) {
		GetPrintable gp = new GetPrintable(table, title, optimize, shrink, enlarge);
		EDTUtils.executeOnEDT(gp);
		return gp.printable;
	}

	public static Printable getPrintable(JTable table, Component title, int optimize, boolean shrink, boolean enlarge) {
		GetPrintable gp = new GetPrintable(table, null, optimize, shrink, enlarge);
		EDTUtils.executeOnEDT(gp);
		ExtendedHeaderFooterPrintable ehfp = new ExtendedHeaderFooterPrintable(gp.printable, title, null,
				PrintManager.getFont());
		return ehfp;
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

	private static class GetPrintable implements Runnable {

		volatile Printable printable;

		private JTable table;
		private int optimize;
		private boolean shrink;
		private boolean enlarge;
		private String title;

		public GetPrintable(JTable t, String ti, int o, boolean s, boolean e) {
			table = t;
			title = ti;
			optimize = o;
			shrink = s;
			enlarge = e;
		}

		@Override
		public void run() {
			printable = getHeaderPrintable(
					new JTablePrintable(table, optimize, false,
							(shrink ? JTable.PrintMode.FIT_WIDTH : JTable.PrintMode.NORMAL), enlarge, getFont()),
					title);
		}
	}

	public static Printable getPrintable(TableModel tm, String title, int optimize, boolean shrink, boolean enlarge) {
		return new MultiplePrintable(getPrintableI(tm, title, optimize, shrink, enlarge));
	}

	private static Printable getPrintableI(TableModel tm, String title, int optimize, boolean shrink, boolean enlarge) {
		return getPrintable(JPrintTable.createTable(tm), title, optimize, shrink, enlarge);
	}

	public static Printable getPrintable(TableModel[] tm, String[] title, int optimize, boolean shrink,
			boolean enlarge) {
		MultiplePrintable mp = new MultiplePrintable();
		for (int x = 0; x < tm.length; x++) {
			mp.add(getPrintableI(tm[x], title[x], optimize, shrink, enlarge));
		}
		return mp;
	}

	public static Printable getPrintable(ExtendedTableModel[] tm, int optimize, boolean shrink, boolean enlarge) {
		MultiplePrintable mp = new MultiplePrintable();
		for (ExtendedTableModel aTm : tm) {
			mp.add(getPrintableI(aTm, aTm.getName(), optimize, shrink, enlarge));
		}
		return mp;
	}

	public static Printable getPrintable(JTable[] tm, String[] title, int optimize, boolean shrink, boolean enlarge) {
		MultiplePrintable mp = new MultiplePrintable();
		for (int x = 0; x < tm.length; x++) {
			mp.add(getPrintable(tm[x], title[x], optimize, shrink, enlarge));
		}
		return mp;
	}

	public static Printable getPrintable(JTable[] tm, Component[] title, int optimize, boolean shrink,
			boolean enlarge) {
		MultiplePrintable mp = new MultiplePrintable();
		for (int x = 0; x < tm.length; x++) {
			mp.add(getPrintable(tm[x], title[x], optimize, shrink, enlarge));
		}
		return mp;
	}

	public static Printable getPrintable(JTable[] tm, String title, int optimize, boolean shrink, boolean enlarge) {
		MultiplePrintable mp = new MultiplePrintable();
		for (JTable aTm : tm) {
			mp.add(getPrintable(aTm, title, optimize, shrink, enlarge));
		}
		return mp;
	}

	public static Printable[] getPrintables(JTable[] tm, String[] title, int optimize, boolean shrink,
			boolean enlarge) {
		Printable[] ps = new Printable[tm.length];
		for (int x = 0; x < tm.length; x++) {
			ps[x] = getPrintable(tm[x], title[x], optimize, shrink, enlarge);
		}
		return ps;
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

	public static Printable getFinalPrintable(Printable printable, Date currentDate, String header, String jobname) {
		return getFinalPrintable(printable, currentDate, header == null ? null : new MessageFormat(header), jobname);
	}

	public static Printable getFinalPrintable(Printable printable, Date currentDate, boolean header, String jobname) {
		return getFinalPrintable(printable, currentDate, header ? new MessageFormat("") : null, jobname);
	}

	public static Printable getFinalPrintable(Printable printable, Date currentDate, MessageFormat header,
			String jobname) {
		HeaderFooterPrintable hfp = new HeaderFooterPrintable(printable, null, null, font);
		hfp.addDynamic(name);
		hfp.addDynamic(location);
		hfp.addDynamic(date);
		if (header != null) {
			hfp.setHeader(header, HeaderFooterPrintable.CENTER);
			hfp.setHeader(leftHeader, HeaderFooterPrintable.LEFT);
			hfp.setHeader(rightHeader, HeaderFooterPrintable.RIGHT);
		}
		if (currentDate != null) {
			hfp.addDynamic(DateFormat.getDateInstance().format(currentDate));
			hfp.addDynamic(DateFormat.getTimeInstance().format(currentDate));
			hfp.setFooter(leftFooter, HeaderFooterPrintable.LEFT);
		}
		hfp.setFooter(centerFooter, HeaderFooterPrintable.CENTER);
		hfp.setFooter(rightFooter, HeaderFooterPrintable.RIGHT);

		for (String job : adsJobs) {
			if (job.equals(jobname)) {
				return new BannerPrintable(hfp, adsPTop, adsPBottom, adsLTop, adsLBottom);
			}
		}
		return hfp;
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

	public static JLabel getPrintLabel(String text) {
		JLabel l = new JLabel(text);
		l.setFont(getFont());
		return l;
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

	public static String getDate() {
		return date;
	}

	public static void setDatum(String datum) {
		PrintManager.date = StringTools.firstLine(datum);
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name, String shortname) {
		shortname = StringTools.firstLine(shortname);
		if (shortname != null && shortname.trim().length() > 0) {
			PrintManager.name = shortname;
		} else {
			PrintManager.name = StringTools.firstLine(name);
		}
	}

	public static String getLocation() {
		return location;
	}

	public static void setOrt(String ort) {
		PrintManager.location = StringTools.firstLine(ort);
	}
}