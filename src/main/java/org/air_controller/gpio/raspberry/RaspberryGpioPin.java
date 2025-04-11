package org.air_controller.gpio.raspberry;

import org.air_controller.gpio.GpioPin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RaspberryGpioPin implements GpioPin {
    private static final Logger logger = LogManager.getLogger(RaspberryGpioPin.class);

    private final String name;
    private final RaspberryPin raspberryPiPin;

    public RaspberryGpioPin(GpioFunction pinFunction, boolean initialHigh) throws IOException {
        this(pinFunction.name(), new RaspberryPin(pinFunction), initialHigh);
    }

    public RaspberryGpioPin(String name, RaspberryPin raspberryPiPin, boolean initialHigh) {
        this.name = name;
        this.raspberryPiPin = raspberryPiPin;

        raspberryPiPin.export(true);
        logger.info("{} set initial to {}", name, initialHigh ? "on" : "off");
        raspberryPiPin.write(initialHigh);
    }

    @Override
    public boolean getGpioState() {
        return raspberryPiPin.read();
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            logger.info("{} set to {}", name, stateOn ? "on" : "off");
            raspberryPiPin.write(stateOn);
        }
    }
}
