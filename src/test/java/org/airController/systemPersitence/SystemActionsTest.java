package org.airController.systemPersitence;

import org.airController.system.OutputState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SystemActionsTest {
    @Test
    void shouldReturnActionsFromLastHour() {
        final SystemActions testee = new SystemActions();

        testee.setAirFlowOn(OutputState.ON);

        final List<SystemAction> actionsFromLastHour = testee.getActionsFromLastHour(SystemPart.AIR_FLOW);
        assertThat(actionsFromLastHour).size().isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldReturnActionsFromLastHourInAscOrder() throws InterruptedException {
        final SystemActions testee = new SystemActions();

        testee.setAirFlowOn(OutputState.ON);
        Thread.sleep(1); // Force a difference
        testee.setAirFlowOn(OutputState.OFF);

        final List<SystemAction> actionsFromLastHour = testee.getActionsFromLastHour(SystemPart.AIR_FLOW);
        assertThat(actionsFromLastHour).size().isGreaterThanOrEqualTo(1);
        final SystemAction lastAction = actionsFromLastHour.get(actionsFromLastHour.size() - 1);
        assertThat(lastAction.systemPart()).isEqualTo(SystemPart.AIR_FLOW);
        assertThat(lastAction.outputState()).isEqualTo(OutputState.OFF);
        final SystemAction secondLastAction = actionsFromLastHour.get(actionsFromLastHour.size() - 2);
        assertThat(secondLastAction.systemPart()).isEqualTo(SystemPart.AIR_FLOW);
        assertThat(secondLastAction.outputState()).isEqualTo(OutputState.ON);
        assertThat(lastAction.actionTime()).isAfter(secondLastAction.actionTime());
    }
}