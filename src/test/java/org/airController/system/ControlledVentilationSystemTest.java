package org.airController.system;

import org.airController.gpio.GpioPin;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ControlledVentilationSystemTest {

    @Test
    void shouldSetAirFlowOn() {
        final GpioPin airFlow = mock(GpioPin.class);
        final GpioPin humidityExchanger = mock(GpioPin.class);
        final ControlledVentilationSystem testee = new ControlledVentilationSystem(airFlow, humidityExchanger);

        testee.setAirFlowOn(true);

        verify(airFlow).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }

    @Test
    void shouldSetHumidityExchangerOn() {
        final GpioPin airFlow = mock(GpioPin.class);
        final GpioPin humidityExchanger = mock(GpioPin.class);
        final ControlledVentilationSystem testee = new ControlledVentilationSystem(airFlow, humidityExchanger);

        testee.setHumidityExchangerOn(true);

        verify(humidityExchanger).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }
}