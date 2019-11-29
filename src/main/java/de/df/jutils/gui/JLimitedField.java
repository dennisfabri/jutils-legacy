package de.df.jutils.gui;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.*;

public final class JLimitedField extends JTextField {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256718494165775670L;

    static final Toolkit      TOOLKIT          = Toolkit.getDefaultToolkit();
    protected final int       length;

    public JLimitedField(final int maxlength) {
        super();
        if (maxlength <= 0) {
            length = 0;
        } else {
            length = maxlength;
        }
    }

    public JLimitedField(final String s) {
        super();
        length = 0;
        setText(s);
    }

    public JLimitedField(final int maxlength, final String s) {
        super();
        if (maxlength <= 0) {
            length = 0;
        } else {
            length = maxlength;
        }
        setText(s);
    }

    @Override
    public void setText(final String s) {
        if (s == null) {
            return;
        }
        if (s.length() <= length) {
            super.setText(s);
        } else {
            super.setText(s.substring(0, length));
        }
    }

    @Override
    protected Document createDefaultModel() {
        return new LimitedLengthDocument(this);
    }

    private final class LimitedLengthDocument extends PlainDocument {

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3257570611449705016L;
        private JLimitedField     jlf;

        public LimitedLengthDocument(final JLimitedField jtf) {
            super();
            jlf = jtf;
            if (jlf == null) {
                throw new NullPointerException();
            }
        }

        @Override
        public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;

            for (int i = 0; i < result.length; i++) {
                if ((length == 0) || (j + jlf.getText().length() < length)) {
                    result[j] = source[i];
                    j++;
                } else {
                    TOOLKIT.beep();
                }
            }
            super.insertString(offs, new String(result, 0, j), a);
        }
    }
}
