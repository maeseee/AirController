package org.airController.system;

import org.airController.gpioAdapter.GpioPin;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ControlledVentilationSystemImplTest {

    @Test
    void shouldSetAirFlowOn() {
        final GpioPin airFlow = mock(GpioPin.class);
        final GpioPin humidityExchanger = mock(GpioPin.class);
        final ControlledVentilationSystemImpl testee = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);

        testee.setAirFlowOn(true);

        verify(airFlow).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }

    @Test
    void shouldSetHumidityExchangerOn() {
        final GpioPin airFlow = mock(GpioPin.class);
        final GpioPin humidityExchanger = mock(GpioPin.class);
        final ControlledVentilationSystemImpl testee = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);

        testee.setHumidityExchangerOn(true);

        verify(humidityExchanger).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }
}