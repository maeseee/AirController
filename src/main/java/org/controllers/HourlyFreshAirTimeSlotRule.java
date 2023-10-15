package org.controllers;

import java.time.LocalTime;
import java.time.MonthDay;

public class HourlyFreshAirTimeSlotRule implements FreshAirRule {

    public static final int HOURLY_FRESH_AIR_MINUTES = 10;

    private final LocalTime timeNow;

    public HourlyFreshAirTimeSlotRule(LocalTime timeNow) {
        this.timeNow = timeNow;
    }

    @Override
    public boolean turnFreshAirOn() {
        final int minute = timeNow.getMinute();
        return minute < HOURLY_FRESH_AIR_MINUTES;
    }
}
