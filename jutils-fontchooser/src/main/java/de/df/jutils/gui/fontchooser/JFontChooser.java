package de.df.jutils.gui.fontchooser;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.i18n.SafeTextProcessor;
import de.df.jutils.resourcebundle.IdentityResourceBundle;
import de.df.jutils.resourcebundle.MultipleResourceBundle;
import de.df.jutils.resourcebundle.SafeResourceBundle;

/**
 * Swing component for choosing a font: a filterable list of font families, the
 * styles of the font, a size (spinner and list) and a preview below.
 * <p>
 * The selected font is bound property {@value #SELECTED_FONT_CHANGED_KEY}.
 */
public class JFontChooser extends JComponent {

    private static final long serialVersionUID = 1L;

    public static final String SELECTED_FONT_CHANGED_KEY = "selectedFont";

    static final String BUNDLE = "de.df.jutils.gui.fontchooser.fontchooser";

    private static final int[] SIZES = { 6, 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72 };
    private static final String[] STYLE_KEYS = { "Regular", "Bold", "Italic", "BoldItalic" };
    private static final String DEFAULT_FAMILY = "Dialog";
    private static final int FALLBACK_SIZE = 12;

    private final List<String> allFamilies = new ArrayList<>();
    private final DefaultListModel<String> familyModel = new DefaultListModel<>();
    private final DefaultListModel<String> styleModel = new DefaultListModel<>();
    private final DefaultListModel<Integer> sizeModel = new DefaultListModel<>();

    private final JLabel fontLabel = new JLabel();
    private final JLabel styleLabel = new JLabel();
    private final JLabel sizeLabel = new JLabel();
    private final JTextField filterField = new JTextField();
    private final JList<String> familyList = new JList<>(familyModel);
    private final JList<String> styleList = new JList<>(styleModel);
    private final JList<Integer> sizeList = new JList<>(sizeModel);
    private final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(FALLBACK_SIZE, 1, 999, 1));
    private final JTextArea preview = new JTextArea();
    private final JPanel previewPanel = new JPanel(new BorderLayout());

    private String family = DEFAULT_FAMILY;
    private int style = Font.PLAIN;
    private int size = lookAndFeelSize();
    private Font selectedFont = new Font(family, style, size);
    private boolean updating;
    private SafeTextProcessor texts;

    /**
     * Creates a chooser with the first of the given font names that is available.
     * Without a match the default font is selected: Dialog, plain, in the dialog font size of the look and feel.
     */
    public JFontChooser(String... preferredFontNames) {
        for (int s : SIZES) {
            sizeModel.addElement(s);
        }
        buildUi();
        loadFamilies();
        applyTexts();
        setSelectedFontName(preferredFontNames);
        apply(family, style, size);
    }

    private static int lookAndFeelSize() {
        Font dialogFont = UIManager.getFont("Label.font");
        return dialogFont == null ? FALLBACK_SIZE : dialogFont.getSize();
    }

    static ResourceBundle bundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE, locale, JFontChooser.class.getClassLoader(),
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
    }

    public Font getSelectedFont() {
        return selectedFont;
    }

    public void setSelectedFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("font must not be null");
        }
        apply(font.getFamily(), font.getStyle() & (Font.BOLD | Font.ITALIC), font.getSize());
    }

    /**
     * Selects the first of the given font family names that is available (ignoring
     * case and surrounding whitespace). Style and size are kept.
     *
     * @return true if one of the names was found
     */
    public boolean setSelectedFontName(String... preferredFontNames) {
        if (preferredFontNames == null) {
            return false;
        }
        for (String name : preferredFontNames) {
            if (name == null || name.isBlank()) {
                continue;
            }
            for (String available : allFamilies) {
                if (available.equalsIgnoreCase(name.trim())) {
                    apply(available, style, size);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setLocale(Locale l) {
        super.setLocale(l);
        if (texts != null) {
            loadFamilies();
            applyTexts();
        }
    }

    private void buildUi() {
        familyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        familyList.setVisibleRowCount(10);
        familyList.setPrototypeCellValue("MMMMMMMMMMMMMMMMMMMM");
        styleList.setPrototypeCellValue("MMMMMMMMMMMM");
        sizeList.setPrototypeCellValue(999);

        preview.setLineWrap(true);
        preview.setWrapStyleWord(true);
        preview.setEditable(false);
        preview.setFocusable(false);
        preview.setOpaque(false);
        JScrollPane previewScroll = new JScrollPane(preview, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        previewScroll.setBorder(null);
        previewScroll.setOpaque(false);
        previewScroll.getViewport().setOpaque(false);
        previewPanel.add(previewScroll, BorderLayout.CENTER);

        setLayout(new FormLayout("fill:default:grow,4dlu,fill:default,4dlu,fill:max(40dlu;default)",
                "default,2dlu,default,2dlu,fill:default:grow,4dlu,fill:60dlu"));
        add(fontLabel, CC.xy(1, 1));
        add(styleLabel, CC.xy(3, 1));
        add(sizeLabel, CC.xy(5, 1));
        add(filterField, CC.xy(1, 3));
        add(new JScrollPane(styleList), CC.xywh(3, 3, 1, 3));
        add(sizeSpinner, CC.xy(5, 3));
        add(new JScrollPane(familyList), CC.xy(1, 5));
        add(new JScrollPane(sizeList), CC.xy(5, 5));
        add(previewPanel, CC.xyw(1, 7, 5));

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
        filterField.addActionListener(e -> {
            if (!familyModel.isEmpty()) {
                apply(familyModel.get(0), style, size);
            }
        });
        familyList.addListSelectionListener(e -> {
            if (!updating && !e.getValueIsAdjusting() && familyList.getSelectedValue() != null) {
                apply(familyList.getSelectedValue(), style, size);
            }
        });
        styleList.addListSelectionListener(e -> {
            if (!updating && !e.getValueIsAdjusting() && styleList.getSelectedIndex() >= 0) {
                apply(family, styleList.getSelectedIndex(), size);
            }
        });
        sizeList.addListSelectionListener(e -> {
            if (!updating && !e.getValueIsAdjusting() && sizeList.getSelectedValue() != null) {
                apply(family, style, sizeList.getSelectedValue());
            }
        });
        sizeSpinner.addChangeListener(e -> {
            if (!updating) {
                apply(family, style, ((Number) sizeSpinner.getValue()).intValue());
            }
        });
    }

    private void loadFamilies() {
        allFamilies.clear();
        allFamilies.addAll(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        Collator collator = Collator.getInstance(getLocale());
        allFamilies.sort(collator);
        applyFilter();
    }

    private void applyFilter() {
        String filter = filterField.getText().trim().toLowerCase(Locale.ROOT);
        boolean old = updating;
        updating = true;
        try {
            familyModel.clear();
            for (String f : allFamilies) {
                if (filter.isEmpty() || f.toLowerCase(Locale.ROOT).contains(filter)) {
                    familyModel.addElement(f);
                }
            }
            syncFamilySelection();
        } finally {
            updating = old;
        }
    }

    private void syncFamilySelection() {
        int index = familyModel.indexOf(family);
        if (index >= 0) {
            familyList.setSelectedIndex(index);
            familyList.ensureIndexIsVisible(index);
        } else {
            familyList.clearSelection();
        }
    }

    private void applyTexts() {
        Locale locale = getLocale();
        MultipleResourceBundle rb = new MultipleResourceBundle();
        try {
            rb.add(bundle(locale));
        } catch (RuntimeException re) {
            // Falls back to the identity bundle
        }
        rb.add(new IdentityResourceBundle());
        texts = new SafeTextProcessor(new SafeResourceBundle(rb));

        fontLabel.setText(text("Font"));
        styleLabel.setText(text("Style"));
        sizeLabel.setText(text("Size"));
        filterField.setToolTipText(text("Filter"));
        filterField.putClientProperty("JTextField.placeholderText", text("Filter"));
        previewPanel.setBorder(BorderFactory.createTitledBorder(text("Preview")));
        preview.setText(text("PreviewText"));
        preview.setCaretPosition(0);

        boolean old = updating;
        updating = true;
        try {
            styleModel.clear();
            for (String key : STYLE_KEYS) {
                styleModel.addElement(text(key));
            }
            styleList.setSelectedIndex(style);
        } finally {
            updating = old;
        }
    }

    private String text(String key) {
        return texts.process(key, new Object[0]);
    }

    private void apply(String newFamily, int newStyle, int newSize) {
        if (updating) {
            return;
        }
        newSize = Math.max(1, Math.min(999, newSize));
        updating = true;
        try {
            family = newFamily;
            style = newStyle;
            size = newSize;
            syncFamilySelection();
            styleList.setSelectedIndex(style);
            sizeSpinner.setValue(size);
            int sizeIndex = sizeModel.indexOf(size);
            if (sizeIndex >= 0) {
                sizeList.setSelectedIndex(sizeIndex);
                sizeList.ensureIndexIsVisible(sizeIndex);
            } else {
                sizeList.clearSelection();
            }
            Font font = new Font(family, style, size);
            preview.setFont(font);
            Font old = selectedFont;
            selectedFont = font;
            firePropertyChange(SELECTED_FONT_CHANGED_KEY, old, font);
        } finally {
            updating = false;
        }
    }

    // Accessors for tests
    JList<String> getStyleList() {
        return styleList;
    }

    JList<Integer> getSizeList() {
        return sizeList;
    }

    JSpinner getSizeSpinner() {
        return sizeSpinner;
    }

    JTextArea getPreview() {
        return preview;
    }

    JLabel getSizeLabel() {
        return sizeLabel;
    }

    JTextField getFilterField() {
        return filterField;
    }

    List<String> getVisibleFamilies() {
        return Collections.list(familyModel.elements());
    }
}
