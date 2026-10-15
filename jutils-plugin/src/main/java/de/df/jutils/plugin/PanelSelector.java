package de.df.jutils.plugin;

import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import de.df.jutils.gui.JButtonBar;

final class PanelSelector {

    private final JButtonBar bar = new JButtonBar(SwingConstants.VERTICAL);
    private final ButtonGroup group = new ButtonGroup();
    private final List<PanelInfo> infos = new ArrayList<>();
    private final Map<PanelInfo, JToggleButton> buttons = new IdentityHashMap<>();
    private Consumer<PanelInfo> selectionListener = info -> {
    };

    JButtonBar getComponent() {
        return bar;
    }

    void setSelectionListener(Consumer<PanelInfo> selectionListener) {
        this.selectionListener = selectionListener;
    }

    void addLast(PanelInfo info) {
        add(info, infos.size());
    }

    void add(PanelInfo info, int position) {
        JToggleButton button = new JToggleButton(info.getName(), info.getIcon());
        button.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectionListener.accept(info);
            }
        });
        infos.add(position, info);
        buttons.put(info, button);
        group.add(button);
        bar.add(button, position);
        bar.revalidate();
        bar.repaint();
    }

    void remove(PanelInfo info) {
        JToggleButton button = buttons.remove(info);
        if (button == null) {
            return;
        }
        infos.remove(info);
        group.remove(button);
        bar.remove(button);
        bar.revalidate();
        bar.repaint();
    }

    boolean contains(PanelInfo info) {
        return buttons.containsKey(info);
    }

    int size() {
        return infos.size();
    }

    PanelInfo getElementAt(int index) {
        return infos.get(index);
    }

    List<PanelInfo> getAllElements() {
        return Collections.unmodifiableList(infos);
    }

    PanelInfo getSelected() {
        for (PanelInfo info : infos) {
            if (buttons.get(info).isSelected()) {
                return info;
            }
        }
        return null;
    }

    void select(PanelInfo info) {
        JToggleButton button = buttons.get(info);
        if (button != null) {
            button.setSelected(true);
        }
    }
}
