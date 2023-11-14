package org.airController.gpio;

import org.airController.util.Logging;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

class DailyGpioStatistic {
    private static final LocalTime NEW_DAY = LocalTime.of(0, 0, 0);
    private static final long SECONDS_PER_DAY = 60 * 60 * 24;

    private final String name;

    private LocalTime lastTimeStamp = NEW_DAY;
    private boolean lastState;
    private long totalDailyOnTime = 0;

    public DailyGpioStatistic(String name, boolean initialState) {
        this.name = name;
        this.lastState = initialState;
    }

    public double stateChange(boolean newState, LocalTime now) {
        if (now.isBefore(lastTimeStamp)) {
            return startNewDay(newState);
        }

        if (newState != lastState) {
            if (!newState) {
                totalDailyOnTime += ChronoUnit.SECONDS.between(lastTimeStamp, now);
            }
            lastState = newState;
            lastTimeStamp = now;
        }

        return getOnPercentage(ChronoUnit.SECONDS.between(NEW_DAY, now));

    }

    private double startNewDay(boolean initialState) {
        final double onPercentage = getOnPercentage(SECONDS_PER_DAY);
        Logging.getLogger().info("Daily on time of " + name + " is " + onPercentage);
        lastTimeStamp = NEW_DAY;
        lastState = initialState;
        return onPercentage;
    }

    private double getOnPercentage(long totalSeconds) {
        return Math.round((double) totalDailyOnTime / (double) totalSeconds * 1000.0) / 10.0;
    }

}
