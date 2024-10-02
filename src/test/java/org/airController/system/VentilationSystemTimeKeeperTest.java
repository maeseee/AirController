package org.airController.system;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class VentilationSystemTimeKeeperTest {

    @ParameterizedTest(name = "{index} => onMinutesBeforeNow={0}, offMinutesBeforeNow={1}, expectedResult={2}")
    @CsvSource({
            "60, 0, 60",
            "70, -10, 60",
            "70, 20, 40",
            "40, 0, 40",
    })
    void shouldReturnOnDurationInTheLastHour(int onMinutesBeforeNow, int offMinutesBeforeNow, long expectedResult) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime onTime = now.minusMinutes(onMinutesBeforeNow);
        final LocalDateTime offTime = now.minusMinutes(offMinutesBeforeNow);
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime)
                    .thenReturn(offTime)
                    .thenReturn(now);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
        }

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(expectedResult, Offset.offset(1L));
    }

    @Test
    void shouldReturnDurationInLastHourWithOnGoingOnTime() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime onTime = now.minusMinutes(30);
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime)
                    .thenReturn(now);
            testee.setAirFlowOn(true);
        }

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(30, Offset.offset(1L));
    }

    @Test
    void shouldCountTwoTimePeriods() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime onTime1 = now.minusMinutes(50);
        final LocalDateTime offTime1 = now.minusMinutes(40);
        final LocalDateTime onTime2 = now.minusMinutes(20);
        final LocalDateTime offTime2 = now.minusMinutes(10);
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime1)
                    .thenReturn(offTime1)
                    .thenReturn(onTime2)
                    .thenReturn(offTime2)
                    .thenReturn(now);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
        }

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(20, Offset.offset(1L));
    }

    @ParameterizedTest(name = "{index} => minusStartDay={0}, minusEndDay={1}, expectedDuration={2}")
    @CsvSource({
            "1, 1, 10", // 12:00 to 12:10
            "0, 0, 0",
            "2, 2, 0",
            "2, 1, 730", // 00:00 to 12:10
            "1, 0, 720", // 12:00 to 00:00
    })
    void shouldReturnReportFromYesterday(int minusStartDay, int minusEndDay, int expectedDuration) {
        final LocalDateTime now = LocalDateTime.of(2024, 9, 17, 12, 0);
        final LocalDateTime onTime = now.minusDays(minusStartDay);
        final LocalDateTime offTime = now.minusDays(minusEndDay).plusMinutes(10);
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime)
                    .thenReturn(offTime);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
        }
        final LocalDate yesterday = now.minusDays(1).toLocalDate();

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toMinutes()).isCloseTo(expectedDuration, Offset.offset(1L));
    }

    @Test
    void shouldReturnReportFromYesterdayWithOnGoingTime() {
        final LocalDateTime onTime = LocalDateTime.of(2024, 9, 17, 22, 0);
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime);
            testee.setAirFlowOn(true);
        }

        final Duration result = testee.getTotalAirFlowFromDay(onTime.toLocalDate());

        assertThat(result.toMinutes()).isCloseTo(120, Offset.offset(1L));
    }

    @Test
    void shouldRemovePeriodsFromTwoDaysAgo() {
        final LocalDateTime now = LocalDateTime.of(2024, 9, 30, 12, 0);
        final LocalDateTime onTimeToday = now.minusMinutes(10);
        final LocalDateTime onTime2DaysAgo = onTimeToday.minusDays(2);
        final LocalDateTime offTime2DaysAgo = onTime2DaysAgo.plusMinutes(10);
        final LocalDateTime onTimeYesterday = onTimeToday.minusDays(1);
        final LocalDateTime offTimeYesterday = onTimeYesterday.plusMinutes(10);
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime2DaysAgo)
                    .thenReturn(offTime2DaysAgo)
                    .thenReturn(onTimeYesterday)
                    .thenReturn(offTimeYesterday)
                    .thenReturn(onTimeToday)
                    .thenReturn(now)
                    .thenReturn(now);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
            testee.setAirFlowOn(true);
        }
        Duration result2DayAgo = testee.getTotalAirFlowFromDay(onTime2DaysAgo.toLocalDate());
        Duration resultYesterday = testee.getTotalAirFlowFromDay(onTimeYesterday.toLocalDate());
        assertThat(result2DayAgo.toMinutes()).isCloseTo(10, Offset.offset(1L));
        assertThat(resultYesterday.toMinutes()).isCloseTo(10, Offset.offset(1L));

        testee.removeTimePeriods(onTimeYesterday.toLocalDate());

        result2DayAgo = testee.getTotalAirFlowFromDay(onTime2DaysAgo.toLocalDate());
        resultYesterday = testee.getTotalAirFlowFromDay(onTimeYesterday.toLocalDate());
        assertThat(result2DayAgo.toMinutes()).isEqualTo(0);
        assertThat(resultYesterday.toMinutes()).isCloseTo(10, Offset.offset(1L));
    }
}