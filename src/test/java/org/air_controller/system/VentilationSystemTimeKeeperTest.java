package org.air_controller.system;

import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemPart;
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
class VentilationSystemTimeKeeperTest {

    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;

    @Test
    void shouldReturnZero_whenNoEventInTheLastHour() {
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(emptyList());
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result).isEqualTo(Duration.ZERO);
    }

    @Test
    void shouldReturnAnHour_whenNoEventInTheLastHourButLastEventWasOn() {
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(emptyList());
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        testee.setAirFlowOn(OutputState.ON);
        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void shouldReturnDurationToOff_whenNoMore() {
        final ZonedDateTime offTime = ZonedDateTime.now(ZoneOffset.UTC).minusHours(1).plusMinutes(20);
        final SystemAction offAction = new SystemAction(offTime, SystemPart.AIR_FLOW, OutputState.OFF);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(List.of(offAction));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(20, within(1L));
    }

    @Test
    void shouldReturnDurationFromOn_whenNoMore() {
        final ZonedDateTime onTime = ZonedDateTime.now(ZoneOffset.UTC).minusHours(1).plusMinutes(20);
        final SystemAction onAction = new SystemAction(onTime, SystemPart.AIR_FLOW, OutputState.ON);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(List.of(onAction));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(40, within(1L));
    }

    @Test
    void shouldCountSeveralOnOffActions() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(1);
        final SystemAction onAction1 = new SystemAction(startTime.plusMinutes(10), SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction1 = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.OFF);
        final SystemAction onAction2 = new SystemAction(startTime.plusMinutes(40), SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction2 = new SystemAction(startTime.plusMinutes(50), SystemPart.AIR_FLOW, OutputState.OFF);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(
                List.of(onAction1, offAction1, onAction2, offAction2));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(20, within(1L));
    }

    @Test
    void shouldReturnZero_whenNoEventYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(emptyList());
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result).isEqualTo(Duration.ZERO);
    }

    @Test
    void shouldReturnDurationToOff_whenOnlyThisYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final ZonedDateTime startTime = ZonedDateTime.of(yesterday.atStartOfDay(), ZoneOffset.UTC).plusHours(10);
        final SystemAction offAction = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.OFF);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(List.of(offAction));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toHours()).isCloseTo(10, within(1L));
    }

    @Test
    void shouldReturnDurationFromOn_whenOnlyThisYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final ZonedDateTime startTime = ZonedDateTime.of(yesterday.atStartOfDay(), ZoneOffset.UTC).plusHours(10);
        final SystemAction offAction = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.ON);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(List.of(offAction));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toHours()).isCloseTo(14, within(1L));
    }

    @Test
    void shouldReturnDuration_whenMultipleActionsYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final ZonedDateTime startTime = ZonedDateTime.of(yesterday.atStartOfDay(), ZoneOffset.UTC).plusHours(10);
        final SystemAction onAction1 = new SystemAction(startTime.plusMinutes(10), SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction1 = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.OFF);
        final SystemAction onAction2 = new SystemAction(startTime.plusMinutes(40), SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction2 = new SystemAction(startTime.plusMinutes(50), SystemPart.AIR_FLOW, OutputState.OFF);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(
                List.of(onAction1, offAction1, onAction2, offAction2));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toMinutes()).isCloseTo(20, within(1L));
    }

    @Test
    void shouldReturnDuration_whenMultipleRepeatingOffEventsYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final ZonedDateTime startTime = ZonedDateTime.of(yesterday.atStartOfDay(), ZoneOffset.UTC).plusHours(10);
        final SystemAction onAction1 = new SystemAction(startTime, SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction1 = new SystemAction(startTime.plusMinutes(10), SystemPart.AIR_FLOW, OutputState.OFF);
        final SystemAction offAction2 = new SystemAction(startTime.plusMinutes(30), SystemPart.AIR_FLOW, OutputState.OFF);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(List.of(onAction1, offAction1, offAction2));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toMinutes()).isCloseTo(10, within(1L));
    }

    @Test
    void shouldReturnDuration_whenMultipleRepeatingOnEventsYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final ZonedDateTime startTime = ZonedDateTime.of(yesterday.atStartOfDay(), ZoneOffset.UTC).plusHours(10);
        final SystemAction onAction1 = new SystemAction(startTime, SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction onAction2 = new SystemAction(startTime.plusMinutes(10), SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction2 = new SystemAction(startTime.plusMinutes(30), SystemPart.AIR_FLOW, OutputState.OFF);
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(List.of(onAction1, onAction2, offAction2));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(airFlowDbAccessor);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toMinutes()).isCloseTo(30, within(1L));
    }
}