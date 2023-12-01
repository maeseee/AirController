package org.airController.gpio;

import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.util.Logging;
import org.airController.util.RaspberryPiPin;

import java.io.IOException;
import java.time.LocalTime;

public class GpioPinImpl implements GpioPin {
    private final String name;
    private final RaspberryPiPin raspberryPiPin;
    private final DailyGpioStatistic dailyGpioStatistic;

    public GpioPinImpl(GpioFunction pinFunction, boolean initialHigh) throws IOException {
        this(pinFunction.name(), new RaspberryPiPin(pinFunction),initialHigh);
    }

    GpioPinImpl(String name, RaspberryPiPin raspberryPiPin, boolean initialHigh) {
        this.name = name;
        this.raspberryPiPin = raspberryPiPin;
        this.dailyGpioStatistic =  new DailyGpioStatistic(name, initialHigh);

        raspberryPiPin.export(true);
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
            Logging.getLogger().info(name + " set to " + (stateOn ? "on" : "off"));
            raspberryPiPin.write(stateOn);
        }
    }
}
