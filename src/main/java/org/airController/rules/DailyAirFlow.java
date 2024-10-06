package org.airController.rules;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;

public class DailyAirFlow implements Rule {

    private static final MonthDay SUMMER_TIME_START = MonthDay.of(5, 10);
    private static final MonthDay SUMMER_TIME_END = MonthDay.of(9, 10);
    private static final LocalTime SUMMER_ON_TIME = LocalTime.of(4, 0, 0);

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
        double cosConfidence = getCosinusOfDailyTemperatur(now);
        return Math.abs(cosConfidence) > Math.sin(Math.PI / 4.0) ? cosConfidence : 0.0;
    }

    private double getCosinusOfDailyTemperatur(LocalTime timeNow) {
        double m = 2.0 * Math.PI / Duration.ofDays(1).toHours();
        double b = 2.0 * Math.PI * -SUMMER_ON_TIME.getHour() / Duration.ofDays(1).toHours();
        return Math.cos(m * timeNow.getHour() + b);
    }
}
