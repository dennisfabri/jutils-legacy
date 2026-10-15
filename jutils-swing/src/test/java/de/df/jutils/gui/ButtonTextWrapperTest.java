package de.df.jutils.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.FontMetrics;
import java.util.List;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;

class ButtonTextWrapperTest {

    private final FontMetrics metrics = new JButton().getFontMetrics(new JButton().getFont());

    private List<String> wrapNarrow(String text) {
        return ButtonTextWrapper.wrap(text, metrics, metrics.stringWidth(text) - 1);
    }

    @Test
    void textThatFitsIsNotChanged() {
        String text = "DLRG 2026";

        assertEquals(List.of(text), ButtonTextWrapper.wrap(text, metrics, metrics.stringWidth(text)));
    }

    @Test
    void shortTextWithDashKeepsTheDashWhenItFits() {
        assertEquals(List.of("DLRG - 2026"), ButtonTextWrapper.wrap("DLRG - 2026", metrics, 1000));
    }

    @Test
    void aTooWideTextIsBrokenOnceAtTheSpaceNearestToTheMiddle() {
        assertEquals(List.of("one two three", "four five six"), wrapNarrow("one two three four five six"));
    }

    @Test
    void aSpacedDashNearestToTheMiddleIsTheBreakAndIsDropped() {
        assertEquals(List.of("International", "Ocean Mixed"), wrapNarrow("International - Ocean Mixed"));
    }

    @Test
    void aSpaceCanBeNearerToTheMiddleThanADash() {
        assertEquals(List.of("Alpha Beta", "Gamma - Delta"), wrapNarrow("Alpha Beta Gamma - Delta"));
    }

    @Test
    void onlyOneBreakIsInsertedEvenIfSeveralDashesExist() {
        assertEquals(List.of("International", "Ocean — Pool"), wrapNarrow("International – Ocean — Pool"));
    }

    @Test
    void ifTwoSpacesAreEquallyNearTheEarlierOneIsUsed() {
        assertEquals(List.of("ab", "cd e"), wrapNarrow("ab cd e"));
    }

    @Test
    void aHyphenWithoutSpacesIsNoBreak() {
        assertEquals(List.of("Ocean-Pool"), wrapNarrow("Ocean-Pool"));
    }

    @Test
    void aSingleWordIsNeverBroken() {
        assertEquals(List.of("Unterschiedlichkeiten"), wrapNarrow("Unterschiedlichkeiten"));
    }

    @Test
    void htmlContainsTheLinesSeparatedByBreaksAndIsEscaped() {
        assertEquals("<html><center>A &amp; B<br>&lt;C&gt;</center></html>",
                ButtonTextWrapper.toHtml(List.of("A & B", "<C>")));
    }
}
