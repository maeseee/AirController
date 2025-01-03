package org.air_controller.rules;

import java.time.Duration;

public interface TimeKeeper extends Runnable {
    Duration getAirFlowOnDurationInLastHour();
}
