package de.df.jutils.gui;

import java.util.List;

public class TipOfTheDay {

    public record Texts(String title, String didYouKnow, String showOnStartup, String previous, String next,
            String close) {
    }

    public interface ShowOnStartupChoice {

        boolean isShowingOnStartup();

        void setShowingOnStartup(boolean showOnStartup);
    }

    private final List<String> tips;
    private int current = 0;

    public TipOfTheDay(List<String> tips) {
        this.tips = List.copyOf(tips);
    }

    public boolean isEmpty() {
        return tips.isEmpty();
    }

    public int getCurrentIndex() {
        return current;
    }

    public void setCurrentIndex(int index) {
        current = tips.isEmpty() ? 0 : Math.floorMod(index, tips.size());
    }

    public String getCurrentTip() {
        return tips.isEmpty() ? "" : tips.get(current);
    }

    public void next() {
        setCurrentIndex(current + 1);
    }

    public void previous() {
        setCurrentIndex(current - 1);
    }
}
