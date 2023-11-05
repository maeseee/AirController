package org.airController.gpio;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class GpioPinImpl implements GpioPin {
    private static final Logger logger = Logger.getLogger(GpioPinImpl.class.getName());

    private final GpioFunction pinFunction;

    public GpioPinImpl(GpioFunction pinFunction) {
        this.pinFunction = pinFunction;

        if (Gpio.wiringPiSetup() == -1) {
            System.out.println(" ==>> GPIO SETUP FAILED");
//            throw new IOException("GPIO SETUP FAILED");
        }
        GpioUtil.export(pinFunction.getGpio(), GpioUtil.DIRECTION_OUT);
    }

    @Override
    public boolean getGpioState() {
        final int pinState = Gpio.digitalRead(pinFunction.getGpio());
        return mapToStateOn(pinState);
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            logger.info(pinFunction.name() + " set to " + (stateOn ? "on" : "off"));
            Gpio.digitalWrite(pinFunction.getGpio(), mapToPinState(stateOn));
        }
    }

    private int mapToPinState(boolean stateOn) {
        return stateOn ? Gpio.HIGH : Gpio.LOW;
    }

    private boolean mapToStateOn(int state) {
        return state != 0;
    }

    public static void main(String[] args) {
        final GpioPinImpl gpioPin = new GpioPinImpl(GpioFunction.MAIN_SYSTEM);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> gpioPin.setGpioState(!gpioPin.getGpioState()), 0, 2, TimeUnit.SECONDS);
    }
}
