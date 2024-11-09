package org.airController.rules;

import java.time.Duration;

public interface TimeKeeper extends Runnable {
    Duration getAirFlowOnDurationInLastHour();
}
