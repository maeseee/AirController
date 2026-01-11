package org.air_controller.rules;

import java.time.*;
import java.time.temporal.ChronoUnit;

class DailyAirFlow implements Rule {

    private static final MonthDay SUMMER_TIME_START = MonthDay.of(5, 10);
    private static final MonthDay SUMMER_TIME_END = MonthDay.of(9, 21);
    private static final Duration transitionalSeason = Duration.ofDays(20);
    private static final LocalTime HEAT_PEAK_TIME_UTC = LocalTime.of(2, 0, 0);
    private static final double CONFIDENCE_WEIGHT = 0.6;

    @Override
    public String name() {
        return "Daily air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final double seasonFactor = calculateSeasonFactor(MonthDay.from(now));
        final double confidence = getCosinus(now.toLocalTime());
        return new Confidence(confidence * seasonFactor, CONFIDENCE_WEIGHT);
    }

    private double calculateSeasonFactor(MonthDay dateNow) {
        final int year = 2025;
        final LocalDate now = dateNow.atYear(year);
        final LocalDate summerStart = SUMMER_TIME_START.atYear(year);
        final LocalDate summerEnd = SUMMER_TIME_END.atYear(year);

        final double springSeasonFactor = getSeasonFactor(summerStart, now);
        final double autumnSeasonFactor = getSeasonFactor(now, summerEnd);
        return calculateFactor(springSeasonFactor, autumnSeasonFactor);
    }

    private double getSeasonFactor(LocalDate dateStart, LocalDate dateEnd) {
        double daysSinceSummerStart = ChronoUnit.DAYS.between(dateStart, dateEnd);
        return daysSinceSummerStart / (double) transitionalSeason.toDays();
    }

    private double calculateFactor(double springSeasonFactor, double autumnSeasonFactor) {
        final double minSeasonFactor = Math.min(Math.abs(springSeasonFactor), Math.abs(autumnSeasonFactor));
        final double sign = Math.signum(springSeasonFactor * autumnSeasonFactor);
        return Math.min(minSeasonFactor, 1.0) * sign;
    }

    private double getCosinus(LocalTime timeNow) {
        final Duration diffToPeak = Duration.between(HEAT_PEAK_TIME_UTC, timeNow);
        final double hoursToPeak = diffToPeak.getSeconds() / 60.0 / 60.0;
        final Duration cosPeriodDuration = Duration.ofDays(1);
        final double b = 2 * Math.PI / cosPeriodDuration.toHours();
        return Math.cos(b * hoursToPeak);
    }
}
