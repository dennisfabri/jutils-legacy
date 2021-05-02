/*
 * Created on 17.12.2004
 */
package de.df.jutils.print;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lisasp.swing.filechooser.FileChooserUtils;
import org.lisasp.swing.filechooser.filefilter.FileFilterPDF;

import de.df.jutils.gui.JInfiniteProgressFrame;
import de.df.jutils.gui.JIntSpinner;
import de.df.jutils.gui.border.ShadowBorder;
import de.df.jutils.gui.util.AIconBundle;
import de.df.jutils.gui.util.DialogUtils;
import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.gui.util.InfiniteProgressUtils;
import de.df.jutils.gui.util.UIStateUtils;
import de.df.jutils.i18n.util.JUtilsI18n;
import de.df.jutils.io.PdfOutput;
import de.df.jutils.print.PrintQueue.APrintCallback;
import de.df.jutils.util.Feedback;
import net.java.GraphicsUtilities;

/**
 * @author Dennis Mueller
 */
public class JPrintPreview extends JInfiniteProgressFrame {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3258129163256084278L;

    private static final int  IMAGE_TYPE       = BufferedImage.TYPE_BYTE_GRAY;

    private Window            parent;
    PageContainer             pages1;
    PageContainer             pages2;

    boolean                   render;
    boolean                   pause;
    boolean                   stopped;

    private ImageIcon         imageIcon;
    private JLabel            image;
    JPanel                    imagePanel;
    JScrollPane               imagePanelScroller;

    private JButton           print;
    private JButton           setup;
    private JButton           save;
    JButton                   next;
    JButton                   previous;
    JComboBox<String>         pageNumber;
    // JComboBox size;
    JIntSpinner               size;

    int                       percent;
    String                    name;
    PrintableCreator          printable;
    JLabel                    maxpage;

    JLabel                    status;
    int                       statuspos        = 0;
    Icon[]                    statusicons      = null;

    PrintCallback             printcallback;

    public JPrintPreview(Window parent, PrintableCreator p, String jobname, AIconBundle icons, Image titleicon) {
        this(parent, p, jobname, icons, (List<Image>) null);
        setIconImage(titleicon);
    }

    public JPrintPreview(Window parent, PrintableCreator p, String jobname, AIconBundle icons, List<Image> titleicons) {
        super(JUtilsI18n.get("de.dm.print.PrintPreview"));
        if (p == null) {
            throw new NullPointerException();
        }
        if ((titleicons != null) && (!titleicons.isEmpty())) {
            setIconImages(titleicons);
        }

        name = jobname;
        printable = p;
        pages1 = new PageContainer();
        pages2 = new PageContainer();

        render = true;
        pause = false;
        stopped = true;

        imageIcon = new ImageIcon();
        image = new JLabel();

        print = new JButton(JUtilsI18n.get("de.dm.print.Print"), icons.getSmallIcon("print"));
        setup = new JButton(JUtilsI18n.get("de.dm.print.Pagesetup"), icons.getSmallIcon("pagesetup"));
        save = new JButton(JUtilsI18n.get("de.dm.print.Save"), icons.getSmallIcon("savefile"));
        next = new JButton(icons.getSmallIcon("next"));
        next.setEnabled(false);
        previous = new JButton(icons.getSmallIcon("previous"));
        previous.setEnabled(false);
        pageNumber = new JComboBox<String>();
        pageNumber.setMaximumRowCount(20);
        pageNumber.setEnabled(false);
        // size = new JComboBox(getSizes());
        size = new JIntSpinner(100, 10, 400, 25);
        size.setEditable(true);

        status = new JLabel();
        status.setToolTipText(JUtilsI18n.get("de.dm.print.AllPagesReady"));
        statusicons = new Icon[3];
        statusicons[0] = icons.getSmallIcon("previewstatus1");
        statusicons[1] = icons.getSmallIcon("previewstatus2");
        statusicons[2] = icons.getSmallIcon("previewstatus3");

        maxpage = new JLabel();

        percent = 100;
        init(parent);

        printcallback = new PrintCallback();
        PrintQueue.getInstance().addPrintCallback(printcallback);
    }

    /**
     * @param parent
     * @param p
     */
    private void init(Window c) {
        parent = c;
        if (parent != null) {
            parent.setEnabled(false);
        }

        setAnimated(true);

        try {
            new PrintableToImages(printable.create()).start();
        } catch (RuntimeException e) {
            e.printStackTrace();
            render = false;
            pause = false;
            stopped = true;
        }

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                exit();
            }
        });

        image.setBorder(new ShadowBorder());
        imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        imagePanel.add(image);
        imagePanel.addMouseWheelListener(new PreviewMouseWheelListener());

        imagePanelScroller = new JScrollPane(imagePanel);
        imagePanelScroller.getVerticalScrollBar().setUnitIncrement(20);
        imagePanelScroller.getHorizontalScrollBar().setUnitIncrement(20);
        add(imagePanelScroller, BorderLayout.CENTER);
        addButtons();

        setLocation(20, 20);
        setSize(800, 600);
        UIStateUtils.uistatemanage(this, JPrintPreview.class.getName());

        addActions();
    }

    Printable createPrintable() {
        return printable.create();
    }

    private void addButtons() {
        JToolBar panel = new JToolBar();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setFloatable(false);

        print.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PrintManager.print(createPrintable(), name, true, JPrintPreview.this);
                    }
                }).start();
            }
        });
        setup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                doSetup();
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                doSave();
            }
        });
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                nextImage();
            }
        });
        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                previousImage();
            }
        });
        pageNumber.addActionListener(new PageActionListener());

        maxpage.setText(JUtilsI18n.get("de.dm.print.ofpages", "?"));

        size.setMinimumSize(new Dimension(100, 10));
        size.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int p = size.getInt();
                if (p != percent) {
                    percent = p;
                    updateImage();
                }
            }
        });

        panel.add(print);
        panel.add(setup);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(save);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(previous);
        panel.add(new JLabel(JUtilsI18n.get("de.dm.print.Page")));
        panel.add(pageNumber);
        panel.add(maxpage);
        panel.add(next);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JLabel(JUtilsI18n.get("de.dm.print.Zoom")));
        panel.add(size);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(status);

        add(panel, BorderLayout.NORTH);
    }

    protected void pauseRendering(boolean p) {
        pause = p;
    }

    protected void updateButtons() {
        while (pageNumber.getItemCount() < pages1.size()) {
            EDTUtils.addItem(pageNumber, "" + (pageNumber.getItemCount() + 1));
        }
        if (pageNumber.getSelectedIndex() < 0) {
            try {
                EDTUtils.setSelctedIndex(pageNumber, 0);
            } catch (RuntimeException re) {
                // Nothing to do
            }
        }
        EDTUtils.setEnabled(previous, pageNumber.getSelectedIndex() > 0);
        EDTUtils.setEnabled(next, pageNumber.getSelectedIndex() + 1 < pageNumber.getItemCount());
        EDTUtils.setEnabled(pageNumber, pageNumber.getItemCount() > 1);
    }

    @Override
    public void setVisible(boolean b) {
        parent.setEnabled(!b);
        super.setVisible(b);
        if (!b) {
            pages1.clear();
            pages2.clear();
        }
    }

    private final class PrintCallback extends APrintCallback {
        @Override
        public void queueEmpty() {
            // EDTUtils.setEnabled(JPrintPreview.this, true);
        }

        @Override
        public void jobFinished(String job, int jobs) {
            if (jobs == 0) {
                EDTUtils.setEnabled(JPrintPreview.this, true);
            }
        }
    }

    private final class PreviewMouseWheelListener implements MouseWheelListener {

        public PreviewMouseWheelListener() {
            // Nothin to do
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            Dimension d1 = imagePanelScroller.getSize();
            Dimension d2 = imagePanel.getSize();
            if ((d1.getWidth() >= d2.getWidth()) && (d1.getHeight() >= d2.getHeight())) {
                if (mwe.getWheelRotation() > 0) {
                    nextImage();
                } else {
                    previousImage();
                }
            } else {
                JScrollBar bar = imagePanelScroller.getVerticalScrollBar();
                if (mwe.getWheelRotation() > 0) {
                    if (bar.getMaximum() <= bar.getValue() + bar.getModel().getExtent()) {
                        if (nextImage()) {
                            bar.setValue(bar.getMinimum());
                        }
                    } else {
                        bar.setValue(Math.min(bar.getMaximum(), bar.getValue() + bar.getUnitIncrement() * mwe.getUnitsToScroll()));
                    }
                } else {
                    if (bar.getMinimum() == bar.getValue()) {
                        if (previousImage()) {
                            bar.setValue(bar.getMaximum());
                        }
                    } else {
                        bar.setValue(Math.min(bar.getMinimum(), bar.getValue() + bar.getUnitIncrement() * mwe.getUnitsToScroll()));
                    }
                }
            }
        }
    }

    private final class SaveSwingWorker extends SwingWorker<Boolean, Object> {

        private String filename;

        public SaveSwingWorker(String n) {
            filename = n;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            Feedback fb = new Feedback() {
                @Override
                public void showFeedback(String text) {
                    InfiniteProgressUtils.setTextAsync(JPrintPreview.this, text);
                }
            };
            return PdfOutput.write(filename, createPrintable(), PageSetup.isPortrait(name), fb);
        }

        @Override
        protected void done() {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            setEnabled(true);
            pauseRendering(false);
        }
    }

    /**
     * @param p
     */
    private class PrintableToImages extends Thread {

        private Printable printer;

        public PrintableToImages(Printable p) {
            if (p == null) {
                throw new NullPointerException();
            }
            setName("JPrintPreview$PrintableToImages");
            setPriority(Thread.NORM_PRIORITY);
            setDaemon(true);
            this.printer = p;
        }

        @Override
        public void run() {
            render = true;
            pause = false;
            stopped = false;

            PageFormat pF = PageSetup.getPageFormat(name);
            int pageIndex = 0;
            while (render) {
                setStatus(true);
                try {
                    BufferedImage img1 = new BufferedImage((int) pF.getWidth(), (int) pF.getHeight(), IMAGE_TYPE);
                    Graphics2D g1 = (Graphics2D) img1.getGraphics();
                    // g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    // RenderingHints.VALUE_ANTIALIAS_ON);
                    g1.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g1.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g1.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g1.setColor(Color.WHITE);
                    g1.fillRect(0, 0, (int) pF.getWidth(), (int) pF.getHeight());
                    g1.setColor(Color.BLACK);
                    int result = printer.print(g1, pF, pageIndex);
                    if (result != Printable.PAGE_EXISTS) {
                        render = false;
                    } else {
                        // Print with double factor to be able to resize
                        // to a certain factor
                        BufferedImage img2 = new BufferedImage((int) pF.getWidth() * 2, (int) pF.getHeight() * 2, IMAGE_TYPE);
                        Graphics2D g2 = (Graphics2D) img2.getGraphics();
                        g2.scale(2, 2);
                        // g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        // RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                        g2.setColor(Color.WHITE);
                        g2.fillRect(0, 0, (int) pF.getWidth(), (int) pF.getHeight());
                        g2.setColor(Color.BLACK);
                        int printResult = printer.print(g2, pF, pageIndex);
                        // int printResult = EDTUtils.print(printer, g2, pF,
                        // pageIndex);
                        if (printResult != Printable.PAGE_EXISTS) {
                            img2 = new BufferedImage((int) pF.getWidth() * 2, (int) pF.getHeight() * 2, IMAGE_TYPE);
                            g2 = (Graphics2D) img2.getGraphics();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                            g2.setColor(Color.WHITE);
                            g2.fillRect(0, 0, (int) pF.getWidth(), (int) pF.getHeight());
                            g2.setColor(Color.BLACK);
                            g2.scale(2, 2);
                            g2.drawImage(img1, 0, 0, null);
                        }

                        pages2.add(img2);
                        pages1.add(img1);
                        pageIndex++;
                        updateButtons();
                    }
                } catch (OutOfMemoryError oome) {
                    oome.printStackTrace();
                    render = false;
                } catch (PrinterException oome) {
                    oome.printStackTrace();
                    render = false;
                }
                while (pause && render) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // Nothing to do
                    }
                }
            }
            stopped = true;
            setStatus(false);
        }
    }

    private void addActions() {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 3257572818995525944L;

            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    void exit() {
        render = false;
        parent.setEnabled(true);
        setVisible(false);

        PrintQueue.getInstance().removePrintCallback(printcallback);
    }

    void setStatus(boolean v) {
        EDTUtils.executeOnEDT(new StatusRunnable(v));
    }

    @Override
    public void setEnabled(boolean enabled) {
        pauseRendering(!enabled);
        super.setEnabled(enabled);
    }

    private class StatusRunnable implements Runnable {

        private final boolean v;

        public StatusRunnable(boolean v) {
            this.v = v;
        }

        @Override
        public void run() {
            if (v) {
                status.setToolTipText(JUtilsI18n.get("de.dm.print.PreparingFurtherPages"));
                status.setIcon(statusicons[statuspos]);
                statuspos = (statuspos + 1) % 2;
            } else {
                status.setToolTipText(JUtilsI18n.get("de.dm.print.AllPagesReady"));
                status.setIcon(null);
            }
            maxpage.setText(JUtilsI18n.get("de.dm.print.ofpages", pages2.size()));
        }
    }

    protected void updateImage() {
        try {
            int index = Math.max(0, pageNumber.getSelectedIndex());
            Image i = null;
            if ((percent > 100) && pages2.size() > index) {
                BufferedImage bi = pages2.get(index);
                if (bi != null) {
                    if (percent == 200) {
                        // Use 200 percent prerendered image
                        i = bi;
                    } else {
                        // Use 200 percent prerendered image and scale to
                        // deserved size
                        double w = bi.getWidth();
                        double h = bi.getHeight();
                        w = w * percent / 200.0;
                        h = h * percent / 200.0;
                        i = GraphicsUtilities.getScaledInstance(bi, (int) Math.round(w), (int) Math.round(h), true);
                    }
                }
            }
            if (i == null) {
                BufferedImage bi = pages1.get(index);
                if (bi == null && pages2.size() > index) {
                    bi = pages2.get(index);
                    if (bi != null) {
                        bi = GraphicsUtilities.getScaledInstance(bi, bi.getWidth() / 2, bi.getHeight() / 2, true);
                    }
                }
                if (bi != null) {
                    if (percent == 100) {
                        i = bi;
                    } else {
                        double w = bi.getWidth();
                        double h = bi.getHeight();
                        w = w * percent / 100.0;
                        h = h * percent / 100.0;
                        i = GraphicsUtilities.getScaledInstance(bi, (int) Math.round(w), (int) Math.round(h), true);
                    }
                }
            }
            image.setIcon(null);
            imageIcon.setImage(i);
            image.setIcon(imageIcon);
        } catch (RuntimeException re) {
            re.printStackTrace();
            // Nothing to do
        }
    }

    void doSetup() {
        pauseRendering(true);
        boolean ok = PageSetup.show(name);
        if (ok) {
            render = false;
            while (!stopped) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Nothing to do
                }
            }
            pageNumber.removeAllItems();
            pages1.clear();
            pages2.clear();
            new PrintableToImages(printable.create()).start();
        } else {
            pauseRendering(false);
        }
    }

    void doSave() {
        pauseRendering(true);
        String filename = FileChooserUtils.saveFile(JPrintPreview.this, JUtilsI18n.get("SaveAs"), new FileFilterPDF());
        if (filename != null) {
            boolean result = true;
            if (new File(filename).exists()) {
                result = DialogUtils.ask(JPrintPreview.this, JUtilsI18n.get("de.dm.print.OverwriteFileQuestion", filename),
                        JUtilsI18n.get("de.dm.print.OverwriteFileQuestion.Note", filename));
            }
            if (result) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                EDTUtils.setEnabled(JPrintPreview.this, false);
                new SaveSwingWorker(filename).execute();
            } else {
                pauseRendering(false);
            }
        } else {
            pauseRendering(false);
        }
    }

    boolean nextImage() {
        int index = pageNumber.getSelectedIndex();
        if ((index < 0) && (pageNumber.getItemCount() > 0)) {
            pageNumber.setSelectedIndex(0);
            return true;
        }
        index++;
        if (pageNumber.getItemCount() <= index) {
            return false;
        }
        pageNumber.setSelectedIndex(index);
        return true;
    }

    boolean previousImage() {
        int index = pageNumber.getSelectedIndex();
        if ((index < 0) && (pageNumber.getItemCount() > 0)) {
            pageNumber.setSelectedIndex(0);
            return true;
        }
        index--;
        if (0 > index) {
            return false;
        }
        pageNumber.setSelectedIndex(index);
        return true;
    }

    private class PageActionListener implements ActionListener {

        public PageActionListener() {
            // Nothing to do
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            previous.setEnabled(pageNumber.getSelectedIndex() > 0);
            next.setEnabled(pageNumber.getSelectedIndex() + 1 < pageNumber.getItemCount());
            updateImage();
        }
    }
}