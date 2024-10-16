package org.airController.system;

import com.google.common.annotations.VisibleForTesting;
import org.airController.rules.TimeKeeper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VentilationSystemTimeKeeper implements VentilationSystem, TimeKeeper {
    private static final Logger logger = LogManager.getLogger(VentilationSystemTimeKeeper.class);

    private final List<TimePeriod> timePeriods = new ArrayList<>();
    private LocalDateTime onTime;

    @Override
    public void setAirFlowOn(boolean on) {
        final LocalDateTime now = LocalDateTime.now();
        if (on && onTime == null) {
            onTime = now;
        }
        if (!on && onTime != null) {
            final TimePeriod timePeriod = new TimePeriod(onTime, now);
            timePeriods.add(timePeriod);
            onTime = null;
        }
    }

    @Override
    public void setHumidityExchangerOn(boolean on) {
        // No need for time keeping
    }

    @Override
    public Duration getAirFlowOnDurationInLastHour() {
        final LocalDateTime endTime = LocalDateTime.now();
        final LocalDateTime startTime = endTime.minusHours(1);
        return getDuration(startTime, endTime);
    }

    @Override
    public Duration getTotalAirFlowFromDay(LocalDate day) {
        final LocalDateTime startTime = day.atStartOfDay();
        final LocalDateTime endTime = day.atTime(LocalTime.MAX);
        return getDuration(startTime, endTime);
    }

    @Override
    public void run() {
        try {
            final LocalDateTime now = LocalDateTime.now();
            final LocalDate yesterday = now.toLocalDate().minusDays(1);
            final Duration totalAirFlowYesterday = getTotalAirFlowFromDay(yesterday);
            logger.info("The daily switch-on time  of {} was {} minutes ({} %)", yesterday, totalAirFlowYesterday.toMinutes(),
                    getOnPercentage(totalAirFlowYesterday));

            removeTimePeriods(yesterday);
        } catch (Exception e) {
            logger.error("Exception occurred while running VentilationSystemTimeKeeper! ", e);
        }
    }

    @VisibleForTesting
    void removeTimePeriods(LocalDate lastDayToKeep) {
        timePeriods.removeIf(timePeriod -> timePeriod.off().toLocalDate().isBefore(lastDayToKeep));
    }

    private Duration getDuration(LocalDateTime startTime, LocalDateTime endTime) {
        final ArrayList<TimePeriod> timePeriodsCopy = new ArrayList<>(timePeriods);
        if (onTime != null && onTime.isBefore(endTime)) {
            timePeriodsCopy.add(new TimePeriod(onTime, endTime));
        }
        return timePeriodsCopy.stream()
                .filter(timePeriod -> isBetween(startTime, endTime, timePeriod))
                .map(timePeriod -> getDurationInTimePeriod(timePeriod, startTime, endTime))
                .reduce(Duration.ZERO, Duration::plus);
    }

    private boolean isBetween(LocalDateTime startTime, LocalDateTime endTime, TimePeriod timePeriod) {
        return timePeriod.off().isAfter(startTime) && timePeriod.on().isBefore(endTime);
    }

    private Duration getDurationInTimePeriod(TimePeriod timePeriod, LocalDateTime startTime, LocalDateTime endTime) {
        final LocalDateTime durationStart = timePeriod.on().isAfter(startTime) ? timePeriod.on() : startTime;
        final LocalDateTime durationEnd = timePeriod.off().isBefore(endTime) ? timePeriod.off() : endTime;
        return Duration.between(durationStart, durationEnd);
    }

    private double getOnPercentage(Duration onTime) {
        final long SECONDS_PER_DAY = 60 * 60 * 24;
        return Math.round((double) onTime.toSeconds() / (double) SECONDS_PER_DAY * 1000.0) / 10.0;
    }
}
