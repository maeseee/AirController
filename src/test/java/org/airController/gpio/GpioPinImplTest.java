package org.airController.gpio;

import org.airController.gpioAdapter.GpioFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GpioPinImplTest {

    @Mock
    private RaspberryPiPin raspberryPiPin;

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void testInitialGpioState(boolean initialHigh) {
        final GpioPinImpl testee = new GpioPinImpl(GpioFunction.AIR_FLOW.name(), raspberryPiPin, initialHigh);

        testee.getGpioState();

        verify(raspberryPiPin).write(initialHigh);
    }

    @Test
    void testGpioStateOffWhenSetOff() {
        final boolean initialState = true;
        when(raspberryPiPin.read()).thenReturn(initialState);
        final GpioPinImpl testee = new GpioPinImpl(GpioFunction.AIR_FLOW.name(), raspberryPiPin, initialState);

        testee.setGpioState(false);

        ArgumentCaptor<Boolean> argument = ArgumentCaptor.forClass(Boolean.class);
        verify(raspberryPiPin, times(2)).write(argument.capture());
        final List<Boolean> values = argument.getAllValues();
        assertEquals(2, values.size());
        assertEquals(true, values.get(0));
        assertEquals(false, values.get(1));
    }

    @Test
    void testKeepStateOnWhenSetOn() {
        final boolean initialState = true;
        when(raspberryPiPin.read()).thenReturn(initialState);
        final GpioPinImpl testee = new GpioPinImpl(GpioFunction.AIR_FLOW.name(), raspberryPiPin, initialState);

        testee.setGpioState(true);

        verify(raspberryPiPin, times(1)).write(true);
    }

}