package org.air_controller.statistics;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.system_action.DurationCalculator;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;

@Slf4j
@Component
public class DailyOnTimeLogger {

    private final SystemActionDbAccessor dbAccessor;

    public DailyOnTimeLogger(@Qualifier("airFlowAccessor") SystemActionDbAccessor dbAccessor) {
        this.dbAccessor = dbAccessor;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    public void runAtMidnightUtc() {
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
        final List<SystemAction> actionsFromLastDay = dbAccessor.getActions(Duration.ofDays(1)); // just enough data
        final DurationCalculator durationCalculator = new DurationCalculator(actionsFromLastDay);
        return durationCalculator.getDuration(startTime, endTime);
    }

    private double getOnPercentage(Duration onTime) {
        final long SECONDS_PER_DAY = 60L * 60L * 24L;
        return Math.round((double) onTime.toSeconds() / (double) SECONDS_PER_DAY * 1000.0) / 10.0;
    }
}
