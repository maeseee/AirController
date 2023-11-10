package org.airController.gpio;

import org.airController.gpioAdapter.GpioPin;

import java.util.logging.Logger;

public class GpioPinMock implements GpioPin {
    private static final Logger logger = Logger.getLogger(GpioPinImpl.class.getName());

    private final String name;

    private boolean stateOn = false;

    public GpioPinMock(String name) {
        this.name = name;
    }
    @Override
    public boolean getGpioState() {
        return stateOn;
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (stateOn != this.stateOn) {
            logger.info(name + " set to " + (stateOn ? "on" : "off"));
            this.stateOn = stateOn;
        }
    }
}
