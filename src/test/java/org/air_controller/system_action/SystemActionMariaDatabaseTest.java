package org.air_controller.system_action;

import org.air_controller.system.OutputState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

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
        final VentilationSystemPersistenceData data = new VentilationSystemPersistenceData(OutputState.ON, 0.0, Collections.emptyMap());
        final VentilationSystemPersistence testee = new VentilationSystemPersistence(dbAccessors);

        testee.persistAirFlowData(data);

        verify(airFlowDbAccessor).insertAction(eq(data), any());
        verifyNoMoreInteractions(airFlowDbAccessor);
    }
}