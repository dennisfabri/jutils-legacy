/*
 * Created on 01.04.2006
 */
package de.df.jutils.io.csv;

import de.df.jutils.util.StringTools;

public class Seconds {

    private int seconds = 0;

    public Seconds(int s) {
        seconds = s;
    }

    public int getSeconds() {
        return seconds;
    }

    String getString(char sep) {
        return StringTools.zeitString(seconds, sep);
    }

    @Override
    public String toString() {
        return getString('.');
    }
}