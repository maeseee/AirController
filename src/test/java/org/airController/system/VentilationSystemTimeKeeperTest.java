package org.airController.system;

import org.airController.systemPersitence.SystemAction;
import org.airController.systemPersitence.SystemActions;
import org.airController.systemPersitence.SystemPart;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentilationSystemTimeKeeperTest {

    @Mock
    private SystemActions systemActions;

    @Test
    void shouldReturnZero_whenNoEventInTheLastHour() {
        when(systemActions.getActionsFromTimeToNow(any(), eq(SystemPart.AIR_FLOW))).thenReturn(emptyList());
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result).isEqualTo(Duration.ZERO);
    }

    @Test
    void shouldReturnDurationToOff_whenNoMore() {
        final LocalDateTime endTime = LocalDateTime.now();
        final LocalDateTime startTime = endTime.minusHours(1);
        final SystemAction offAction = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.OFF);
        when(systemActions.getActionsFromTimeToNow(any(), eq(SystemPart.AIR_FLOW))).thenReturn(List.of(offAction));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(20, Offset.offset(1L));
    }

    @Test
    void shouldReturnDurationFromOn_whenNoMore() {
        final LocalDateTime endTime = LocalDateTime.now();
        final LocalDateTime startTime = endTime.minusHours(1);
        final SystemAction onAction = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.ON);
        when(systemActions.getActionsFromTimeToNow(any(), eq(SystemPart.AIR_FLOW))).thenReturn(List.of(onAction));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(40, Offset.offset(1L));
    }

    @Test
    void shouldCountSeveralOnOffActions() {
        final LocalDateTime endTime = LocalDateTime.now();
        final LocalDateTime startTime = endTime.minusHours(1);
        final SystemAction onAction1 = new SystemAction(startTime.plusMinutes(10), SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction1 = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.OFF);
        final SystemAction onAction2 = new SystemAction(startTime.plusMinutes(40), SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction2 = new SystemAction(startTime.plusMinutes(50), SystemPart.AIR_FLOW, OutputState.OFF);
        when(systemActions.getActionsFromTimeToNow(any(), eq(SystemPart.AIR_FLOW))).thenReturn(List.of(onAction1, offAction1, onAction2, offAction2));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);

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
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime)
                    .thenReturn(offTime);
            testee.setAirFlowOn(OutputState.ON);
            testee.setAirFlowOn(OutputState.OFF);
        }
        final LocalDate yesterday = now.minusDays(1).toLocalDate();

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toMinutes()).isCloseTo(expectedDuration, Offset.offset(1L));
    }

    @Test
    void shouldReturnReportFromYesterdayWithOnGoingTime() {
        final LocalDateTime onTime = LocalDateTime.of(2024, 9, 17, 22, 0);
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime);
            testee.setAirFlowOn(OutputState.ON);
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
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime2DaysAgo)
                    .thenReturn(offTime2DaysAgo)
                    .thenReturn(onTimeYesterday)
                    .thenReturn(offTimeYesterday)
                    .thenReturn(onTimeToday)
                    .thenReturn(now)
                    .thenReturn(now);
            testee.setAirFlowOn(OutputState.ON);
            testee.setAirFlowOn(OutputState.OFF);
            testee.setAirFlowOn(OutputState.ON);
            testee.setAirFlowOn(OutputState.OFF);
            testee.setAirFlowOn(OutputState.ON);
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