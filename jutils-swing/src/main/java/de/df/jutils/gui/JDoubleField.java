package de.df.jutils.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

public class JDoubleField extends JWarningTextField {

    private static final long serialVersionUID = 3906081243996173875L;

    public static final double EMPTY_FIELD = -1;
    public static final double NO_MAXVALUE = 0;

    private NumberFormatter nf = null;

    private String[] specials = null;

    private JDoubleField(double value, double max, boolean required, boolean force) {
        super(required, force);
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
    }

    public JDoubleField(double value, double max) {
        this(value, max, false, false);
    }

    private JDoubleField(double max, boolean required) {
        this(EMPTY_FIELD, max, required, false);
    }

    public JDoubleField(double max) {
        this(EMPTY_FIELD, max, false, false);
    }

    public JDoubleField(boolean required, boolean force) {
        this(EMPTY_FIELD, NO_MAXVALUE, required, force);
    }

    private JDoubleField(boolean required) {
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
        nf.setCommitsOnValidEdit(true);
        if (max > NO_MAXVALUE) {
            nf.setMaximum(new MaxComparable(max));
        }
        nf.setMinimum(new ZeroComparable());
        return nf;
    }

    public void setDouble(double i) {
        if (getDouble() == i) {
            return;
        }
        
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

    @Override
    public void setText(String t) {
        if (!super.getText().equals(t)) {
            super.setText(t);
        }
    }
    
    public double getDouble() {
        try {
            return ((Number) nf.stringToValue(getText())).doubleValue();
        } catch (RuntimeException | ParseException e) {
            return 0;
        }
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

    public void disableSpecialStrings() {
        specials = null;
        repaint();
    }

    public boolean isValidDouble() {
        return isOk();
    }

    @Override
    public boolean isOk() {
        if (isSpecialString()) {
            return true;
        }

        if (!super.isOk()) {
            return false;
        }

        if (getText().length() > 0) {
            try {
                String text = getText();
                Object o = nf.stringToValue(text);
                boolean result = false;
                while ((text.length() > 1) && (text.startsWith("0"))) {
                    text = text.substring(1, text.length());
                }
                if (!Character.isDigit(text.charAt(0))) {
                    text = "0" + text;
                }
                text += 0;
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

    public void setValidator(Validator validator) {
        super.setValidator(validator == null ? null : new ValidatorWrapper(validator));
    }

    private final class ValidatorWrapper implements JWarningTextField.Validator {
        private final Validator validator;

        public ValidatorWrapper(Validator v) {
            validator = v;
        }

        @Override
        public boolean validate(String text) {
            try {
                if (text.isEmpty()) {
                    return true;
                }
                double value = (Double) nf.stringToValue(text);
                return validator.validate(value);
            } catch (RuntimeException | ParseException e) {
                return false;
            }
        }
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

    public static interface Validator {
        boolean validate(double value);
    }
}