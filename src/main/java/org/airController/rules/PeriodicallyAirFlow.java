package org.airController.rules;

import java.time.Duration;

public class PeriodicallyAirFlow implements Rule {

    private static final double MAX_IMPACT = 0.5;
    private static final Duration HOURLY_FRESH_AIR = Duration.ofMinutes(10);
    private static final double B = MAX_IMPACT; // y = xm + b
    private static final double M = -B / HOURLY_FRESH_AIR.toMinutes(); // y = xm + b

    private final Timetraker timetraker;

    public PeriodicallyAirFlow(Timetraker timetraker) {
        this.timetraker = timetraker;
    }

    @Override
    public Percentage getAirFlowNeed() {
        Duration airFlowOnDurationInLastHour = timetraker.getAirFlowOnDurationInLastHour();
        double impact = M * airFlowOnDurationInLastHour.toMinutes() + B;
        return new Percentage(impact, -MAX_IMPACT, MAX_IMPACT);
    }
}