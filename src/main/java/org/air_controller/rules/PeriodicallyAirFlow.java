package org.air_controller.rules;

import lombok.RequiredArgsConstructor;
import org.air_controller.system_action.DurationCalculator;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
class PeriodicallyAirFlow implements Rule {

    private static final Duration TWO_HOURLY_FRESH_AIR = Duration.ofMinutes(60);
    private static final double B = 1; // y = xm + b
    private static final double M = -B / TWO_HOURLY_FRESH_AIR.toMinutes(); // y = xm + b
    private static final double CONFIDENCE_WEIGHT = 0.4;

    private final SystemActionDbAccessor dbAccessor;

    @Override
    public String name() {
        return "Two hourly air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Duration airFlowOnDurationInLastHour = getOnDurationOfLastTwoHours();
        final double impact = M * airFlowOnDurationInLastHour.toMinutes() + B;
        return new Confidence(impact, CONFIDENCE_WEIGHT);
    }

    public Duration getOnDurationOfLastTwoHours() {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime startTime = endTime.minusHours(3); // Just enough
        final List<SystemAction> actionsFromLastHour = dbAccessor.getActionsFromTimeToNow(startTime);
        final DurationCalculator durationCalculator = new DurationCalculator(actionsFromLastHour);
        return durationCalculator.getDuration(startTime, endTime);
    }
}
