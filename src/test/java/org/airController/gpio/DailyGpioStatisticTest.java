package org.airController.gpio;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class DailyGpioStatisticTest {

    @Test
    void testZeroOnTime() {
        final DailyGpioStatistic testee = new DailyGpioStatistic("Test", false);

        final double onPercentage = testee.stateChange(false, LocalTime.of(6, 0, 0));

        assertThat(onPercentage, is(0.0));
    }

    @Test
    void testAlwaysOnTime() {
        final DailyGpioStatistic testee = new DailyGpioStatistic("Test", true);

        final double onPercentage = testee.stateChange(false, LocalTime.of(6, 0, 0));

        assertThat(onPercentage, is(100.0));
    }

    @Test
    void testPartOn() {
        final DailyGpioStatistic testee = new DailyGpioStatistic("Test", false);

        final double onPercentage1 = testee.stateChange(true, LocalTime.of(6, 0, 0));
        final double onPercentage2 = testee.stateChange(false, LocalTime.of(8, 0, 0));

        assertThat(onPercentage1, is(0.0));
        assertThat(onPercentage2, is(2.0 / 8 * 100));
    }

    @Test
    void testNewDay() {
        final DailyGpioStatistic testee = new DailyGpioStatistic("Test", false);

        final double onPercentage1 = testee.stateChange(true, LocalTime.of(6, 0, 0));
        final double onPercentage2 = testee.stateChange(false, LocalTime.of(8, 0, 0));
        final double onPercentage3 = testee.stateChange(true, LocalTime.of(7, 0, 0));
        final double onPercentage4 = testee.stateChange(false, LocalTime.of(12, 0, 0));

        assertThat(onPercentage1, is(0.0));
        assertThat(onPercentage2, is(2.0 / 8 * 100)); // on 2h, total 8h
        assertThat(onPercentage3, is(0.0));
        assertThat(onPercentage4, is(Math.round(5.0 / 12 * 1000) / 10.0)); // on 5h, total 12h
    }
}