package org.airController.rules;

import java.time.Duration;

public interface TimeKeeper {
    Duration getAirFlowOnDurationInLastHour();
}
