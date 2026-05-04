package org.air_controller.rules.airflow;

import java.time.MonthDay;
import java.time.temporal.ChronoUnit;

public class YearPeriod {
    private static final int REFERENCE_YEAR = 2026;

    private final MonthDay lowFactorDate;
    private final MonthDay peakFactorDate;
    private final double factor;

    public YearPeriod(MonthDay lowFactorDate, MonthDay peakFactorDate, double factor) {
        this.lowFactorDate = lowFactorDate;
        this.peakFactorDate = peakFactorDate;
        this.factor = factor;
    }

    public boolean isInQuarter(MonthDay date) {
        if (isYearWrap(lowFactorDate, peakFactorDate)) {
            return peakFactorDate.isAfter(date) || lowFactorDate.isBefore(date);
        }
        if (lowFactorDate.equals(date) || peakFactorDate.equals(date)) {
            return true;
        }
        if (lowFactorDate.isAfter(peakFactorDate)) {
            return peakFactorDate.isBefore(date) && lowFactorDate.isAfter(date);
        } else {
            return peakFactorDate.isAfter(date) && lowFactorDate.isBefore(date);
        }
    }

    public double getSeasonFactor(MonthDay dateNow) {
        final double totalDays = daysBetween(lowFactorDate, peakFactorDate);
        final double factorRelevantDays = daysBetween(lowFactorDate, dateNow);
        return factor * factorRelevantDays / totalDays;
    }

    private double daysBetween(MonthDay a, MonthDay b) {
        final int reverenceYearOfB = isYearWrap(a, b) ? REFERENCE_YEAR + 1 : REFERENCE_YEAR;
        final long days = Math.abs(ChronoUnit.DAYS.between(a.atYear(REFERENCE_YEAR), b.atYear(reverenceYearOfB)));
        return Math.abs(days);
    }

    private boolean isYearWrap(MonthDay lowFactorDate, MonthDay peakFactorDate) {
        final long daysInBetween = Math.abs(ChronoUnit.DAYS.between(lowFactorDate.atYear(REFERENCE_YEAR), peakFactorDate.atYear(REFERENCE_YEAR)));
        final long daysInHalfAYear = 180;
        return daysInBetween > daysInHalfAYear;
    }
}
