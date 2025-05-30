package org.air_controller.system;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.GpioPins;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ControlledVentilationSystemTest {

    @Test
    void shouldSetAirFlowOn() {
        final GpioPin airFlow = mock(GpioPin.class);
        final GpioPin humidityExchanger = mock(GpioPin.class);
        final GpioPins gpioPins = new GpioPins(airFlow, humidityExchanger);
        final ControlledVentilationSystem testee = new ControlledVentilationSystem(gpioPins);

        testee.setAirFlowOn(OutputState.ON);

        verify(airFlow).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }

    @Test
    void shouldSetHumidityExchangerOn() {
        final GpioPin airFlow = mock(GpioPin.class);
        final GpioPin humidityExchanger = mock(GpioPin.class);
        final GpioPins gpioPins = new GpioPins(airFlow, humidityExchanger);
        final ControlledVentilationSystem testee = new ControlledVentilationSystem(gpioPins);

        testee.setHumidityExchangerOn(OutputState.ON);

        verify(humidityExchanger).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }
}