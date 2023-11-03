package org.airController.gpio;

import org.airController.gpioAdapter.GpioPin;

public class GpioPinSpy implements GpioPin {

    private boolean stateOn = false;

    @Override
    public boolean getGpioState() {
        return stateOn;
    }

    @Override
    public void setGpioState(boolean stateOn) {
        this.stateOn = stateOn;
    }
}
