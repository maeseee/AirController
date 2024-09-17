package org.airController.system;

import org.airController.rules.TimeKeeper;
import org.airController.systemAdapter.ControlledVentilationSystem;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ControlledVentilationSystemTimeKeeper implements ControlledVentilationSystem, TimeKeeper {

    private final List<TimePeriod> timePeriods = new ArrayList<>();
    private LocalDateTime onTime;

    @Override
    public void setAirFlowOn(boolean on) {
        LocalDateTime now = LocalDateTime.now();
        if (onTime == null) {
            onTime = now;
        } else {
            TimePeriod timePeriod = new TimePeriod(onTime, now);
            timePeriods.add(timePeriod);
            removeOldTimePeriods(now);
        }
    }

    @Override
    public void setHumidityExchangerOn(boolean on) {
        // No need for time keeping
    }

    @Override
    public Duration getAirFlowOnDurationInLastHour() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(1);
        return getDuration(startTime, endTime);
    }

    private void removeOldTimePeriods(LocalDateTime now) {
        LocalDateTime yesterday = now.minusDays(1);
        timePeriods.removeIf(timePeriod -> timePeriod.off().isBefore(yesterday));
    }

    private Duration getDuration(LocalDateTime startTime, LocalDateTime endTime) {
        return timePeriods.stream()
                .filter(timePeriod -> timePeriod.on().isBefore(endTime))
                .filter(timePeriod -> timePeriod.off().isAfter(startTime))
                .map(timePeriod -> getDurationInTimePeriod(timePeriod, startTime, endTime))
                .reduce(Duration.ZERO, Duration::plus);
    }

    private Duration getDurationInTimePeriod(TimePeriod timePeriod, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime durationStart = timePeriod.on().isAfter(startTime) ? timePeriod.on() : startTime;
        LocalDateTime durationEnd = timePeriod.off().isBefore(endTime) ? timePeriod.off() : endTime;
        return Duration.between(durationStart, durationEnd);
    }


}
