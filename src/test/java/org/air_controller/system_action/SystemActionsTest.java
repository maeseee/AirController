package org.air_controller.system_action;

import org.air_controller.system.OutputState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemActionsTest {

    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;
    private SystemActionDbAccessor humidityExchangerDbAccessor;

    @Test
    void shouldReturnActionsFromLastHour() throws SQLException {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemActions testee = new SystemActions(airFlowDbAccessor, humidityExchangerDbAccessor);

        testee.getAirFlowActionsFromTimeToNow(startTime, SystemPart.AIR_FLOW);

        verify(airFlowDbAccessor).getActionsFromTimeToNow(startTime, SystemPart.AIR_FLOW);
    }

//    @Test
//    void shouldReturnActionsFromLastHourInAscOrder() throws InterruptedException, SQLException {
//        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
//        final SystemActions testee = new SystemActions(airFlowDbAccessor, humidityExchangerDbAccessor);
//
//        testee.setAirFlowOn(OutputState.OFF); // Ensure a state change
//        testee.setAirFlowOn(OutputState.ON);
//        Thread.sleep(100); // Force a difference
//        testee.setAirFlowOn(OutputState.OFF);
//
//        final List<SystemAction> actionsFromLastHour = testee.getAirFlowActionsFromTimeToNow(startTime, SystemPart.AIR_FLOW);
//        assertThat(actionsFromLastHour).size().isPositive();
//        final SystemAction lastAction = actionsFromLastHour.getLast();
//        assertThat(lastAction.systemPart()).isEqualTo(SystemPart.AIR_FLOW);
//        assertThat(lastAction.outputState()).isEqualTo(OutputState.OFF);
//        final SystemAction secondLastAction = actionsFromLastHour.get(actionsFromLastHour.size() - 2);
//        assertThat(secondLastAction.systemPart()).isEqualTo(SystemPart.AIR_FLOW);
//        assertThat(secondLastAction.outputState()).isEqualTo(OutputState.ON);
//        assertThat(lastAction.actionTime()).isAfter(secondLastAction.actionTime());
//    }

    @Test
    void shouldSetTheState() throws SQLException {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemAction mostCurrentAction = new SystemAction(startTime, SystemPart.AIR_FLOW, OutputState.OFF);
        when(airFlowDbAccessor.getMostCurrentState()).thenReturn(Optional.of(mostCurrentAction));
        final SystemActions testee = new SystemActions(airFlowDbAccessor, humidityExchangerDbAccessor);

        testee.setAirFlowOn(OutputState.ON);

        verify(airFlowDbAccessor).getMostCurrentState();
        verify(airFlowDbAccessor).insertAction(eq(OutputState.ON), any());
        verifyNoMoreInteractions(airFlowDbAccessor);
    }

    @Test
    void shouldIgnoreSettingTheSameStateTwice() throws SQLException {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemAction mostCurrentAction = new SystemAction(startTime, SystemPart.AIR_FLOW, OutputState.ON);
        when(airFlowDbAccessor.getMostCurrentState()).thenReturn(Optional.of(mostCurrentAction));
        final SystemActions testee = new SystemActions(airFlowDbAccessor, humidityExchangerDbAccessor);

        testee.setAirFlowOn(OutputState.ON);

        verify(airFlowDbAccessor).getMostCurrentState();
        verifyNoMoreInteractions(airFlowDbAccessor);
    }
}