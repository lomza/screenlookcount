package com.totemsoft.screenlookcount.calendar;

import java.util.Calendar;

/**
 * Calendar listener for swipe gesture events (top-bottom, left-right, etc.)
 *
 * @author antonina.tkachuk
 */
public interface CalendarSwipeListener {
    /**
     * Swipe gesture event.
     *
     * @param destinationMonth month to show after the swipe gesture performed
     */
    void onCalendarSwipe(Calendar destinationMonth);
}
