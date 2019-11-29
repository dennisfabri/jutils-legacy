/*
 * Created on 15.04.2005
 */
package de.df.jutils.gui.wizard;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class WizardEmptyPage extends AWizardPage {

    public WizardEmptyPage(String title, String note) {
        super(title, note);
    }

    @Override
    public JComponent getPage() {
        return new JPanel();
    }
}