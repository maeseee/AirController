package org.airController.system;

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
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class VentilationSystemTimeKeeper implements VentilationSystem, TimeKeeper {
    private static final Logger logger = LogManager.getLogger(VentilationSystemTimeKeeper.class);

    private final SystemActions systemActions;
    private OutputState currentAirFlowState;

    public VentilationSystemTimeKeeper(SystemActions systemActions) {
        this.systemActions = systemActions;
    }

    @Override
    public void setAirFlowOn(OutputState state) {
        currentAirFlowState = state;
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
        final List<SystemAction> actionsFromLastDay = systemActions.getActionsFromTimeToNow(startTime, SystemPart.AIR_FLOW);
        return getDuration(actionsFromLastDay, startTime, endTime);
    }

    @Override
    public void run() {
        try {
            final LocalDateTime now = LocalDateTime.now();
            final LocalDate yesterday = now.toLocalDate().minusDays(1);
            final Duration totalAirFlowYesterday = getTotalAirFlowFromDay(yesterday);
            logger.info("The daily switch-on time of {} was {} minutes ({} %)", yesterday, totalAirFlowYesterday.toMinutes(),
                    getOnPercentage(totalAirFlowYesterday));
        } catch (Exception e) {
            logger.error("Exception occurred while running VentilationSystemTimeKeeper! ", e);
        }
    }

    private Duration getDuration(List<SystemAction> systemActions, LocalDateTime startTime, LocalDateTime endTime) {
        if (systemActions.isEmpty()) {
            return currentAirFlowState == OutputState.ON ? Duration.between(startTime, endTime) : Duration.ZERO;
        }
        final List<SystemAction> actionsWithStartAndEnd = addStartAndEndActions(systemActions, startTime, endTime);
        final List<Duration> onDurations = convertToOnDurationList(actionsWithStartAndEnd);
        return onDurations.stream().reduce(Duration.ZERO, Duration::plus);
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

    private double getOnPercentage(Duration onTime) {
        final long SECONDS_PER_DAY = 60 * 60 * 24;
        return Math.round((double) onTime.toSeconds() / (double) SECONDS_PER_DAY * 1000.0) / 10.0;
    }

    private List<Duration> convertToOnDurationList(List<SystemAction> systemActions) {
        assert (systemActions.size() > 1);
        return IntStream.range(1, systemActions.size())
                .mapToObj(i -> getDurationBetweenSystemAction(systemActions.get(i - 1), systemActions.get(i)))
                .collect(toList());
    }

    private Duration getDurationBetweenSystemAction(SystemAction systemAction1, SystemAction systemAction2) {
        if (systemAction1.outputState().isOn() && !systemAction2.outputState().isOn()) {
            return Duration.between(systemAction1.actionTime(), systemAction2.actionTime());
        }
        return Duration.ZERO;
    }
}
