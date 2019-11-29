package de.df.jutils.gui.autocomplete;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * @author Scott Presley
 * @email cotfessi@gmail.com
 * @url http://jroller.com/page/santhosh/20050625#list_based_autocompletion
 */
public class ListAutoCompleter extends AutoCompleter {

    private List<String> completionList;
    private boolean      ignoreCase;

    public ListAutoCompleter(JTextComponent comp, List<String> completionList, boolean ignoreCase) {
        super(comp);
        this.completionList = completionList;
        this.ignoreCase = ignoreCase;
    }

    /**
     * update classes model depending on the data in textfield
     */
    @Override
    protected boolean updateListData() {
        String value = textComp.getText();

        int substringLen = value.length();

        List<String> possibleStrings = new ArrayList<String>();
        for (String aCompletionList : completionList) {
            String listEntry = aCompletionList;
            if (substringLen >= listEntry.length()) {
                continue;
            }

            if (ignoreCase) {
                if (value.equalsIgnoreCase(listEntry.substring(0, substringLen))) {
                    possibleStrings.add(listEntry);
                }
            } else {
                if (listEntry.startsWith(value)) {
                    possibleStrings.add(listEntry);
                }
            }
        }

        list.setListData(possibleStrings.toArray(new String[possibleStrings.size()]));
        return true;
    }

    /**
     * user has selected some item in the classes. update textfield
     * accordingly...
     */
    @Override
    protected void acceptedListItem(String selected) {
        if (selected == null) {
            return;
        }

        int prefixlen = textComp.getDocument().getLength();

        try {
            textComp.getDocument().insertString(textComp.getCaretPosition(), selected.substring(prefixlen), null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        popup.setVisible(false);
    }
}