/*
 * Created on 01.03.2005
 */
package de.df.jutils.gui;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.JPanel;

public class JGlassPanel<T extends Component> extends JPanel {

    private static final long serialVersionUID = 3258128068173312306L;

    private CardLayout layout = new CardLayout();
    private JGlassPane glass = new JGlassPane();
    private T component = null;

    public JGlassPanel(T c) {
        component = c;

        setLayout(layout);
        glass.setVisible(true);

        add(component, "enabled");
        add(glass, "disabled");
        setEnabled(true);
    }

    public T getComponent() {
        return component;
    }

    @Override
    public void setEnabled(boolean enabled) {
        component.setEnabled(enabled);
        layout.show(this, (enabled ? "enabled" : "disabled"));
        super.setEnabled(enabled);
    }

    public JGlassPane getGlassPanel() {
        return glass;
    }
}
