package de.df.jutils.gui.autocomplete;

import java.awt.Color;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * @author Santhosh Kumar T
 * @email santhosh@in.fiorano.com
 * @link http://jroller.com/page/santhosh/20050620#file_path_autocompletion
 */
abstract class AutoCompleter {

    private Action              acceptAction     = new AcceptAction(AUTOCOMPLETER);
    private DocumentListener    documentListener = new PopupDocumentListener();
    private Action              showAction       = new ShowAction(AUTOCOMPLETER);
    private Action              upAction         = new UpAction(AUTOCOMPLETER);
    private Action              hidePopupAction  = new HidePopupAction(AUTOCOMPLETER);

    protected JList<String>     list             = new JList<String>();
    protected JPopupMenu        popup            = new JPopupMenu();
    protected JTextComponent    textComp;

    // NOI18N
    private static final String AUTOCOMPLETER    = "AUTOCOMPLETER";

    public AutoCompleter(JTextComponent comp) {
        textComp = comp;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);

        list.setFocusable(false);
        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.add(scroll);

        if (textComp instanceof JTextField) {
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
            textComp.getDocument().addDocumentListener(documentListener);
        } else {
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_FOCUSED);
        }

        textComp.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);
        textComp.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);

        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // Nothing to do
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Nothing to do
            }
        });
        list.setRequestFocusEnabled(false);
    }

    protected void showPopup() {
        try {
            popup.setVisible(false);
            if (textComp.isEnabled() && updateListData() && list.getModel().getSize() != 0) {
                if (!(textComp instanceof JTextField)) {
                    textComp.getDocument().addDocumentListener(documentListener);
                }
                textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
                int size = list.getModel().getSize();
                list.setVisibleRowCount(size < 10 ? size : 10);

                int x = 0;
                try {
                    int pos = Math.min(textComp.getCaret().getDot(), textComp.getCaret().getMark());
                    x = (int) Math.round(textComp.modelToView2D(pos).getX());
                } catch (BadLocationException e) {
                    // this should never happen!!!
                    e.printStackTrace();
                }
                popup.show(textComp, x, textComp.getHeight());
            } else {
                popup.setVisible(false);
            }
            textComp.requestFocus();
        } catch (IllegalComponentStateException icse) {
            // React to illegal state exception.
            popup.setVisible(false);
        }
    }

    /**
     * Selects the next item in the list. It won't change the selection if the
     * currently selected item is already the last item.
     */
    protected void selectNextPossibleValue() {
        int si = list.getSelectedIndex();

        if (si < list.getModel().getSize() - 1) {
            list.setSelectedIndex(si + 1);
            list.ensureIndexIsVisible(si + 1);
        }
    }

    /**
     * Selects the previous item in the list. It won't change the selection if
     * the currently selected item is already the first item.
     */
    protected void selectPreviousPossibleValue() {
        int si = list.getSelectedIndex();

        if (si > 0) {
            list.setSelectedIndex(si - 1);
            list.ensureIndexIsVisible(si - 1);
        }
    }

    // update list model depending on the data in textfield
    protected abstract boolean updateListData();

    // user has selected some item in the list. update textfield accordingly...
    protected abstract void acceptedListItem(String selected);

    @SuppressWarnings("serial")
    private static final class HidePopupAction extends AbstractAction {
        private final String autocompleter;

        HidePopupAction(String autocompleter) {
            super();
            this.autocompleter = autocompleter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer = (AutoCompleter) tf.getClientProperty(autocompleter);
            if (tf.isEnabled()) {
                completer.popup.setVisible(false);
            }
        }
    }

    @SuppressWarnings("serial")
    private static final class UpAction extends AbstractAction {
        private final String autocompleter;

        UpAction(String autocompleter) {
            super();
            this.autocompleter = autocompleter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer = (AutoCompleter) tf.getClientProperty(autocompleter);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectPreviousPossibleValue();
                }
            }
        }
    }

    @SuppressWarnings("serial")
    private static final class ShowAction extends AbstractAction {
        private final String autocompleter;

        ShowAction(String autocompleter) {
            super();
            this.autocompleter = autocompleter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer = (AutoCompleter) tf.getClientProperty(autocompleter);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectNextPossibleValue();
                } else {
                    completer.showPopup();
                }
            }
        }
    }

    final class PopupDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            showPopup();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            showPopup();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // Nothing to do
        }
    }

    @SuppressWarnings("serial")
    private static final class AcceptAction extends AbstractAction {
        private final String autocompleter;

        AcceptAction(String autocompleter) {
            super();
            this.autocompleter = autocompleter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer = (AutoCompleter) tf.getClientProperty(autocompleter);
            completer.popup.setVisible(false);
            completer.acceptedListItem(completer.list.getSelectedValue());
        }
    }
}