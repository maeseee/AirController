package org.airController.gpio;

import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.util.RaspberryPiPin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalTime;

public class GpioPinImpl implements GpioPin {
    private static final Logger logger = LogManager.getLogger(GpioPinImpl.class);

    private final String name;
    private final RaspberryPiPin raspberryPiPin;
    private final DailyGpioStatistic dailyGpioStatistic;

    public GpioPinImpl(GpioFunction pinFunction, boolean initialHigh) throws IOException {
        this(pinFunction.name(), new RaspberryPiPin(pinFunction), initialHigh);
    }

    public GpioPinImpl(String name, RaspberryPiPin raspberryPiPin, boolean initialHigh) {
        this.name = name;
        this.raspberryPiPin = raspberryPiPin;
        this.dailyGpioStatistic = new DailyGpioStatistic(name, initialHigh);

        raspberryPiPin.export(true);
        logger.info("{} set initial to {}", name, initialHigh ? "on" : "off");
        raspberryPiPin.write(initialHigh);
    }

    @Override
    public boolean getGpioState() {
        return raspberryPiPin.read();
    }

    @Override
    public void setGpioState(boolean stateOn) {
        dailyGpioStatistic.stateChange(stateOn, LocalTime.now());
        if (getGpioState() != stateOn) {
            logger.info("{} set to {}", name, stateOn ? "on" : "off");
            raspberryPiPin.write(stateOn);
        }
    }
}
