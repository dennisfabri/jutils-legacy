package de.df.jutils.gui;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

public class JIntegerField extends JWarningTextField {

    private static final long serialVersionUID = 3906081243996173875L;

    public static final int EMPTY_FIELD = -1;
    public static final int NO_MAXVALUE = 0;
    private NumberFormatter nf = null;

    public JIntegerField(int value, int min, int max, boolean required, boolean force) {
        super(required, force);
        nf = getIntegerFormatter(min, max);
        setInt(value);
        if (max > NO_MAXVALUE) {
            setColumns(("" + max).length());
        } else {
            setColumns(10);
        }
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

    public int getInt() {
        try {
            return (Integer) nf.stringToValue(getText());
        } catch (RuntimeException | ParseException e) {
            return 0;
        }
    }

    public double getDouble() {
        return getInt();
    }

    public boolean isValidInt() {
        return isOk();
    }

    @Override
    public boolean isOk() {
        if (!super.isOk()) {
            return false;
        }

        if (getText().length() > 0) {
            try {
                String text = getText();
                Object o = nf.stringToValue(text);
                while ((text.length() > 1) && (text.startsWith("0"))) {
                    text = text.substring(1, text.length());
                }
                if (!nf.valueToString(o).equalsIgnoreCase(text)) {
                    return false;
                }
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
                int value = (Integer) nf.stringToValue(text);
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

    public static interface Validator {
        boolean validate(int value);
    }
}