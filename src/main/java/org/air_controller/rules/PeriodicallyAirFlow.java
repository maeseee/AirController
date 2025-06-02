package org.air_controller.rules;

import java.time.Duration;

public class PeriodicallyAirFlow implements Rule {

    private static final Duration HOURLY_FRESH_AIR = Duration.ofMinutes(30);
    private static final double B = 1; // y = xm + b
    private static final double M = -B / HOURLY_FRESH_AIR.toMinutes(); // y = xm + b

    private final AirFlowStatistics statistics;

    public PeriodicallyAirFlow(AirFlowStatistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String name() {
        return "Hourly air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Duration airFlowOnDurationInLastHour = statistics.getAirFlowOnDurationInLastHour();
        final double impact = M * airFlowOnDurationInLastHour.toMinutes() + B;
        return new Confidence(impact);
    }
}
