package de.df.jutils.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

public class JDoubleField extends JTextField {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long  serialVersionUID       = 3906081243996173875L;

    private static final Color YELLOW                 = new Color(255, 255, 200);
    private static final Color RED                    = new Color(255, 200, 200);

    public static final double EMPTY_FIELD            = -1;
    public static final double NO_MAXVALUE            = 0;

    private Color              enabled                = null;

    private boolean            required               = false;
    private NumberFormatter    nf                     = null;

    private String[]           specials               = null;

    private FocusListener      selectionfocuslistener = null;

    public JDoubleField(double value, double max, boolean required, boolean force) {
        super();
        this.required = required;
        nf = getDoubleFormatter(max);
        if (value > EMPTY_FIELD) {
            try {
                setText(nf.valueToString(value));
            } catch (ParseException pe) {
                // Nothing to do
            }
        }

        if (force) {
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent arg0) {
                    if (!(isValidDouble() || isSpecialString())) {
                        Toolkit.getDefaultToolkit().beep();
                        requestFocus();
                    }
                }
            });
        }

        // Storing colors
        enabled = getBackground();
    }

    public JDoubleField(double value, double max) {
        this(value, max, false, false);
    }

    public JDoubleField(double max, boolean required) {
        this(EMPTY_FIELD, max, required, false);
    }

    public JDoubleField(double max) {
        this(EMPTY_FIELD, max, false, false);
    }

    public JDoubleField(boolean required, boolean force) {
        this(EMPTY_FIELD, NO_MAXVALUE, required, force);
    }

    public JDoubleField(boolean required) {
        this(NO_MAXVALUE, required);
    }

    public JDoubleField() {
        this(NO_MAXVALUE, false);
    }

    private static NumberFormatter getDoubleFormatter(double max) {
        DecimalFormat n = new DecimalFormat();
        n.setMaximumFractionDigits(2);
        n.setMinimumFractionDigits(0);
        n.setGroupingUsed(false);
        n.setDecimalSeparatorAlwaysShown(false);
        NumberFormatter nf = new NumberFormatter(n);
        nf.setAllowsInvalid(false);
        nf.setValueClass(Double.class);
        if (max > NO_MAXVALUE) {
            nf.setMaximum(new MaxComparable(max));
        }
        nf.setMinimum(new ZeroComparable());
        return nf;
    }

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
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isEnabled()) {
            Color next = enabled;
            if (!(isValidDouble() || isSpecialString())) {
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

    public boolean isValidDouble() {
        if (required || (getText().length() > 0)) {
            try {
                String text = getText();
                Object o = nf.stringToValue(text);
                boolean result = false;
                text = "0" + text;
                while ((text.length() > 1) && (text.startsWith("0"))) {
                    text = text.substring(1, text.length());
                }
                if (!Character.isDigit(text.charAt(0))) {
                    text = "0" + text;
                }
                text += "0";
                while ((!result) && (text.endsWith("0"))) {
                    text = text.substring(0, text.length() - 1);
                    result = nf.valueToString(o).equalsIgnoreCase(text);
                    if (!result) {
                        DecimalFormat df = (DecimalFormat) nf.getFormat();
                        df.setDecimalSeparatorAlwaysShown(true);
                        result = nf.valueToString(o).equalsIgnoreCase(text);
                        df.setDecimalSeparatorAlwaysShown(false);
                    }
                }
                return result;
            } catch (ParseException e) {
                return false;
            }
        }
        return true;
    }

    public void setDouble(double i) {
        if (i > EMPTY_FIELD) {
            try {
                setText(nf.valueToString(i));
            } catch (ParseException e) {
                setText("");
            }
        } else {
            setText("");
        }
    }

    public double getDouble() {
        try {
            return ((Number) nf.stringToValue(getText())).doubleValue();
        } catch (RuntimeException e) {
            return 0;
        } catch (ParseException e) {
            return 0;
        }
    }

    static final class ZeroComparable implements Comparable<Object> {

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return ((obj instanceof ZeroComparable));
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof Number) {
                double i = -((Number) o).doubleValue();
                if ((i < -1) || (i > 1)) {
                    return (int) i;
                }
                if (i < 0) {
                    return -1;
                }
                if (i > 0) {
                    return 1;
                }
                return 0;
            }
            return Integer.MAX_VALUE;
        }
    }

    private static class MaxComparable implements Comparable<Object> {

        private final double maximum;

        public MaxComparable(double max) {
            maximum = max;
        }

        @Override
        public int hashCode() {
            return (int) maximum;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof MaxComparable && (maximum == ((MaxComparable) obj).maximum);
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof Number) {
                double i = -((Number) o).doubleValue() + maximum;
                if ((i < -1) || (i > 1)) {
                    return (int) i;
                }
                if (i < 0) {
                    return -1;
                }
                if (i > 0) {
                    return 1;
                }
                return 0;
            }
            return Integer.MIN_VALUE;
        }
    }
}