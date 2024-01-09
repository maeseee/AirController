package org.airController.gpio;

import org.airController.gpioAdapter.GpioPin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MockGpioPin implements GpioPin{
    private static final Logger logger = LogManager.getLogger(MockGpioPin.class);

    private final String name;
    private boolean gpioState;

    public MockGpioPin (String name, boolean initialHigh){
        this.name = name;
        gpioState = initialHigh;
    }

    @Override
    public boolean getGpioState() {
        return gpioState;
    }

    @Override
    public boolean setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            logger.info(name + " set to " + (stateOn ? "on" : "off"));
            gpioState = stateOn;
            return true;
        }
        return false;
    }
}
