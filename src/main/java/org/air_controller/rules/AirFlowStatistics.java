package org.air_controller.rules;

import java.time.Duration;

public interface AirFlowStatistics extends Runnable {
    Duration getAirFlowOnDurationInLastHour();
}
