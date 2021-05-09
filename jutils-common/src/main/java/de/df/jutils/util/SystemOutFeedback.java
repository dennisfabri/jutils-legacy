/*
 * Created on 12.01.2006
 */
package de.df.jutils.util;

public class SystemOutFeedback implements Feedback {
    @Override
    public void showFeedback(String text) {
        System.out.println(text);
    }
}