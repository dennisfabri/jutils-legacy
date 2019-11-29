/*
 * Created on 02.01.2005
 */
package de.df.jutils.print;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.util.EDTUtils;

public abstract class AComponentMultiOnPagePrintable implements Printable {

    private PageMode mode = PageMode.FOUR_PER_PAGE;

    public AComponentMultiOnPagePrintable(PageMode mode) {
        this.mode = mode;
    }

    public PageMode getMode() {
        return mode;
    }

    public int getPagesPerPage() {
        switch (mode) {
        case ONE_PER_PAGE:
            return 1;
        case TWO_PER_PAGE:
            return 2;
        default:
        case FOUR_PER_PAGE:
            return 4;
        }
    }

    private static JPanel getWhitePanel() {
        return EDTUtils.executeOnEDTwithReturn(() -> getWhitePanelI());
    }

    protected static JPanel getWhitePanelI() {
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(Color.WHITE);
        return p;
    }

    private JPanel createPage(PageFormat pf) {
        return EDTUtils.executeOnEDTwithReturn(() -> createPageI(pf));
    }

    private static final class AddingRunnable implements Runnable {

        private final JPanel     page;
        private final JComponent p;
        private final int        posx;
        private final int        posy;

        public AddingRunnable(JPanel page, JComponent p, int posx, int posy) {
            this.page = page;
            this.p = p;
            this.posx = posx;
            this.posy = posy;
        }

        @Override
        public void run() {
            page.add(p, CC.xy(posx, posy));
        }
    }

    JPanel createPageI(PageFormat pf) {
        int bottom = (int) (pf.getHeight() - pf.getImageableHeight() - pf.getImageableY());
        int top = (int) pf.getImageableY();
        int right = (int) (pf.getWidth() - pf.getImageableWidth() - pf.getImageableX());
        int left = (int) pf.getImageableX();

        String layoutx = "";
        String layouty = "";
        switch (mode) {
        case ONE_PER_PAGE:
            layoutx = "fill:4dlu:grow";
            layouty = "fill:4dlu:grow";
            break;
        case TWO_PER_PAGE:
            layoutx = "fill:4dlu:grow,fill:" + right + "px,1px,fill:" + left + "px,fill:4dlu:grow";
            layouty = "fill:4dlu:grow";
            break;
        default:
        case FOUR_PER_PAGE:
            layoutx = "fill:4dlu:grow,fill:" + right + "px,1px,fill:" + left + "px,fill:4dlu:grow";
            layouty = "fill:4dlu:grow,fill:" + bottom + "px,1px,fill:" + top + "px,fill:4dlu:grow";
            break;
        }
        FormLayout layout = new FormLayout(layoutx, layouty);
        switch (mode) {
        case ONE_PER_PAGE:
            break;
        case TWO_PER_PAGE:
            layout.setColumnGroups(new int[][] { { 1, 5 } });
            break;
        default:
        case FOUR_PER_PAGE:
            layout.setColumnGroups(new int[][] { { 1, 5 } });
            layout.setRowGroups(new int[][] { { 1, 5 } });
            break;
        }

        JPanel page = new JPanel(layout);
        // JPanel page = new FormDebugPanel(layout);
        page.setBackground(Color.BLACK);
        page.setForeground(Color.WHITE);

        switch (mode) {
        case ONE_PER_PAGE:
            break;
        case TWO_PER_PAGE:
            page.add(getWhitePanelI(), CC.xy(2, 1));
            page.add(getWhitePanelI(), CC.xy(4, 1));
            break;
        default:
        case FOUR_PER_PAGE:
            page.add(getWhitePanelI(), CC.xy(2, 1));
            page.add(getWhitePanelI(), CC.xy(4, 1));

            page.add(getWhitePanelI(), CC.xy(1, 2));
            page.add(getWhitePanelI(), CC.xy(2, 2));
            page.add(getWhitePanelI(), CC.xy(4, 2));
            page.add(getWhitePanelI(), CC.xy(5, 2));

            page.add(getWhitePanelI(), CC.xy(1, 4));
            page.add(getWhitePanelI(), CC.xy(2, 4));
            page.add(getWhitePanelI(), CC.xy(4, 4));
            page.add(getWhitePanelI(), CC.xy(5, 4));

            page.add(getWhitePanelI(), CC.xy(2, 5));
            page.add(getWhitePanelI(), CC.xy(4, 5));
            break;
        }

        return page;
    }

    public abstract JComponent getPanel(int page, int offset);

    public abstract boolean pageExists(int page);

    private static void add(JPanel page, JComponent p, int posx, int posy) {
        EDTUtils.executeOnEDT(new AddingRunnable(page, p, posx, posy));
    }

    @Override
    public int print(Graphics g, PageFormat pf, int index) throws PrinterException {
        if (!pageExists(index)) {
            return NO_SUCH_PAGE;
        }

        JPanel page = createPage(pf);

        int amount = getPagesPerPage();
        for (int x = 0; x < amount; x++) {
            JComponent p = getPanel(index, x);
            if (p == null) {
                p = getWhitePanel();
            }
            int posx = 1 + (x % 2 > 0 ? 4 : 0);
            int posy = 1 + (x / 2 > 0 ? 4 : 0);

            add(page, p, posx, posy);
        }
        ComponentPagePrintable.printComponent(page, g, pf);
        page.removeAll();
        page = null;
        return PAGE_EXISTS;
    }
}