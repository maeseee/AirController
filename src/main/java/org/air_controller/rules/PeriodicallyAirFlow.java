package org.air_controller.rules;

import org.air_controller.system.SystemStatistics;

import java.time.Duration;

class PeriodicallyAirFlow implements Rule {

    private static final Duration HOURLY_FRESH_AIR = Duration.ofMinutes(30);
    private static final double B = 1; // y = xm + b
    private static final double M = -B / HOURLY_FRESH_AIR.toMinutes(); // y = xm + b

    private final SystemStatistics statistics;

    public PeriodicallyAirFlow(SystemStatistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String name() {
        return "Hourly air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Duration airFlowOnDurationInLastHour = statistics.getOnDurationInLastHour();
        final double impact = M * airFlowOnDurationInLastHour.toMinutes() + B;
        return new Confidence(impact);
    }
}
