package org.controllers;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;

public class MainFreshAirTimeSlotRule implements FreshAirRule {

    private static final MonthDay SUMMER_TIME_START = MonthDay.of(5, 10);
    private static final MonthDay SUMMER_TIME_END = MonthDay.of(9, 10);
    private static final LocalTime SUMMER_START_AIR_TIME = LocalTime.of(1,30,0);
    private static final LocalTime SUMMER_END_AIR_TIME = LocalTime.of(7,0,0);
    private static final LocalTime WINTER_START_AIR_TIME = LocalTime.of(12,30,0);
    private static final LocalTime WINTER_END_AIR_TIME = LocalTime.of(17,0,0);

    private final MonthDay dateNow;
    private final LocalTime timeNow;

    public MainFreshAirTimeSlotRule(LocalDateTime now) {
        this.dateNow = MonthDay.from(now);
        this.timeNow = now.toLocalTime();
    }

    @Override
    public boolean turnFreshAirOn() {
        return isSummerTime() ? freshAirOnSummerTime() : freshAirOnWinterTime();
    }

    private boolean isSummerTime() {
        return dateNow.isAfter(SUMMER_TIME_START) && dateNow.isBefore(SUMMER_TIME_END);
    }

    private boolean freshAirOnSummerTime() {
        return timeNow.isAfter(SUMMER_START_AIR_TIME) && timeNow.isBefore(SUMMER_END_AIR_TIME);
    }

    private boolean freshAirOnWinterTime() {
        return timeNow.isAfter(WINTER_START_AIR_TIME) && timeNow.isBefore(WINTER_END_AIR_TIME);
    }
}
