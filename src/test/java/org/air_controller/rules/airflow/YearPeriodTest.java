package org.air_controller.rules.airflow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.MonthDay;

import static org.assertj.core.api.Assertions.assertThat;

class YearPeriodTest {

    @ParameterizedTest(name = "{index} => Month={0}, expectedResult={1}")
    @CsvSource({
            "2, false",
            "5, true",
            "8, false"
    })
    void shouldCheckIfInQuarter(int month, boolean expectedResult) {
        final MonthDay start = MonthDay.of(3, 1);
        final MonthDay end = MonthDay.of(7, 1);
        final YearPeriod testee = new YearPeriod(start, end, 1);

        final boolean result = testee.isInQuarter(MonthDay.of(month, 15));

        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest(name = "{index} => Month={0}, expectedResult={1}")
    @CsvSource({
            "6, false",
            "8, true",
            "11, false"
    })
    void shouldCheckIfInQuarterWhenPeakDateFirst(int month, boolean expectedResult) {
        final MonthDay start = MonthDay.of(7, 1);
        final MonthDay end = MonthDay.of(10, 1);
        final YearPeriod testee = new YearPeriod(end, start, 1);

        final boolean result = testee.isInQuarter(MonthDay.of(month, 15));

        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest(name = "{index} => Month={0}, expectedResult={1}")
    @CsvSource({
            "10, false",
            "12, true",
            "1, true",
            "3, false"
    })
    void shouldCheckIfInQuarterYearWarp(int month, boolean expectedResult) {
        final MonthDay start = MonthDay.of(11, 1);
        final MonthDay end = MonthDay.of(2, 1);
        final YearPeriod testee = new YearPeriod(start, end, 1);

        final boolean result = testee.isInQuarter(MonthDay.of(month, 15));

        assertThat(result).isEqualTo(expectedResult);
    }
}