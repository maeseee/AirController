package org.airController.rules;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;

public class DailyAirFlow implements Rule {

    public static final double MAX_IMPACT = 0.5;
    private static final MonthDay SUMMER_TIME_START = MonthDay.of(5, 10);
    private static final MonthDay SUMMER_TIME_END = MonthDay.of(9, 10);
    private static final LocalTime SUMMER_ON_TIME = LocalTime.of(4, 0, 0);
    private static final LocalTime WINTER_ON_TIME = LocalTime.of(14, 0, 0);
    private static final Duration DAILY_FRESH_AIR = Duration.ofHours(4);
    private static final double B = MAX_IMPACT; // y = xm + b
    private static final double M = -B / DAILY_FRESH_AIR.toHours() * 2; // y = xm + b

    @Override
    public Percentage getAirFlowNeed() {
        LocalDateTime now = LocalDateTime.now();
        Duration durationToPeak = isSummerTime(MonthDay.from(now)) ? getDurationToPeakInSummer(now.toLocalTime()) : getDurationToPeakInWinter(now.toLocalTime());
        double impact = M * durationToPeak.toHours() + B;
        return new Percentage(impact, -MAX_IMPACT, MAX_IMPACT);
    }

    private boolean isSummerTime(MonthDay dateNow) {
        return dateNow.isAfter(SUMMER_TIME_START) && dateNow.isBefore(SUMMER_TIME_END);
    }

    private Duration getDurationToPeakInSummer(LocalTime timeNow) {
        return Duration.between(timeNow, SUMMER_ON_TIME);
    }

    private Duration getDurationToPeakInWinter(LocalTime timeNow) {
        return Duration.between(timeNow, WINTER_ON_TIME);
    }
}
