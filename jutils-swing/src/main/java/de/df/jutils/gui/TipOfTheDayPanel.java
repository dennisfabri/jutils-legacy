package de.df.jutils.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class TipOfTheDayPanel extends JPanel {

    private static final long serialVersionUID = 4415201872660395402L;

    private static final int ICON_TEXT_GAP = 12;
    private static final int BUTTON_GAP = 9;
    private static final Insets SYMBOL_BUTTON_MARGIN = new Insets(2, 8, 2, 8);

    private final transient TipOfTheDay tips;
    private final transient TipOfTheDay.ShowOnStartupChoice choice;
    private final JTextArea tipText = new JTextArea(5, 40);
    private final JCheckBox showOnStartup;
    private final JButton close;

    public TipOfTheDayPanel(TipOfTheDay tips, TipOfTheDay.Texts texts, Icon icon,
            TipOfTheDay.ShowOnStartupChoice choice) {
        super(new BorderLayout(10, 10));
        this.tips = tips;
        this.choice = choice;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel heading = new JLabel(texts.didYouKnow(), icon, JLabel.LEADING);
        heading.setIconTextGap(ICON_TEXT_GAP);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, heading.getFont().getSize2D() + 2));

        tipText.setEditable(false);
        tipText.setLineWrap(true);
        tipText.setWrapStyleWord(true);
        tipText.setBackground(UIManager.getColor("TextArea.background"));
        tipText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.add(heading, BorderLayout.NORTH);
        center.add(new JScrollPane(tipText), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        showOnStartup = new JCheckBox(texts.showOnStartup(), choice.isShowingOnStartup());
        JButton previous = new JButton(texts.previous());
        previous.setMargin(SYMBOL_BUTTON_MARGIN);
        previous.addActionListener(e -> {
            tips.previous();
            showCurrentTip();
        });
        JButton next = new JButton(texts.next());
        next.setMargin(SYMBOL_BUTTON_MARGIN);
        next.addActionListener(e -> {
            tips.next();
            showCurrentTip();
        });
        close = new JButton(texts.close());

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(previous);
        buttons.add(Box.createHorizontalStrut(BUTTON_GAP / 3));
        buttons.add(next);
        buttons.add(Box.createHorizontalStrut(BUTTON_GAP));
        buttons.add(close);
        JPanel south = new JPanel(new BorderLayout());
        south.add(showOnStartup, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);

        showCurrentTip();
    }

    private void showCurrentTip() {
        tipText.setText(tips.getCurrentTip());
        tipText.setCaretPosition(0);
    }

    public JButton getCloseButton() {
        return close;
    }

    public void saveChoice() {
        if (showOnStartup.isSelected() != choice.isShowingOnStartup()) {
            choice.setShowingOnStartup(showOnStartup.isSelected());
        }
    }
}
