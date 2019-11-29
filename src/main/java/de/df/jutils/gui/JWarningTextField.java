package de.df.jutils.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JWarningTextField extends JTextField {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long  serialVersionUID       = 3906081243996173875L;

    public static final int    EMPTY_FIELD            = -1;
    public static final int    NO_MAXVALUE            = 0;

    private static final Color YELLOW                 = new Color(255, 255, 200);
    private static final Color RED                    = new Color(255, 200, 200);

    private Color              enabled                = null;

    private boolean            required               = false;
    private boolean            force                  = false;

    private FocusListener      selectionfocuslistener = null;

    public JWarningTextField(boolean required, boolean force) {
        super();
        this.required = required;
        this.force = force;
        // Storing colors
        enabled = getBackground();
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent arg0) {
                checkFocus();
            }
        });
        getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateOk();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateOk();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateOk();
            }
        });

        updateOk();
    }

    public JWarningTextField() {
        this(false, false);
    }

    public JWarningTextField(String text) {
        this(false, false);
        setText(text);
    }

    public void setRequired(boolean r) {
        required = r;
        updateOk();
    }

    public void setForced(boolean f) {
        force = f;
        updateOk();
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isEnabled()) {
            Color next = enabled;
            if (!isOk()) {
                if (getText().length() == 0) {
                    next = YELLOW;
                } else {
                    next = RED;
                }
            }
            if (!next.equals(getBackground())) {
                setBackground(next);
            }
        } else {
            setBackground(enabled);
        }
        super.paintComponent(g);
    }

    private String[] specials = null;

    public boolean isSpecialString() {
        if (specials == null) {
            return false;
        }
        String s = getText();
        for (String special : specials) {
            if (special.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void setSpecialStrings(String... specials) {
        this.specials = specials;
        updateOk();
        repaint();
    }

    /**
     * Based on an implementation of Karsten Kropp.
     */
    public void setAutoSelectAll(boolean active) {
        if (active) {
            if (selectionfocuslistener == null) {
                selectionfocuslistener = new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent arg0) {
                        selectAll();
                    }
                };
                addFocusListener(selectionfocuslistener);
            }
        } else {
            if (selectionfocuslistener != null) {
                removeFocusListener(selectionfocuslistener);
                selectionfocuslistener = null;
            }
        }
    }

    public void disableSpecialStrings() {
        specials = null;
        updateOk();
        repaint();
    }

    private String[] forbidden = null;

    public boolean isForbiddenString() {
        if (forbidden == null) {
            return false;
        }
        String s = getText();
        for (String aForbidden : forbidden) {
            if (aForbidden.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void setForbiddenStrings(String... forbidden) {
        this.forbidden = forbidden;
        updateOk();
        repaint();
    }

    public void disableForbiddenStrings() {
        forbidden = null;
        updateOk();
        repaint();
    }

    public boolean isValidString() {
        if (required && (getText().length() == 0)) {
            return false;
        }
        return validator == null || validator.validate(getText());
    }

    private Validator validator = null;

    public static interface Validator {
        public boolean validate(String value);
    }

    public void setValidator(Validator v) {
        validator = v;
        updateOk();
        repaint();
    }

    void checkFocus() {
        if (force || (getText().length() > 0)) {
            if (!isOk()) {
                Toolkit.getDefaultToolkit().beep();
                requestFocus();
            }
        }
    }

    private void updateOk() {
        isok = (isValidString() || isSpecialString()) && (!isForbiddenString());
    }

    private boolean isok = true;

    public boolean isOk() {
        return isok;
    }
}