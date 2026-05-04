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
            return isInQuarterForYearWrap(date);
        }
        if (isOnEdgeDay(date)) {
            return true;
        }
        return isBetweenLowAndPeakDate(date);
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

    private boolean isOnEdgeDay(MonthDay date) {
        return lowFactorDate.equals(date) || peakFactorDate.equals(date);
    }

    private boolean isInQuarterForYearWrap(MonthDay date) {
        return peakFactorDate.isAfter(date) || lowFactorDate.isBefore(date);
    }

    private boolean isBetweenLowAndPeakDate(MonthDay date) {
        if (lowFactorDate.isBefore(peakFactorDate)) {
            return isBetween(lowFactorDate, peakFactorDate, date);
        } else {
            return isBetween(peakFactorDate, lowFactorDate, date);
        }
    }

    private boolean isBetween(MonthDay startDate, MonthDay EndDate, MonthDay testDate) {
        return startDate.isBefore(testDate) && EndDate.isAfter(testDate);
    }
}
