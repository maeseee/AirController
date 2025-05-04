package org.air_controller.rules;

import java.time.Duration;

public class PeriodicallyAirFlow implements Rule {

    private static final Duration HOURLY_FRESH_AIR = Duration.ofMinutes(30);
    private static final double B = 1; // y = xm + b
    private static final double M = -B / HOURLY_FRESH_AIR.toMinutes(); // y = xm + b

    private final TimeKeeper timeKeeper;

    public PeriodicallyAirFlow(TimeKeeper timeKeeper) {
        this.timeKeeper = timeKeeper;
    }

    @Override
    public String name() {
        return "Hourly air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Duration airFlowOnDurationInLastHour = timeKeeper.getAirFlowOnDurationInLastHour();
        final double impact = M * airFlowOnDurationInLastHour.toMinutes() + B;
        return new Confidence(impact);
    }
}
