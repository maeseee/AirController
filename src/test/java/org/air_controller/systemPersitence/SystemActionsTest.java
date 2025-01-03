package org.air_controller.systemPersitence;

import org.air_controller.system.OutputState;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SystemActionsTest {
    @Test
    void shouldReturnActionsFromLastHour() {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemActions testee = new SystemActions();

        testee.setAirFlowOn(OutputState.OFF); // Ensure a state change
        testee.setAirFlowOn(OutputState.ON);

        final List<SystemAction> actionsFromLastHour = testee.getActionsFromTimeToNow(startTime, SystemPart.AIR_FLOW);
        assertThat(actionsFromLastHour).size().isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldReturnActionsFromLastHourInAscOrder() throws InterruptedException {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemActions testee = new SystemActions();

        testee.setAirFlowOn(OutputState.OFF); // Ensure a state change
        testee.setAirFlowOn(OutputState.ON);
        Thread.sleep(1); // Force a difference
        testee.setAirFlowOn(OutputState.OFF);

        final List<SystemAction> actionsFromLastHour = testee.getActionsFromTimeToNow(startTime, SystemPart.AIR_FLOW);
        assertThat(actionsFromLastHour).size().isGreaterThanOrEqualTo(1);
        final SystemAction lastAction = actionsFromLastHour.get(actionsFromLastHour.size() - 1);
        assertThat(lastAction.systemPart()).isEqualTo(SystemPart.AIR_FLOW);
        assertThat(lastAction.outputState()).isEqualTo(OutputState.OFF);
        final SystemAction secondLastAction = actionsFromLastHour.get(actionsFromLastHour.size() - 2);
        assertThat(secondLastAction.systemPart()).isEqualTo(SystemPart.AIR_FLOW);
        assertThat(secondLastAction.outputState()).isEqualTo(OutputState.ON);
        assertThat(lastAction.actionTime()).isAfter(secondLastAction.actionTime());
    }

    @Test
    void shouldIgnoreSettingTheSameStateTwice() {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemActions testee = new SystemActions();
        final int numberOfActionsFromLastHour = testee.getActionsFromTimeToNow(startTime, SystemPart.AIR_FLOW).size();

        testee.setAirFlowOn(OutputState.OFF); // Ensure a state change
        testee.setAirFlowOn(OutputState.ON);
        testee.setAirFlowOn(OutputState.ON);

        final List<SystemAction> actionsFromLastHour = testee.getActionsFromTimeToNow(startTime, SystemPart.AIR_FLOW);
        assertThat(actionsFromLastHour).size().isGreaterThanOrEqualTo(numberOfActionsFromLastHour + 1);
    }
}