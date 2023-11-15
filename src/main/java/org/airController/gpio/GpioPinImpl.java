package org.airController.gpio;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.util.Logging;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GpioPinImpl implements GpioPin {
    private final GpioFunction pinFunction;
    private final DailyGpioStatistic dailyGpioStatistic;

    public GpioPinImpl(GpioFunction pinFunction, boolean initialState) throws IOException {
        this.pinFunction = pinFunction;
        this.dailyGpioStatistic = new DailyGpioStatistic(pinFunction.name(), initialState);

        if (Gpio.wiringPiSetup() == -1) {
            throw new IOException("GPIO SETUP FAILED");
        }
        GpioUtil.export(pinFunction.getGpio(), GpioUtil.DIRECTION_OUT);
        setGpioState(initialState);
    }

    @Override
    public boolean getGpioState() {
        final int pinState = Gpio.digitalRead(pinFunction.getGpio());
        return mapToStateOn(pinState);
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            Logging.getLogger().info(pinFunction.name() + " set to " + (stateOn ? "on" : "off"));
            Gpio.digitalWrite(pinFunction.getGpio(), mapToPinState(stateOn));
            dailyGpioStatistic.stateChange(stateOn, LocalTime.now());
        }
    }

    private int mapToPinState(boolean stateOn) {
        return stateOn ? Gpio.HIGH : Gpio.LOW;
    }

    private boolean mapToStateOn(int state) {
        return state != 0;
    }

    public static void main(String[] args) throws IOException {
        final GpioPinImpl gpioPin = new GpioPinImpl(GpioFunction.MAIN_SYSTEM, true);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> gpioPin.setGpioState(!gpioPin.getGpioState()), 0, 2, TimeUnit.SECONDS);
    }
}
