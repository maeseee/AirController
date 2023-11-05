package org.airController.gpio;

import com.pi4j.io.gpio.*;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class GpioPinImpl implements GpioPin {
    private static final Logger logger = Logger.getLogger(GpioPinImpl.class.getName());

    private final GpioPinDigitalOutput outputPin;

    public GpioPinImpl(GpioFunction pinFunction) {
        final Pin pin = mapToPin(pinFunction);
        final String name = pinFunction.name();
        final GpioController gpioController = GpioFactory.getInstance();
        outputPin = gpioController.provisionDigitalOutputPin(pin, name, PinState.LOW);
    }

    GpioPinImpl(GpioPinDigitalOutput outputPin) {
        this.outputPin = outputPin;
    }

    @Override
    public boolean getGpioState() {
        final PinState pinState = outputPin.getState();
        return mapToStateOn(pinState);
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            logger.info(outputPin.getName() + " set to " + (stateOn ? "on" : "off"));
            final PinState pinState = mapToPinState(stateOn);
            outputPin.setState(pinState);
        }
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

    public static void main(String[] args) {
        final GpioPinImpl gpioPin = new GpioPinImpl(GpioFunction.MAIN_SYSTEM);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> gpioPin.setGpioState(!gpioPin.getGpioState()), 0, 2, TimeUnit.SECONDS);
    }
}
