package org.air_controller.system;

import com.google.common.annotations.VisibleForTesting;
import org.air_controller.rules.TimeKeeper;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class VentilationSystemTimeKeeper implements VentilationSystem, TimeKeeper {
    private static final Logger logger = LogManager.getLogger(VentilationSystemTimeKeeper.class);

    private final SystemActionDbAccessor airFlowDbAccessor;
    private OutputState currentAirFlowState;

    public VentilationSystemTimeKeeper(SystemActionDbAccessor airFlowDbAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
    }

    @Override
    public void setAirFlowOn(OutputState state) {
        currentAirFlowState = state;
    }

    @Override
    public void setHumidityExchangerOn(OutputState state) {
    }

    @Override
    public Duration getAirFlowOnDurationInLastHour() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(1);
        final List<SystemAction> actionsFromLastHour = airFlowDbAccessor.getActionsFromTimeToNow(startTime);
        return getDuration(actionsFromLastHour, startTime, endTime);
    }

    @Override
    public void run() {
        try {
            final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            final LocalDate yesterday = now.toLocalDate().minusDays(1);
            final Duration totalAirFlowYesterday = getTotalAirFlowFromDay(yesterday);
            logger.info("The daily switch-on time of {} was {} minutes ({} %)", yesterday, totalAirFlowYesterday.toMinutes(),
                    getOnPercentage(totalAirFlowYesterday));
        } catch (Exception e) {
            logger.error("Exception occurred while running VentilationSystemTimeKeeper! ", e);
        }
    }

    @VisibleForTesting
    Duration getTotalAirFlowFromDay(LocalDate day) {
        final ZonedDateTime startTime = day.atStartOfDay(ZoneOffset.UTC);
        final ZonedDateTime endTime = ZonedDateTime.of(day.atTime(LocalTime.MAX), ZoneOffset.UTC);
        final List<SystemAction> actionsFromLastDay = airFlowDbAccessor.getActionsFromTimeToNow(startTime);
        return getDuration(actionsFromLastDay, startTime, endTime);
    }

    private Duration getDuration(List<SystemAction> systemActions, ZonedDateTime startTime, ZonedDateTime endTime) {
        if (systemActions.isEmpty()) {
            return currentAirFlowState == OutputState.ON ? Duration.between(startTime, endTime) : Duration.ZERO;
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
}
