package org.airController.util;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import org.airController.gpioAdapter.GpioFunction;

import java.io.IOException;

public class RaspberryPiPin {

    private final GpioFunction gpioFunction;

    public RaspberryPiPin(GpioFunction gpioFunction) throws IOException {
        this.gpioFunction = gpioFunction;
        setupWiringPi();
    }

    public void setMode(boolean input) {
        final int mode = input ? Gpio.INPUT : Gpio.OUTPUT;
        Gpio.pinMode(gpioFunction.getGpio(), mode);
    }

    public void export(boolean directionOut) {
        final int direction = directionOut ? GpioUtil.DIRECTION_IN : GpioUtil.DIRECTION_OUT;
        GpioUtil.export(gpioFunction.getGpio(), direction);
    }

    public void write(boolean high) {
        final int state = high ? Gpio.HIGH : Gpio.LOW;
        Gpio.digitalWrite(gpioFunction.getGpio(), state);
    }

    public boolean read() {
        final int state = Gpio.digitalRead(gpioFunction.getGpio());
        return state != 0;
    }

    public void sleep(int microseconds) {
        Gpio.delayMicroseconds(microseconds);
    }

    private void setupWiringPi() throws IOException {
        final int wiringPiStatus = com.pi4j.wiringpi.Gpio.wiringPiSetup();
        if (wiringPiStatus == -1) {
            throw new IOException("GPIO SETUP FAILED");
        }
    }
}
