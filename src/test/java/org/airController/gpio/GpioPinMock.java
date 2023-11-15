package org.airController.gpio;

import org.airController.gpioAdapter.GpioPin;
import org.airController.util.Logging;

import java.time.LocalTime;

public class GpioPinMock implements GpioPin {
    private final String name;
    private final DailyGpioStatistic dailyGpioStatistic;

    private boolean stateOn = false;

    public GpioPinMock(String name) {
        this.name = name;
        this.dailyGpioStatistic = new DailyGpioStatistic(name, false);
    }
    @Override
    public boolean getGpioState() {
        return stateOn;
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (stateOn != this.stateOn) {
            Logging.getLogger().info(name + " set to " + (stateOn ? "on" : "off"));
            this.stateOn = stateOn;
            dailyGpioStatistic.stateChange(stateOn, LocalTime.now());
        }
    }
}
