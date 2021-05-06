package de.df.jutils.date;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.df.jutils.util.DateUtils;

class DateTests {

    @Test
    void testGet() {
        Date date = DateUtils.get(2017, 2, 3);
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int year = c.get(Calendar.YEAR);
        assertEquals(2017, year);

        int month = c.get(Calendar.MONTH);
        assertEquals(2, month);

        int day = c.get(Calendar.DAY_OF_MONTH);
        assertEquals(3, day);
    }

    @Test
    void testGetYear() {
        int year = DateUtils.getYear(DateUtils.get(2016, 2, 3));
        assertEquals(2016, year);
    }

    @Test
    void testGetMonth() {
        int month = DateUtils.getMonth(DateUtils.get(2016, 2, 3));
        assertEquals(2, month);
    }

    @Test
    void testGetDay() {
        int day = DateUtils.getDay(DateUtils.get(2016, 2, 3));
        assertEquals(3, day);
    }

}
