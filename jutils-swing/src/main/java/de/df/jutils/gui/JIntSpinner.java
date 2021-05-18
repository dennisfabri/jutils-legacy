/*
 * JSpinnerInt.java
 * Created on 23. March 2002, 20:59
 */

package de.df.jutils.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;

/**
 * @author dennis
 */

public final class JIntSpinner extends javax.swing.JSpinner {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 4049635689765287987L;

    public static final int   NO_MIN           = Integer.MIN_VALUE;
    public static final int   NO_MAX           = Integer.MAX_VALUE;

    private IntSpinnerModel   ism              = null;

    public JIntSpinner(final int current, final int min, final int max, final int increment) {
        this(min, max, increment);
        setInt(current);
    }

    private JIntSpinner(final int min, final int max, final int increment) {
        super();
        ism = new IntSpinnerModel(min, max, increment);
        setModel(ism);

        try {
            JFormattedTextField tf2 = ((JSpinner.DefaultEditor) getEditor()).getTextField();

            tf2.addFocusListener(new FocusListener() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        JFormattedTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
                        int i = Integer.parseInt(tf.getText());
                        if (i > getMaximum()) {
                            setInt(getMaximum());
                            tf.setText("" + getMaximum());
                        } else if (i < getMinimum()) {
                            setInt(getMinimum());
                            tf.setText("" + getMinimum());
                        }
                    } catch (Exception ex) {
                        // Nothing to do
                        setInt(getInt());
                    }
                }

                @Override
                public void focusGained(FocusEvent e) {
                    // Nothing to do
                }
            });
        } catch (Exception e) {
        }
    }

    public void setEditable(boolean editable) {
        try {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
            tf.setEditable(editable);
        } catch (Exception e) {
            // Nothing to do
        }
    }

    public int getInt() {
        return ism.getInt();
    }

    public void setInt(final int i) {
        setValue(i);
        updateUI();
    }

    public void setMinimum(int min) {
        ism.setMin(min);
        setValue(ism.getValue());
        updateUI();
    }

    public void setMaximum(int max) {
        ism.setMax(max);
        setValue(ism.getValue());
        updateUI();
    }

    public int getMaximum() {
        return ism.getMax();
    }

    public int getMinimum() {
        return ism.getMin();
    }

    private static class IntSpinnerModel extends AbstractSpinnerModel {

        private static final long serialVersionUID = -2013114715199003509L;
        
        private int min       = 0;
        private int max       = 0;
        private int current   = 0;
        private int increment = 1;

        /** Creates new IntSpinnerModel */
        public IntSpinnerModel(final int ismmin, final int ismmax, final int increment) {
            min = ismmin;
            max = ismmin;
            setMax(ismmax);
            setMin(ismmin);
            setIncrement(increment);
            current = Math.min(Math.max(0, min), max);
        }

        @Override
        public void setValue(final Object obj) {
            setInt(((Number) obj).intValue());
        }

        public void setIncrement(int increment) {
            if (increment <= 0) {
                throw new IllegalArgumentException("Increment must be greater than 0.");
            }
            this.increment = increment;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            if (max < min) {
                min = max;
            }
            this.max = max;
            if (max < current) {
                setValue(Integer.valueOf(max));
            }
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            if (min > max) {
                max = min;
            }
            this.min = min;
            if (min > current) {
                setValue(Integer.valueOf(min));
            }
        }

        public int getInt() {
            return current;
        }

        public void setInt(final int zahl) {
            if ((zahl < min) || (zahl > max) || (zahl == current)) {
                return;
            }
            current = zahl;
            fireStateChanged();
        }

        @Override
        public Object getValue() {
            return current;
        }

        @Override
        public Object getNextValue() {
            int next = current + increment;
            if (next > max) {
                if (current >= max) {
                    return null;
                }
                next = max;
            }
            return next;
        }

        @Override
        public Object getPreviousValue() {
            int previous = current - increment;
            if (previous < min) {
                if (current <= min) {
                    return null;
                }
                previous = min;
            }
            return previous;
        }
    }
}