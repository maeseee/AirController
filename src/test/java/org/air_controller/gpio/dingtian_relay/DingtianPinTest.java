package org.air_controller.gpio.dingtian_relay;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DingtianPinTest {
    @Mock
    private RelayCommunication communication;

    @Test
    void shouldGetGpioStateOfAirFlow() {
        final List<Boolean> gpioStates = Arrays.asList(true, false, false, false);
        when(communication.readStates()).thenReturn(gpioStates);
        final DingtianPin testee = new DingtianPin(DingtianRelay.AIR_FLOW, true, communication);

        final boolean isOn = testee.getGpioState();

        assertThat(isOn).isTrue();
        verify(communication, times(2)).readStates(); // also on initialization
    }

    @Test
    void shouldGetGpioStateOfHumidityExchanger() {
        final List<Boolean> gpioStates = Arrays.asList(false, true, false, false);
        when(communication.readStates()).thenReturn(gpioStates);
        final DingtianPin testee = new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, true, communication);

        final boolean isOn = testee.getGpioState();

        assertThat(isOn).isTrue();
        verify(communication, times(2)).readStates(); // also on initialization
    }

    @Test
    void shouldSetGpioStateOfHumidityExchanger() {
        final List<Boolean> gpioStates = Arrays.asList(false, false, false, false);
        when(communication.readStates()).thenReturn(gpioStates);
        final DingtianRelay relay = DingtianRelay.HUMIDITY_EXCHANGER;
        final DingtianPin testee = new DingtianPin(relay, false, communication);

        testee.setGpioState(true);

        verify(communication).setRelayState(relay.getRelayIndex(), true);
    }

    @Test
    void shouldNotSetGpioState_whenAlreadySet() {
        final List<Boolean> gpioStates = Arrays.asList(false, true, false, false);
        when(communication.readStates()).thenReturn(gpioStates);
        final DingtianRelay relay = DingtianRelay.HUMIDITY_EXCHANGER;
        final DingtianPin testee = new DingtianPin(relay, true, communication);

        testee.setGpioState(true);

        verify(communication, times(0)).setRelayState(relay.getRelayIndex(), true);
    }

    @Test
    void shouldInitiallySetTheStateOfHumidityExchanger() {
        final List<Boolean> gpioStates = Arrays.asList(false, false, false, false);
        when(communication.readStates()).thenReturn(gpioStates);
        final DingtianRelay relay = DingtianRelay.HUMIDITY_EXCHANGER;

        new DingtianPin(relay, true, communication);

        verify(communication).setRelayState(relay.getRelayIndex(), true);
    }
}