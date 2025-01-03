package org.air_controller.gpio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RaspberryGpioPin implements GpioPin {
    private static final Logger logger = LogManager.getLogger(RaspberryGpioPin.class);

    private final String name;
    private final RaspberryPiPin raspberryPiPin;

    public RaspberryGpioPin(GpioFunction pinFunction, boolean initialHigh) throws IOException {
        this(pinFunction.name(), new RaspberryPiPin(pinFunction), initialHigh);
    }

    public RaspberryGpioPin(String name, RaspberryPiPin raspberryPiPin, boolean initialHigh) {
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
