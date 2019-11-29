package de.df.jutils.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

public class JIntegerField extends JTextField {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long  serialVersionUID       = 3906081243996173875L;

    public static final int    EMPTY_FIELD            = -1;
    public static final int    NO_MAXVALUE            = 0;

    private static final Color YELLOW                 = new Color(255, 255, 200);
    private static final Color RED                    = new Color(255, 200, 200);

    private Color              enabled                = null;

    private NumberFormatter    nf                     = null;
    private boolean            required               = false;

    private FocusListener      selectionfocuslistener = null;

    public JIntegerField(int value, int min, int max, boolean required, boolean force) {
        super();
        nf = getIntegerFormatter(min, max);
        this.required = required;
        setInt(value);
        if (max > NO_MAXVALUE) {
            setColumns(("" + max).length());
        } else {
            setColumns(10);
        }
        if (force) {
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent arg0) {
                    if (!isValidInt()) {
                        Toolkit.getDefaultToolkit().beep();
                        requestFocus();
                    }
                }
            });
        }

        // Storing colors
        enabled = getBackground();
    }

    public JIntegerField(int value, int max, boolean required, boolean force) {
        this(value, 0, max, required, force);
    }

    public JIntegerField(int value, int max) {
        this(value, max, false, false);
    }

    public JIntegerField(int max, boolean required) {
        this(EMPTY_FIELD, max, required, false);
    }

    public JIntegerField(int max, boolean required, boolean forced) {
        this(EMPTY_FIELD, max, required, forced);
    }

    public JIntegerField(int max) {
        this(EMPTY_FIELD, max, false, false);
    }

    public JIntegerField(boolean required, boolean forced) {
        this(EMPTY_FIELD, NO_MAXVALUE, required, forced);
    }

    public JIntegerField(boolean required) {
        this(required, false);
    }

    public JIntegerField() {
        this(EMPTY_FIELD, NO_MAXVALUE, false, false);
    }

    private static NumberFormatter getIntegerFormatter(int min, int max) {
        NumberFormat n = NumberFormat.getIntegerInstance();
        n.setGroupingUsed(false);
        n.setParseIntegerOnly(true);
        n.setMinimumIntegerDigits(1);
        n.setMaximumFractionDigits(0);
        n.setMaximumFractionDigits(0);
        NumberFormatter nf = new NumberFormatter(n);
        nf.setAllowsInvalid(true);
        nf.setValueClass(Integer.class);
        nf.setCommitsOnValidEdit(true);
        if (max > NO_MAXVALUE) {
            nf.setMaximum(new MaxComparable(max));
        }
        nf.setMinimum(new MaxComparable(min));
        return nf;
    }

    public void setInt(int i) {
        if (i == getInt()) {
            return;
        }
        if (i > EMPTY_FIELD) {
            try {
                setText(nf.valueToString(Integer.valueOf(i)));
            } catch (ParseException e) {
                setText("");
            }
        } else {
            setText("");
        }
    }

    @Override
    public void setText(String t) {
        if (!super.getText().equals(t)) {
            super.setText(t);
        }
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

    public int getInt() {
        try {
            String text = getText();
            return (Integer) nf.stringToValue(text);
        } catch (RuntimeException e) {
            return 0;
        } catch (ParseException e) {
            return 0;
        }
    }

    public double getDouble() {
        return getInt();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isEnabled()) {
            Color next = enabled;
            if (!isValidInt()) {
                if (getText().length() == 0) {
                    next = YELLOW;
                } else {
                    next = RED;
                }
            }
            if (!next.equals(getBackground())) {
                setBackground(next);
            }
        }
        super.paintComponent(g);
    }

    public boolean isValidInt() {
        boolean result = true;
        if (required || (getText().length() > 0)) {
            try {
                String text = getText();
                Object o = nf.stringToValue(text);
                text = "0" + text;
                while ((text.length() > 1) && (text.startsWith("0"))) {
                    text = text.substring(1, text.length());
                }
                result = nf.valueToString(o).equalsIgnoreCase(text);
            } catch (ParseException e) {
                return false;
            }
        }

        if (!result) {
            return false;
        }
        if (validator != null) {
            if (getText().length() > 0) {
                try {
                    return validator.validate((Integer) nf.stringToValue(getText()));
                } catch (ParseException pe) {
                    return false;
                }
            }
            return validator.validate(0);
        }

        return true;
    }

    private Validator validator = null;

    public abstract static class Validator {
        public abstract boolean validate(int value);
    }

    public void setValidator(Validator v) {
        validator = v;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() && (!enabled)) {
            setBackground(this.enabled);
        }
        super.setEnabled(enabled);
    }

    static final class ZeroComparable implements Comparable<Object> {

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ZeroComparable;
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof Number) {
                return -((Number) o).intValue();
            }
            return Integer.MAX_VALUE;
        }
    }

    private static class MaxComparable implements Comparable<Object> {

        private final int maximum;

        public MaxComparable(int max) {
            maximum = max;
        }

        @Override
        public int hashCode() {
            return maximum;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof MaxComparable && (maximum == ((MaxComparable) obj).maximum);
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof Number) {
                return -((Number) o).intValue() + maximum;
            }
            return Integer.MIN_VALUE;
        }
    }
}