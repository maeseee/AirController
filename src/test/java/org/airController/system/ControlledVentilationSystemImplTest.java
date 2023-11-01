package org.airController.system;

import org.airController.gpio.GpioPin;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ControlledVentilationSystemImplTest {

    @Test
    void testWhenInitalizedThenAirFlowIsOnAndHumididyExchangerOff() {
        final GpioPin airFlow = mock(GpioPin.class);
        final GpioPin humidityExchanger = mock(GpioPin.class);
        final ControlledVentilationSystemImpl testee = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);

        testee.isAirFlowOn();

        verify(airFlow).setGpioState(true);
        verify(humidityExchanger).setGpioState(false);
    }
}