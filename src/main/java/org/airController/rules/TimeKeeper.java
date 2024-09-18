package org.airController.rules;

import java.time.Duration;
import java.time.LocalDate;

public interface TimeKeeper {
    Duration getAirFlowOnDurationInLastHour();

    Duration getTotalAirFlowFromDay(LocalDate day);
}
