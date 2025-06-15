package org.air_controller.gpio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MockGpioPin implements GpioPin {
    private static final Logger logger = LogManager.getLogger(MockGpioPin.class);

    private final String name;
    private boolean gpioState;

    public MockGpioPin(String name, boolean initialHigh) {
        this.name = name;
        gpioState = initialHigh;
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            logger.info("{} set to {}", name, stateOn ? "on" : "off");
            gpioState = stateOn;
        }
    }

    boolean getGpioState() {
        return gpioState;
    }
}
