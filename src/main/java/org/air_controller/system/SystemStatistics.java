package org.air_controller.system;

import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
        final List<SystemAction> actionsFromLastDay = dbAccessor.getActionsFromTimeToNow(startTime);
        return getDuration(actionsFromLastDay, startTime, endTime);
    }

    private Duration getDuration(List<SystemAction> systemActions, ZonedDateTime startTime, ZonedDateTime endTime) {
        if (systemActions.isEmpty()) {
            return durationFromMostCurrentState();
        }
        final List<SystemAction> actionsWithStartAndEnd = addStartAndEndActions(systemActions, startTime, endTime);
        final List<Duration> onDurations = convertToOnDurationList(actionsWithStartAndEnd);
        return onDurations.stream().reduce(Duration.ZERO, Duration::plus);
    }

    private List<SystemAction> addStartAndEndActions(List<SystemAction> actionsFromLastHour, ZonedDateTime startTime, ZonedDateTime endTime) {
        final SystemAction firstSystemAction = actionsFromLastHour.getFirst();
        final List<SystemAction> actionsWithStartAndEnd = new ArrayList<>(actionsFromLastHour);
        if (firstSystemAction.outputState() == OutputState.OFF) {
            actionsWithStartAndEnd.addFirst(new SystemAction(startTime, firstSystemAction.systemPart(), OutputState.ON));
        }
        final SystemAction lastSystemAction = actionsFromLastHour.getLast();
        if (lastSystemAction.outputState() == OutputState.ON) {
            actionsWithStartAndEnd.add(new SystemAction(endTime, firstSystemAction.systemPart(), OutputState.OFF));
        }
        return actionsWithStartAndEnd;
    }

    private double getOnPercentage(Duration onTime) {
        final long SECONDS_PER_DAY = 60L * 60L * 24L;
        return Math.round((double) onTime.toSeconds() / (double) SECONDS_PER_DAY * 1000.0) / 10.0;
    }

    private List<Duration> convertToOnDurationList(List<SystemAction> systemActions) {
        assert (systemActions.size() > 1);
        return IntStream.range(1, systemActions.size())
                .mapToObj(i -> getDurationBetweenSystemAction(systemActions.get(i - 1), systemActions.get(i)))
                .toList();
    }

    private Duration getDurationBetweenSystemAction(SystemAction systemAction1, SystemAction systemAction2) {
        if (systemAction1.outputState().isOn()) {
            return Duration.between(systemAction1.actionTime(), systemAction2.actionTime());
        }
        return Duration.ZERO;
    }

    private Duration durationFromMostCurrentState() {
        final OutputState state = dbAccessor.getMostCurrentState()
                .map(SystemAction::outputState)
                .orElse(OutputState.OFF);
        return state == OutputState.ON ? Duration.ofHours(1) : Duration.ZERO;
    }
}
