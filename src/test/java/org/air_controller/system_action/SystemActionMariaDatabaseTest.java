package org.air_controller.system_action;

import org.air_controller.system.OutputState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemActionMariaDatabaseTest {

    private VentilationSystemDbAccessors dbAccessors;

    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;
    @Mock
    private SystemActionDbAccessor humidityExchangerDbAccessor;

    @BeforeEach
    void setUpDbAccessors() {
        dbAccessors = new VentilationSystemDbAccessors(airFlowDbAccessor, humidityExchangerDbAccessor);
    }

    @Test
    void shouldSetTheState() {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemAction mostCurrentAction = new SystemAction(startTime, OutputState.OFF);
        when(airFlowDbAccessor.getMostCurrentState()).thenReturn(Optional.of(mostCurrentAction));
        final VentilationSystemPersistence testee = new VentilationSystemPersistence(dbAccessors);

        testee.setAirFlowOn(OutputState.ON);

        verify(airFlowDbAccessor).getMostCurrentState();
        verify(airFlowDbAccessor).insertAction(eq(OutputState.ON), any());
        verifyNoMoreInteractions(airFlowDbAccessor);
    }

    @Test
    void shouldIgnoreSettingTheSameStateTwice() {
        final ZonedDateTime startTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);
        final SystemAction mostCurrentAction = new SystemAction(startTime, OutputState.ON);
        when(airFlowDbAccessor.getMostCurrentState()).thenReturn(Optional.of(mostCurrentAction));
        final VentilationSystemPersistence testee = new VentilationSystemPersistence(dbAccessors);

        testee.setAirFlowOn(OutputState.ON);

        verify(airFlowDbAccessor).getMostCurrentState();
        verifyNoMoreInteractions(airFlowDbAccessor);
    }
}