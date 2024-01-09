package org.airController.gpioAdapter;

public interface GpioPin {

    boolean getGpioState();

    boolean setGpioState(boolean stateOn);
}
