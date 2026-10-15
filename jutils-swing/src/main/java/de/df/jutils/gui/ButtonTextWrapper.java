package de.df.jutils.gui;

import java.awt.FontMetrics;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ButtonTextWrapper {

    private static final Pattern BREAK_CANDIDATE = Pattern.compile("\\s+[-\u2013\u2014]\\s+|\\s");

    private ButtonTextWrapper() {
        // Hide
    }

    static List<String> wrap(String text, FontMetrics metrics, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return List.of("");
        }
        if (metrics.stringWidth(text) <= maxWidth) {
            return List.of(text);
        }
        boolean found = false;
        int bestStart = 0;
        int bestEnd = 0;
        double bestDistance = Double.MAX_VALUE;
        double middle = text.length() / 2.0;
        Matcher matcher = BREAK_CANDIDATE.matcher(text);
        while (matcher.find()) {
            double position = (matcher.start() + matcher.end() - 1) / 2.0;
            double distance = Math.abs(position - middle);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestStart = matcher.start();
                bestEnd = matcher.end();
                found = true;
            }
        }
        if (!found) {
            return List.of(text);
        }
        return List.of(text.substring(0, bestStart), text.substring(bestEnd));
    }

    static String toHtml(List<String> lines) {
        StringBuilder html = new StringBuilder("<html><center>");
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                html.append("<br>");
            }
            html.append(escape(lines.get(i)));
        }
        return html.append("</center></html>").toString();
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
