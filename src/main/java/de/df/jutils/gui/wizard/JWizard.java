/*
 * Created on 14.04.2005
 */
package de.df.jutils.gui.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.gui.util.UIUtils;
import de.df.jutils.i18n.ResourceBundleUIProvider;
import de.df.jutils.i18n.UIElementsProvider;

public class JWizard extends JPanel {

    private static final String       WIZARD_PREVIOUS  = "de.dm.wizard.previous";
    private static final String       WIZARD_NEXT      = "de.dm.wizard.next";
    private static final String       WIZARD_CANCEL    = "de.dm.wizard.cancel";
    private static final String       WIZARD_FINISH    = "de.dm.wizard.finish";

    private static final long         serialVersionUID = 3763091960105022007L;

    private static UIElementsProvider basicProvider    = null;

    private CardLayout                infoLayout       = new CardLayout();
    private CardLayout                centerLayout     = new CardLayout();

    private JPanel                    info             = new JPanel(infoLayout);
    private JPanel                    center           = new JPanel(centerLayout);

    protected JButton                 next             = null;
    protected JButton                 previous         = null;
    protected JButton                 finish           = null;
    protected JButton                 cancel           = null;

    private boolean                   nextEnabled      = true;
    private boolean                   previousEnabled  = true;
    private boolean                   finishEnabled    = false;
    private boolean                   cancelEnabled    = true;

    private UIElementsProvider        provider         = null;

    private LinkedList<AWizardPage>   pages            = new LinkedList<AWizardPage>();
    private LinkedList<Object>        listeners        = new LinkedList<Object>();

    protected Stack<Integer>          pageStack        = new Stack<Integer>();

    protected int                     index            = 0;

    public JWizard() {
        this(null);
    }

    public JWizard(UIElementsProvider provider) {
        init(provider);
    }

    private static synchronized UIElementsProvider getFallbackUIProvider() {
        if (basicProvider == null) {
            basicProvider = new ResourceBundleUIProvider("jutils");
        }
        return basicProvider;
    }

    private void init(UIElementsProvider p) {
        if (p == null) {
            provider = getFallbackUIProvider();
        } else {
            provider = p;
        }

        setLayout(new BorderLayout());
        add(info, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(getButtons(), BorderLayout.SOUTH);
    }

    private JButton getButton(String id) {
        return new JButton(provider.getString(id), provider.getIcon(id));
    }

    public synchronized void addPage(AWizardPage page) {
        if (page == null) {
            throw new NullPointerException();
        }
        info.add(getInfoPanel(page.getTitle(), page.getNote()), "" + pages.size());
        center.add(page.getPage(), "" + pages.size());

        pages.addLast(page);
        addListener(page);
    }

    public synchronized void addListener(Object listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        boolean add = false;
        if (listener instanceof UpdateListener) {
            add = true;
        } else {
            if (listener instanceof PageSwitchListener) {
                add = true;
            } else {
                if (listener instanceof FinishListener) {
                    add = true;
                } else {
                    if (listener instanceof CancelListener) {
                        add = true;
                    }
                }
            }
        }
        if (add && (!listeners.contains(listener))) {
            listeners.addLast(listener);
        }
    }

    public synchronized void removeListener(Object listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        listeners.remove(listener);
    }

    public void notifyUpdate() {
        @SuppressWarnings("rawtypes")
        ListIterator li = listeners.listIterator();
        while (li.hasNext()) {
            Object nextListener = li.next();
            if (nextListener instanceof UpdateListener) {
                ((UpdateListener) nextListener).update();
            }
        }
        updateButtons();
    }

    private void notifyPageSwitch(boolean forward) {
        @SuppressWarnings("rawtypes")
        ListIterator li = listeners.listIterator();
        while (li.hasNext()) {
            Object nextListener = li.next();
            if (nextListener instanceof PageSwitchListener) {
                ((PageSwitchListener) nextListener).pageSwitch(forward);
            }
        }
        updateButtons();
    }

    public void notifyCancel() {
        isFinished = false;
        @SuppressWarnings("rawtypes")
        ListIterator li = listeners.listIterator();
        while (li.hasNext()) {
            Object nextListener = li.next();
            if (nextListener instanceof CancelListener) {
                ((CancelListener) nextListener).cancel();
            }
        }
        close();
    }

    public void notifyFinish() {
        isFinished = true;
        @SuppressWarnings("rawtypes")
        ListIterator li = listeners.listIterator();
        while (li.hasNext()) {
            Object nextListener = li.next();
            if (nextListener instanceof FinishListener) {
                ((FinishListener) nextListener).finish();
            }
        }
        close();
    }

    protected void updateButtons() {
        EDTUtils.setEnabled(next, (getPageCount() > getCurrentPageIndex() + 1) && nextEnabled);
        EDTUtils.setEnabled(previous, (!pageStack.isEmpty()) && previousEnabled);
        EDTUtils.setEnabled(finish, finishEnabled);
        EDTUtils.setEnabled(cancel, cancelEnabled);
    }

    protected void disableButtons() {
        EDTUtils.setEnabled(next, false);
        EDTUtils.setEnabled(previous, false);
        EDTUtils.setEnabled(finish, false);
        EDTUtils.setEnabled(cancel, false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            updateButtons();
        } else {
            disableButtons();
        }
    }

    private JPanel getButtons() {
        finish = getButton(WIZARD_FINISH);
        finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                notifyFinish();
            }
        });
        cancel = getButton(WIZARD_CANCEL);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                notifyCancel();
            }
        });
        next = getButton(WIZARD_NEXT);
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                nextPage();
            }
        });
        previous = getButton(WIZARD_PREVIOUS);
        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                previousPage();
            }
        });

        finish.setEnabled(false);
        next.setEnabled(false);
        previous.setEnabled(false);

        FormLayout layout = new FormLayout("4dlu:grow,fill:default,4dlu,fill:default,16dlu," + "fill:default,4dlu,fill:default,4dlu", "4dlu,fill:default,4dlu");
        layout.setColumnGroups(new int[][] { { 2, 4, 6, 8 } });
        JPanel buttons = new JPanel(layout);

        buttons.add(previous, CC.xy(2, 2));
        buttons.add(next, CC.xy(4, 2));
        buttons.add(finish, CC.xy(6, 2));
        buttons.add(cancel, CC.xy(8, 2));
        return buttons;
    }

    protected int getNextPageIndex() {
        return getNextPageIndex(index);
    }

    private int getNextPageIndex(int x) {
        AWizardPage page = pages.get(x);
        int i = page.getNextPageIndex();
        if (i == AWizardPage.NO_NEXT_PAGE) {
            return AWizardPage.NO_NEXT_PAGE;
        }
        if (i == AWizardPage.NEXT_PAGE_BY_INDEX) {
            i = x + 1;
        }
        if (i < 0) {
            throw new IndexOutOfBoundsException("Index must be higher than 0 or a constant from JWizardPage!");
        }
        if (i >= pages.size()) {
            throw new IndexOutOfBoundsException("Index must be a valid pageindex!");
        }
        if (!pages.get(i).isEnabled()) {
            i = getNextPageIndex(i);
        }
        return i;
    }

    public void start() {
        isFinished = false;
        start(0);
    }

    public void start(int steps) {
        if (pages.size() == 0) {
            throw new IllegalStateException();
        }
        pageStack.clear();
        index = 0;
        AWizardPage page = pages.getFirst();
        if (!page.isEnabled()) {
            index = getNextPageIndex(0);
        }
        for (int i = 0; i < steps; i++) {
            nextPage();
        }

        showPage(true);
        updateButtons();
    }

    public int getCurrentPageIndex() {
        return index;
    }

    public int getPageCount() {
        return pages.size();
    }

    public boolean isCurrentPage(AWizardPage page) {
        if (index >= pages.size()) {
            return false;
        }
        AWizardPage current = pages.get(index);
        return current == page;
    }

    public int getPageIndex(AWizardPage page) {
        ListIterator<AWizardPage> li = pages.listIterator();
        while (li.hasNext()) {
            int idx = li.nextIndex();
            AWizardPage awp = li.next();
            if (awp == page) {
                return idx;
            }
        }
        return -1;
    }

    public boolean isCurrentPage(JComponent page) {
        if (index >= pages.size()) {
            return false;
        }
        AWizardPage current = pages.get(index);
        return current.getPage() == page;
    }

    public void close() {
        // pageStack.clear();
    }

    public void pageSwitch(boolean forward) {
        if (forward) {
            if (next.isEnabled()) {
                nextPage();
            }
        } else {
            if (previous.isEnabled()) {
                previousPage();
            }
        }
    }

    protected void nextPage() {
        AWizardPage p = pages.get(index);
        if (p.leavePage(true)) {
            int i = getNextPageIndex();
            if (i != AWizardPage.NO_NEXT_PAGE) {
                pageStack.push(index);

                index = i;
                showPage(true);
            }
            updateButtons();
        }
    }

    protected void previousPage() {
        if (!pageStack.isEmpty()) {
            index = pageStack.pop();
            showPage(false);
        }
        updateButtons();
    }

    public void setNextButtonEnabled(boolean enabled) {
        nextEnabled = enabled;
        updateButtons();
    }

    public void setPrevioustButtonEnabled(boolean enabled) {
        previousEnabled = enabled;
        updateButtons();
    }

    public void setFinishButtonEnabled(boolean enabled) {
        finishEnabled = enabled;
        updateButtons();
    }

    public void setCancelButtonEnabled(boolean enabled) {
        cancelEnabled = enabled;
        updateButtons();
    }

    private JPanel getInfoPanel(String title, String note) {
        return UIUtils.createHeaderPanel(title, note);
    }

    private void showPage(boolean forward) {
        if ((index >= pages.size()) || (index < 0)) {
            throw new IndexOutOfBoundsException("" + index + "not between 0 and " + pages.size() + "!");
        }
        infoLayout.show(info, "" + index);
        centerLayout.show(center, "" + index);

        pages.get(index).getPage().requestFocus();

        notifyPageSwitch(forward);
    }

    private boolean isFinished = false;

    public boolean isFinished() {
        return isFinished;
    }
}