package org.air_controller.rules;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;

public class DailyAirFlow implements Rule {

    private static final MonthDay SUMMER_TIME_START = MonthDay.of(5, 10);
    private static final MonthDay SUMMER_TIME_END = MonthDay.of(9, 10);
    private static final LocalTime SUMMER_ON_TIME = LocalTime.of(4, 0, 0); // Local time zone

    @Override
    public String name() {
        return "Daily air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final LocalDateTime now = LocalDateTime.now();
        final double sign = isSummerTime(MonthDay.from(now)) ? 1.0 : -1.0;
        final double summerConfidence = getSummerConfidence(now.toLocalTime());
        return new Confidence(summerConfidence * sign);
    }

    private boolean isSummerTime(MonthDay dateNow) {
        return dateNow.isAfter(SUMMER_TIME_START) && dateNow.isBefore(SUMMER_TIME_END);
    }

    private double getSummerConfidence(LocalTime now) {
        final double cosConfidence = getCosinus(now, Duration.ofDays(1).dividedBy(2));
        final double sign = Math.signum(getCosinus(now, Duration.ofDays(1)));
        return cosConfidence > 0 ? cosConfidence * sign : 0.0;
    }

    private double getCosinus(LocalTime timeNow, Duration period) {
        final double m = 2 * Math.PI / period.toHours();
        final double b = -m * SUMMER_ON_TIME.getHour();
        return Math.cos(m * timeNow.getHour() + b);
    }
}
