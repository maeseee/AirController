package org.air_controller.rules;

import org.air_controller.system.SystemStatistics;

import java.time.Duration;

class PeriodicallyAirFlow implements Rule {

    private static final Duration TWO_HOURLY_FRESH_AIR = Duration.ofMinutes(60);
    private static final double B = 1; // y = xm + b
    private static final double M = -B / TWO_HOURLY_FRESH_AIR.toMinutes(); // y = xm + b

    private final SystemStatistics statistics;

    public PeriodicallyAirFlow(SystemStatistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String name() {
        return "Two hourly air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Duration airFlowOnDurationInLastHour = statistics.getOnDurationOfLastTwoHours();
        final double impact = M * airFlowOnDurationInLastHour.toMinutes() + B;
        return new Confidence(impact, 0.5);
    }
}
