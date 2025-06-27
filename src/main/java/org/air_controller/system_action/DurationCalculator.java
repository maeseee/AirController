package org.air_controller.system_action;

import lombok.RequiredArgsConstructor;
import org.air_controller.system.OutputState;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class DurationCalculator {

    private final List<SystemAction> systemActions;

    public Duration getDuration(ZonedDateTime startTime, ZonedDateTime endTime) {
        if (systemActions.size() < 2) {
            return Duration.ZERO;
        }

        try {
            final List<SystemAction> actionsWithStartAndEnd = cutListToStartAndEnd(startTime, endTime);
            final List<Duration> onDurations = convertToOnDurationList(actionsWithStartAndEnd);
            return onDurations.stream().reduce(Duration.ZERO, Duration::plus);
        } catch (ActionException e) {
            return Duration.ZERO;
        }
    }


    private List<SystemAction> cutListToStartAndEnd(ZonedDateTime startTime, ZonedDateTime endTime) throws ActionException {
        final List<SystemAction> actions = systemActions.stream()
                .filter(systemAction -> isActionInRange(systemAction.actionTime(), startTime, endTime))
                .collect(Collectors.toList());
        actions.addFirst(createLastAction(startTime));
        actions.addLast(createLastAction(endTime));
        return actions;
    }

    private SystemAction createLastAction(ZonedDateTime endTime) {
        final SystemAction lastActionInTimeRange = systemActions.stream()
                .filter(systemAction -> systemAction.actionTime().isBefore(endTime))
                .reduce(this::getLastAction)
                .orElse(new SystemAction(endTime, SystemPart.AIR_FLOW, OutputState.OFF));
        return new SystemAction(endTime, lastActionInTimeRange.systemPart(), lastActionInTimeRange.outputState());
    }

    private SystemAction getLastAction(SystemAction action1, SystemAction action2) {
        return action2.actionTime().isAfter(action1.actionTime()) ? action2 : action1;
    }

    private boolean isActionInRange(ZonedDateTime actionTime, ZonedDateTime startTime, ZonedDateTime endTime) {
        return startTime.isBefore(actionTime) && endTime.isAfter(actionTime);
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
