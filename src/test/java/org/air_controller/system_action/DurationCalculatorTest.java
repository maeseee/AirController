package org.air_controller.system_action;

import org.air_controller.system.OutputState;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DurationCalculatorTest {

    @Test
    void shouldReturnZero_whenNoActionsInTheTimeRange() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(1);
        final List<SystemAction> systemActions = new ArrayList<>();
        final DurationCalculator testee = new DurationCalculator(systemActions);

        final Duration result = testee.getDuration(startTime, endTime);

        assertThat(result).isEqualTo(Duration.ZERO);
    }

    @Test
    void shouldReturnTimeRange_whenAllActionsAreInRange() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(1);
        final List<SystemAction> systemActions = new ArrayList<>();
        systemActions.add(new SystemAction(startTime.minusMinutes(15), SystemPart.AIR_FLOW, OutputState.OFF));
        systemActions.add(new SystemAction(endTime.minusMinutes(45), SystemPart.AIR_FLOW, OutputState.ON));
        systemActions.add(new SystemAction(endTime.minusMinutes(15), SystemPart.AIR_FLOW, OutputState.OFF));
        final DurationCalculator testee = new DurationCalculator(systemActions);

        final Duration result = testee.getDuration(startTime, endTime);

        assertThat(result).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    void shouldAlsoTakeActionBeforeStart_whenOn() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(1);
        final List<SystemAction> systemActions = new ArrayList<>();
        systemActions.add(new SystemAction(startTime.minusMinutes(15), SystemPart.AIR_FLOW, OutputState.ON));
        systemActions.add(new SystemAction(startTime.plusMinutes(15), SystemPart.AIR_FLOW, OutputState.OFF));
        final DurationCalculator testee = new DurationCalculator(systemActions);

        final Duration result = testee.getDuration(startTime, endTime);

        assertThat(result).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void shouldAlsoTakeActionUntilEnd_whenOn() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(1);
        final List<SystemAction> systemActions = new ArrayList<>();
        systemActions.add(new SystemAction(startTime.minusMinutes(15), SystemPart.AIR_FLOW, OutputState.OFF));
        systemActions.add(new SystemAction(endTime.minusMinutes(15), SystemPart.AIR_FLOW, OutputState.ON));
        final DurationCalculator testee = new DurationCalculator(systemActions);

        final Duration result = testee.getDuration(startTime, endTime);

        assertThat(result).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void shouldTakeSeveralActionsIntoAccount() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(1);
        final List<SystemAction> systemActions = new ArrayList<>();
        systemActions.add(new SystemAction(startTime.minusMinutes(15), SystemPart.AIR_FLOW, OutputState.OFF));
        systemActions.add(new SystemAction(endTime.minusMinutes(55), SystemPart.AIR_FLOW, OutputState.ON));
        systemActions.add(new SystemAction(endTime.minusMinutes(50), SystemPart.AIR_FLOW, OutputState.OFF));
        systemActions.add(new SystemAction(endTime.minusMinutes(45), SystemPart.AIR_FLOW, OutputState.ON));
        systemActions.add(new SystemAction(endTime.minusMinutes(40), SystemPart.AIR_FLOW, OutputState.OFF));
        final DurationCalculator testee = new DurationCalculator(systemActions);

        final Duration result = testee.getDuration(startTime, endTime);

        assertThat(result).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    void shouldReturnRangeInBetween_whenNoActionBefore() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(1);
        final List<SystemAction> systemActions = new ArrayList<>();
        systemActions.add(new SystemAction(endTime.minusMinutes(45), SystemPart.AIR_FLOW, OutputState.ON));
        systemActions.add(new SystemAction(endTime.minusMinutes(40), SystemPart.AIR_FLOW, OutputState.OFF));
        final DurationCalculator testee = new DurationCalculator(systemActions);

        final Duration result = testee.getDuration(startTime, endTime);

        assertThat(result).isEqualTo(Duration.ofMinutes(5));
    }

    @Test
    void shouldReturnOnTime_whenOnlyOneAction() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(1);
        final List<SystemAction> systemActions = new ArrayList<>();
        systemActions.add(new SystemAction(endTime.minusMinutes(20), SystemPart.AIR_FLOW, OutputState.ON));
        final DurationCalculator testee = new DurationCalculator(systemActions);

        final Duration result = testee.getDuration(startTime, endTime);

        assertThat(result).isEqualTo(Duration.ofMinutes(20));
    }
}