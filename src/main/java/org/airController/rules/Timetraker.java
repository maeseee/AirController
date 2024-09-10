package org.airController.rules;

import java.time.Duration;

public class Timetraker {
    public Duration getAirFlowOnDurationInLastHour() {
        return Duration.ofHours(1);
    }
}
