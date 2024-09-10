package org.airController.rules;

import java.time.Duration;

public class PeriodicallyAirFlow implements Rule {

    public static final double MAX_IMPACT = 0.5;

    private Timetraker timetraker;

    public PeriodicallyAirFlow(Timetraker timetraker) {
        this.timetraker = timetraker;
    }

    @Override
    public Percentage getAirFlowNeed() {
        Duration airFlowOnDurationInLastHour = timetraker.getAirFlowOnDurationInLastHour();
        double impact = -1.0 / 20 * airFlowOnDurationInLastHour.toMinutes() + 0.5;
        return new Percentage(impact, MAX_IMPACT);
    }
}
