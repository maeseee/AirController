package org.airController.system;

import org.airController.systemAdapter.ControlledVentilationSystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ControlledVentilationSystemTimeKeeper implements ControlledVentilationSystem {

    private final List<TimePeriod> timePeriods = new ArrayList<>();
    private LocalDateTime onTime;

    @Override
    public void setAirFlowOn(boolean on) {
        if (onTime == null) {
            onTime = LocalDateTime.now();
        } else {
            TimePeriod timePeriod = new TimePeriod(onTime, LocalDateTime.now());
            timePeriods.add(timePeriod);
            removeOldTimePeriods();
        }
    }

    @Override
    public void setHumidityExchangerOn(boolean on) {
        // No need for time keeping
    }

    private void removeOldTimePeriods() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        timePeriods.removeIf(timePeriod -> timePeriod.off().isBefore(yesterday));
    }
}
