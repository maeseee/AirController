package org.air_controller.statistics;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyOnTimeLoggerTest {

    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;

    @Test
    void shouldReturnZero_whenNoEventYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(emptyList());
        final DailyOnTimeLogger testee = new DailyOnTimeLogger(airFlowDbAccessor);

        final Duration result = testee.getTotalFromDay(yesterday);

        assertThat(result).isEqualTo(Duration.ZERO);
    }

    @Test
    void shouldReturnDurationFromOn_whenOnlyThisYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final ZonedDateTime startTime = ZonedDateTime.of(yesterday.atStartOfDay(), ZoneOffset.UTC).plusHours(10);
        final SystemAction offAction = new SystemAction(startTime.plusMinutes(20), OutputState.ON);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(List.of(offAction));
        final DailyOnTimeLogger testee = new DailyOnTimeLogger(airFlowDbAccessor);

        final Duration result = testee.getTotalFromDay(yesterday);

        assertThat(result.toHours()).isCloseTo(14, within(1L));
    }

    @Test
    void shouldReturnDuration_whenMultipleActionsYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final ZonedDateTime startTime = ZonedDateTime.of(yesterday.atStartOfDay(), ZoneOffset.UTC).plusHours(10);
        final SystemAction onAction1 = new SystemAction(startTime.plusMinutes(10), OutputState.ON);
        final SystemAction offAction1 = new SystemAction(startTime.plusMinutes(20), OutputState.OFF);
        final SystemAction onAction2 = new SystemAction(startTime.plusMinutes(40), OutputState.ON);
        final SystemAction offAction2 = new SystemAction(startTime.plusMinutes(50), OutputState.OFF);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(
                List.of(onAction1, offAction1, onAction2, offAction2));
        final DailyOnTimeLogger testee = new DailyOnTimeLogger(airFlowDbAccessor);

        final Duration result = testee.getTotalFromDay(yesterday);

        assertThat(result.toMinutes()).isCloseTo(20, within(1L));
    }

    @Test
    void shouldReturnDuration_whenMultipleRepeatingOffEventsYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final ZonedDateTime startTime = ZonedDateTime.of(yesterday.atStartOfDay(), ZoneOffset.UTC).plusHours(10);
        final SystemAction onAction1 = new SystemAction(startTime, OutputState.ON);
        final SystemAction offAction1 = new SystemAction(startTime.plusMinutes(10), OutputState.OFF);
        final SystemAction offAction2 = new SystemAction(startTime.plusMinutes(30), OutputState.OFF);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(List.of(onAction1, offAction1, offAction2));
        final DailyOnTimeLogger testee = new DailyOnTimeLogger(airFlowDbAccessor);

        final Duration result = testee.getTotalFromDay(yesterday);

        assertThat(result.toMinutes()).isCloseTo(10, within(1L));
    }

    @Test
    void shouldReturnDuration_whenMultipleRepeatingOnEventsYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final ZonedDateTime startTime = ZonedDateTime.of(yesterday.atStartOfDay(), ZoneOffset.UTC).plusHours(10);
        final SystemAction onAction1 = new SystemAction(startTime, OutputState.ON);
        final SystemAction onAction2 = new SystemAction(startTime.plusMinutes(10), OutputState.ON);
        final SystemAction offAction2 = new SystemAction(startTime.plusMinutes(30), OutputState.OFF);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(List.of(onAction1, onAction2, offAction2));
        final DailyOnTimeLogger testee = new DailyOnTimeLogger(airFlowDbAccessor);

        final Duration result = testee.getTotalFromDay(yesterday);

        assertThat(result.toMinutes()).isCloseTo(30, within(1L));
    }
}