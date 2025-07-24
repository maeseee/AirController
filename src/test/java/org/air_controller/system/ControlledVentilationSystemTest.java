package org.air_controller.system;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.GpioPins;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControlledVentilationSystemTest {

    private ControlledVentilationSystem testee;

    @Mock
    private GpioPin airFlow;
    @Mock
    private GpioPin humidityExchanger;

    @BeforeEach
    void setup() {
        final GpioPins gpioPins = new GpioPins(airFlow, humidityExchanger);
        testee = new ControlledVentilationSystem(gpioPins);
    }

    @Test
    void shouldSetAirFlowOn() {
        testee.setAirFlowOn(OutputState.ON);

        verify(airFlow).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }

    @Test
    void shouldSetHumidityExchangerOn() {
        testee.setHumidityExchangerOn(OutputState.ON);

        verify(humidityExchanger).setGpioState(true);
        verifyNoMoreInteractions(airFlow);
    }

    @Test
    void shouldReturnAirFlowOn_whenOn() {
        when(airFlow.getGpioState()).thenReturn(true);

        final OutputState airFlowOn = testee.isAirFlowOn();

        assertThat(airFlowOn).isEqualTo(OutputState.ON);
    }

    @Test
    void shouldReturnAirFlowOff_whenOff() {
        when(airFlow.getGpioState()).thenReturn(false);

        final OutputState airFlowOn = testee.isAirFlowOn();

        assertThat(airFlowOn).isEqualTo(OutputState.OFF);
    }
}