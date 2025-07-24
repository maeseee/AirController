package org.air_controller.system;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.GpioPins;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ControlledVentilationSystemTest {

    private GpioPins gpioPins;

    @Mock
    private GpioPin airFlow;
    @Mock
    private GpioPin humidityExchanger;

    @BeforeEach
    void setup() {
        gpioPins = new GpioPins(airFlow, humidityExchanger);
    }

    @Test
    void shouldSetAirFlowOn() {
        final ControlledVentilationSystem testee = new ControlledVentilationSystem(gpioPins);

        testee.setAirFlowOn(OutputState.ON);

        verify(airFlow).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }

    @Test
    void shouldSetHumidityExchangerOn() {
        final ControlledVentilationSystem testee = new ControlledVentilationSystem(gpioPins);

        testee.setHumidityExchangerOn(OutputState.ON);

        verify(humidityExchanger).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }
}