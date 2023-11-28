package org.airController.controllers;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;

class DailyFreshAirRule {

    private static final MonthDay SUMMER_TIME_START = MonthDay.of(5, 10);
    private static final MonthDay SUMMER_TIME_END = MonthDay.of(9, 10);
    private static final LocalTime SUMMER_START_AIR_TIME = LocalTime.of(2, 0, 0);
    private static final LocalTime SUMMER_END_AIR_TIME = LocalTime.of(6, 0, 0);
    private static final LocalTime WINTER_START_AIR_TIME = LocalTime.of(13, 0, 0);
    private static final LocalTime WINTER_END_AIR_TIME = LocalTime.of(17, 0, 0);

    public boolean turnFreshAirOn(LocalDateTime now) {
        return isSummerTime(MonthDay.from(now)) ? freshAirOnSummerTime(now.toLocalTime()) : freshAirOnWinterTime(now.toLocalTime());
    }

    private boolean isSummerTime(MonthDay dateNow) {
        return dateNow.isAfter(SUMMER_TIME_START) && dateNow.isBefore(SUMMER_TIME_END);
    }

    private boolean freshAirOnSummerTime(LocalTime timeNow) {
        return timeNow.isAfter(SUMMER_START_AIR_TIME) && timeNow.isBefore(SUMMER_END_AIR_TIME);
    }

    private boolean freshAirOnWinterTime(LocalTime timeNow) {
        return timeNow.isAfter(WINTER_START_AIR_TIME) && timeNow.isBefore(WINTER_END_AIR_TIME);
    }
}
