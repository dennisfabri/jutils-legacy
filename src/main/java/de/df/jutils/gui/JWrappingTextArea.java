package de.df.jutils.gui;

import java.awt.KeyboardFocusManager;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.Document;

/**
 * Focus traversal taken from the Java Glossary:
 * http://mindprod.com/jgloss/focus.html
 * Date: 11.05.05
 */
public class JWrappingTextArea extends JTextArea {

    private static final long     serialVersionUID = 3689072832978169910L;

    /**
     * tab keystroke to be used for forward traversal.
     */
    private static Set<KeyStroke> tab              = new HashSet<KeyStroke>(1);
    /**
     * shift-tab keystroke to be used for backward traversal.
     */
    private static Set<KeyStroke> shifttab         = new HashSet<KeyStroke>(1);

    static {
        tab.add(KeyStroke.getKeyStroke("TAB"));
        shifttab.add(KeyStroke.getKeyStroke("shift TAB"));
    }

    public JWrappingTextArea() {
        super();
        init();
    }

    public JWrappingTextArea(String arg0) {
        super(arg0);
        init();
    }

    public JWrappingTextArea(int arg0, int arg1) {
        super(arg0, arg1);
        init();
    }

    public JWrappingTextArea(String arg0, int arg1, int arg2) {
        super(arg0, arg1, arg2);
        init();
    }

    public JWrappingTextArea(Document arg0) {
        super(arg0);
        init();
    }

    public JWrappingTextArea(Document arg0, String arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
        init();
    }

    private void init() {
        setWrapStyleWord(true);
        setLineWrap(true);

        // setFont(UIManager.getDefaults().getFont("label.font"));

        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, tab);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, shifttab);
    }
}