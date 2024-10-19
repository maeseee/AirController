package org.airController.system;

import com.google.common.annotations.VisibleForTesting;
import org.airController.rules.TimeKeeper;
import org.airController.systemPersitence.SystemAction;
import org.airController.systemPersitence.SystemActions;
import org.airController.systemPersitence.SystemPart;
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
    private final SystemActions systemActions;
    private LocalDateTime onTime;

    public VentilationSystemTimeKeeper(SystemActions systemActions) {
        this.systemActions = systemActions;
    }

    @Override
    public void setAirFlowOn(OutputState state) {
        { // TODO delete after refactoring
            final LocalDateTime now = LocalDateTime.now();
            if (state.isOn() && onTime == null) {
                onTime = now;
            }
            if (!state.isOn() && onTime != null) {
                final TimePeriod timePeriod = new TimePeriod(onTime, now);
                timePeriods.add(timePeriod);
                onTime = null;
            }
        }

        systemActions.setAirFlowOn(state);
    }

    @Override
    public void setHumidityExchangerOn(OutputState state) {
        systemActions.setHumidityExchangerOn(state);
    }

    @Override
    public Duration getAirFlowOnDurationInLastHour() {
        final LocalDateTime endTime = LocalDateTime.now();
        final LocalDateTime startTime = endTime.minusHours(1);
        final List<SystemAction> actionsFromLastHour = systemActions.getActionsFromTimeToNow(startTime, SystemPart.AIR_FLOW);
        return getDuration(actionsFromLastHour, startTime, endTime);
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

    private Duration getDuration(List<SystemAction> actionsFromLastHour, LocalDateTime startTime, LocalDateTime endTime) {
        if (actionsFromLastHour.isEmpty()) {
            return Duration.ZERO;
        }
        final List<SystemAction> actionsWithStartAndEnd = addStartAndEndActions(actionsFromLastHour, startTime, endTime);
        return actionsWithStartAndEnd.stream()
                .map(systemAction -> systemAction.outputState().isOn() ?
                        Duration.between(systemAction.actionTime(), startTime) :
                        Duration.between(startTime, systemAction.actionTime()))
                .reduce(Duration.ZERO, Duration::plus);

    }

    private List<SystemAction> addStartAndEndActions(List<SystemAction> actionsFromLastHour, LocalDateTime startTime, LocalDateTime endTime) {
        final SystemAction firstSystemAction = actionsFromLastHour.get(0);
        final List<SystemAction> actionsWithStartAndEnd = new ArrayList<>(actionsFromLastHour);
        if (firstSystemAction.outputState() == OutputState.OFF) {
            actionsWithStartAndEnd.add(0, new SystemAction(startTime, firstSystemAction.systemPart(), OutputState.ON));
        }
        final SystemAction lastSystemAction = actionsFromLastHour.get(actionsFromLastHour.size() - 1);
        if (lastSystemAction.outputState() == OutputState.ON) {
            actionsWithStartAndEnd.add(new SystemAction(endTime, firstSystemAction.systemPart(), OutputState.OFF));
        }
        return actionsWithStartAndEnd;
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
