package org.air_controller.gpio;

public interface GpioPin {

    boolean getGpioState();

    void setGpioState(boolean stateOn);
}
