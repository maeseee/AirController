package org.airController.system;

import org.airController.systemPersitence.SystemAction;
import org.airController.systemPersitence.SystemActions;
import org.airController.systemPersitence.SystemPart;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void shouldReturnAnHour_whenNoEventInTheLastHourButLastEventWasOn() {
        when(systemActions.getActionsFromTimeToNow(any(), eq(SystemPart.AIR_FLOW))).thenReturn(emptyList());
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);

        testee.setAirFlowOn(OutputState.ON);
        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result).isEqualTo(Duration.ofHours(1));
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

    @Test
    void shouldReturnZero_whenNoEventYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        when(systemActions.getActionsFromTimeToNow(any(), eq(SystemPart.AIR_FLOW))).thenReturn(emptyList());
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result).isEqualTo(Duration.ZERO);
    }

    @Test
    void shouldReturnDurationToOff_whenOnlyThisYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final LocalDateTime startTime = yesterday.atStartOfDay().plusHours(10);
        final SystemAction offAction = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.OFF);
        when(systemActions.getActionsFromTimeToNow(any(), eq(SystemPart.AIR_FLOW))).thenReturn(List.of(offAction));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toHours()).isCloseTo(10, Offset.offset(1L));
    }

    @Test
    void shouldReturnDurationFromOn_whenOnlyThisYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final LocalDateTime startTime = yesterday.atStartOfDay().plusHours(10);
        final SystemAction offAction = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.ON);
        when(systemActions.getActionsFromTimeToNow(any(), eq(SystemPart.AIR_FLOW))).thenReturn(List.of(offAction));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toHours()).isCloseTo(14, Offset.offset(1L));
    }

    @Test
    void shouldReturnDuration_whenMultipleActionsYesterday() {
        final LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
        final LocalDateTime startTime = yesterday.atStartOfDay().plusHours(10);
        final SystemAction onAction1 = new SystemAction(startTime.plusMinutes(10), SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction1 = new SystemAction(startTime.plusMinutes(20), SystemPart.AIR_FLOW, OutputState.OFF);
        final SystemAction onAction2 = new SystemAction(startTime.plusMinutes(40), SystemPart.AIR_FLOW, OutputState.ON);
        final SystemAction offAction2 = new SystemAction(startTime.plusMinutes(50), SystemPart.AIR_FLOW, OutputState.OFF);
        when(systemActions.getActionsFromTimeToNow(any(), eq(SystemPart.AIR_FLOW))).thenReturn(List.of(onAction1, offAction1, onAction2, offAction2));
        final VentilationSystemTimeKeeper testee = new VentilationSystemTimeKeeper(systemActions);

        final Duration result = testee.getTotalAirFlowFromDay(yesterday);

        assertThat(result.toMinutes()).isCloseTo(20, Offset.offset(1L));
    }
}