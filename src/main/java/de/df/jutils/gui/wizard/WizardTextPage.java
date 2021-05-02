/*
 * Created on 15.04.2005
 */
package de.df.jutils.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.df.jutils.gui.border.BorderUtils;

public class WizardTextPage extends AWizardPage {

    final class JWizardPanel extends JPanel {

        private static final long serialVersionUID = 971516187521210601L;

        JWizardPanel() {
            super(new BorderLayout(5, 5));
        }

        @Override
        public void requestFocus() {
            super.requestFocus();
            text.requestFocus();
        }
    }

    JTextArea      text  = new JTextArea();
    private JPanel panel = new JWizardPanel();

    public WizardTextPage(String title, String note, String info, int width, int height) {
        super(title, note);
        text.setText(info);

        JScrollPane scroller = new JScrollPane(text);
        scroller.setPreferredSize(new Dimension(width, height));
        scroller.setBorder(null);

        panel.setBorder(BorderUtils.createSpaceBorder());
        panel.add(scroller, BorderLayout.CENTER);
    }

    public WizardTextPage(String title, String info, String note) {
        this(title, info, note, 300, 200);
    }

    @Override
    public JComponent getPage() {
        return panel;
    }
}