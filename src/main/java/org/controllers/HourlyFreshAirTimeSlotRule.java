package org.controllers;

import java.time.LocalTime;

class HourlyFreshAirTimeSlotRule {

    public static final int HOURLY_FRESH_AIR_MINUTES = 10;

    public boolean turnFreshAirOn(LocalTime timeNow) {
        final int minute = timeNow.getMinute();
        return minute < HOURLY_FRESH_AIR_MINUTES;
    }
}
