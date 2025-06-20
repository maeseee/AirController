package org.air_controller.rules;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;

class DailyAirFlow implements Rule {

    private static final MonthDay SUMMER_TIME_START = MonthDay.of(5, 10);
    private static final MonthDay SUMMER_TIME_END = MonthDay.of(9, 10);
    private static final LocalTime HEAT_PEAK_TIME_UTC = LocalTime.of(2, 0, 0); // TODO refactor to set type to utc (Synology reads only UTC)

    @Override
    public String name() {
        return "Daily air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final LocalDateTime now = LocalDateTime.now();
        final double sign = isSummer(MonthDay.from(now)) ? 1.0 : -1.0;
        final double confidence = getCosinus(now.toLocalTime());
        return new Confidence(confidence * sign, 0.5);
    }

    private boolean isSummer(MonthDay dateNow) {
        return dateNow.isAfter(SUMMER_TIME_START) && dateNow.isBefore(SUMMER_TIME_END);
    }


    private double getCosinus(LocalTime timeNow) {
        final Duration diffToPeak = Duration.between(HEAT_PEAK_TIME_UTC, timeNow);
        final double hoursToPeak = diffToPeak.getSeconds() / 60.0 / 60.0;
        final Duration cosPeriodDuration = Duration.ofDays(1);
        final double b = 2 * Math.PI / cosPeriodDuration.toHours();
        return Math.cos(b * hoursToPeak);
    }
}
