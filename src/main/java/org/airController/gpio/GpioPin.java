package org.airController.gpio;

public interface GpioPin {

    boolean getGpioState();

    void setGpioState(boolean stateOn);
}
