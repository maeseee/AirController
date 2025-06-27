package org.air_controller.system;

import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.air_controller.system_action.DurationCalculator;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.*;
import java.util.List;

@RequiredArgsConstructor
public class SystemStatistics implements Runnable {
    private static final Logger logger = LogManager.getLogger(SystemStatistics.class);

    private final SystemActionDbAccessor dbAccessor;

    @Override
    public void run() {
        try {
            final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            final LocalDate yesterday = now.toLocalDate().minusDays(1);
            final Duration totalAirFlowYesterday = getTotalFromDay(yesterday);
            logger.info("The daily switch-on time of {} was {} minutes ({} %)", yesterday, totalAirFlowYesterday.toMinutes(),
                    getOnPercentage(totalAirFlowYesterday));
        } catch (Exception e) {
            logger.error("Exception occurred while running VentilationSystemTimeKeeper! ", e);
        }
    }

    @VisibleForTesting
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
