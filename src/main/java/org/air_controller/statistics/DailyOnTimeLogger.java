package org.air_controller.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.air_controller.system_action.DurationCalculator;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;

import java.time.*;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class DailyOnTimeLogger implements Runnable {
    private final SystemActionDbAccessor dbAccessor;

    @Override
    public void run() {
        try {
            final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            final LocalDate yesterday = now.toLocalDate().minusDays(1);
            final Duration totalAirFlowYesterday = getTotalFromDay(yesterday);
            log.info("The daily switch-on time of {} was {} minutes ({} %)", yesterday, totalAirFlowYesterday.toMinutes(),
                    getOnPercentage(totalAirFlowYesterday));
        } catch (Exception e) {
            log.error("Exception occurred while running VentilationSystemTimeKeeper! ", e);
        }
    }

    Duration getTotalFromDay(LocalDate day) {
        final ZonedDateTime startTime = day.atStartOfDay(ZoneOffset.UTC);
        final ZonedDateTime endTime = ZonedDateTime.of(day.atTime(LocalTime.MAX), ZoneOffset.UTC);
        final List<SystemAction> actionsFromLastDay = dbAccessor.getActionsFromTimeToNow(startTime.minusHours(2)); // just enough data
        final DurationCalculator durationCalculator = new DurationCalculator(actionsFromLastDay);
        return durationCalculator.getDuration(startTime, endTime);
    }

    private double getOnPercentage(Duration onTime) {
        final long SECONDS_PER_DAY = 60L * 60L * 24L;
        return Math.round((double) onTime.toSeconds() / (double) SECONDS_PER_DAY * 1000.0) / 10.0;
    }
}
