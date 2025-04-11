package org.air_controller.gpio.raspberry;

import org.air_controller.gpio.GpioPin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RaspberryGpioPin implements GpioPin {
    private static final Logger logger = LogManager.getLogger(RaspberryGpioPin.class);

    private final String name;
    private final GpioHwPin gpioHwPin;

    public RaspberryGpioPin(RaspberryGpioFunction pinFunction, boolean initialHigh) throws IOException {
        this(pinFunction.name(), new GpioHwPin(pinFunction), initialHigh);
    }

    RaspberryGpioPin(String name, GpioHwPin gpioHwPin, boolean initialHigh) {
        this.name = name;
        this.gpioHwPin = gpioHwPin;

        gpioHwPin.export(true);
        logger.info("{} set initial to {}", name, initialHigh ? "on" : "off");
        gpioHwPin.write(initialHigh);
    }

    @Override
    public boolean getGpioState() {
        return gpioHwPin.read();
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            logger.info("{} set to {}", name, stateOn ? "on" : "off");
            gpioHwPin.write(stateOn);
        }
    }
}
