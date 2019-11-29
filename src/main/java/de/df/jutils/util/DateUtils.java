package de.df.jutils.util;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

    private DateUtils() {
    }

    public static Date get(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(year, month, day);
        return c.getTime();
    }

    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

}