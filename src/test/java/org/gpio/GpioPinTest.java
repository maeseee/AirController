package org.gpio;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GpioPinTest {

    @Mock
    private GpioPinDigitalOutput outputPin;

    @Test
    void testGetter() {
        when(outputPin.getState()).thenReturn(PinState.HIGH);
        final GpioPin testee = new GpioPin(outputPin);

        final boolean gpioState = testee.getGpioState();

        verify(outputPin).getState();
        assertTrue(gpioState);
    }

    @Test
    void testSetter() {
        final GpioPin testee = new GpioPin(outputPin);

        testee.setGpioState(true);

        verify(outputPin).setState(PinState.HIGH);
    }
}