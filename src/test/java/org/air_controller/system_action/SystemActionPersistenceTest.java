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
class SystemActionPersistenceTest {

    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;
    @Mock
    private SystemActionDbAccessor humidityExchangerDbAccessor;

    @Test
    void shouldReturnActionsFromLastHour() throws SQLException {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemActionPersistence testee = new SystemActionPersistence(airFlowDbAccessor, humidityExchangerDbAccessor);

        testee.getAirFlowActionsFromTimeToNow(startTime);

        verify(airFlowDbAccessor).getActionsFromTimeToNow(startTime);
    }

    @Test
    void shouldSetTheState() throws SQLException {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemAction mostCurrentAction = new SystemAction(startTime, SystemPart.AIR_FLOW, OutputState.OFF);
        when(airFlowDbAccessor.getMostCurrentState()).thenReturn(Optional.of(mostCurrentAction));
        final SystemActionPersistence testee = new SystemActionPersistence(airFlowDbAccessor, humidityExchangerDbAccessor);

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
        final SystemActionPersistence testee = new SystemActionPersistence(airFlowDbAccessor, humidityExchangerDbAccessor);

        testee.setAirFlowOn(OutputState.ON);

        verify(airFlowDbAccessor).getMostCurrentState();
        verifyNoMoreInteractions(airFlowDbAccessor);
    }
}