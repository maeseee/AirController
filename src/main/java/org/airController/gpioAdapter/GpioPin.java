package org.airController.gpioAdapter;

public interface GpioPin {

    boolean getGpioState();

    void setGpioState(boolean stateOn);
}
