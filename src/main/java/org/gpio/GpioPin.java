package org.gpio;

import com.pi4j.io.gpio.*;


public class GpioPin {

    private final GpioPinDigitalOutput outputPin;

    public GpioPin(GpioFunction pinFunction) {
        final Pin pin = mapToPin(pinFunction);
        final String name = pinFunction.name();
        final GpioController gpioController = GpioFactory.getInstance();
        outputPin = gpioController.provisionDigitalOutputPin(pin, name, PinState.LOW);
    }

    public boolean getGpioState() {
        final PinState pinState = outputPin.getState();
        return mapToStateOn(pinState);
    }

    public void setGpioState(boolean stateOn) {
        final PinState pinState = mapToPinState(stateOn);
        outputPin.setState(pinState);
    }

    private Pin mapToPin(GpioFunction function) {
        return switch (function) {
            case DHT22_SENSOR -> RaspiPin.GPIO_04;
            case MAIN_SYSTEM -> RaspiPin.GPIO_05;
            case HUMIDITY_EXCHANGER -> RaspiPin.GPIO_06;
            case UNMAPPED -> RaspiPin.GPIO_13;
            case NIGHT_TIME -> RaspiPin.GPIO_19;
        };
    }

    private PinState mapToPinState(boolean stateOn) {
        if (stateOn) {
            return PinState.HIGH;
        }
        return PinState.LOW;
    }

    private boolean mapToStateOn(PinState state) {
        return switch (state) {
            case LOW -> false;
            case HIGH -> true;
        };
    }
}
