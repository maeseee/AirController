package org.air_controller.rules.airflow;

import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;

public class QuarterYear {
    private static final MonthDay COOLEST_DAY = MonthDay.of(1, 24);
    private static final MonthDay SPRING_TRANSITION_DATE = MonthDay.of(5, 10);
    private static final MonthDay HOTTEST_DAY = MonthDay.of(7, 24);
    private static final MonthDay AUTUMN_TRANSITION_DATE = MonthDay.of(9, 22);

    private final List<YearPeriod> periods = createPeriods();

    public double getSeasonFactor(MonthDay dateNow) {
        final YearPeriod yearPeriod = getYearPeriod(dateNow);
        return yearPeriod.getSeasonFactor(dateNow);
    }

    private YearPeriod getYearPeriod(MonthDay dateNow) {
        return periods.stream()
                .filter(yearPeriod -> yearPeriod.isInQuarter(dateNow))
                .findFirst()
                .orElseThrow();
    }

    private List<YearPeriod> createPeriods() {
        final List<YearPeriod> periods = new ArrayList<>();
        periods.add(new YearPeriod(SPRING_TRANSITION_DATE, COOLEST_DAY, -1.0));
        periods.add(new YearPeriod(SPRING_TRANSITION_DATE, HOTTEST_DAY, 1.0));
        periods.add(new YearPeriod(AUTUMN_TRANSITION_DATE, HOTTEST_DAY, 1.0));
        periods.add(new YearPeriod(AUTUMN_TRANSITION_DATE, COOLEST_DAY, -1.0));
        return periods;
    }
}
