package org.air_controller.rules.airflow;

import org.air_controller.rules.Confidence;
import org.air_controller.system_action.DurationCalculator;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Component
class PeriodicallyAirFlow implements AirFlowRule {

    private static final Duration THREE_HOURLY_FRESH_AIR = Duration.ofMinutes(180);
    private static final double B = 1; // y = xm + b
    private static final double M = -B / (THREE_HOURLY_FRESH_AIR.toMinutes() / 2.0); // y = xm + b
    private static final double CONFIDENCE_WEIGHT = 0.3;

    private final SystemActionDbAccessor dbAccessor;

    PeriodicallyAirFlow(@Qualifier("airFlowAccessor")SystemActionDbAccessor dbAccessor) {
        this.dbAccessor = dbAccessor;
    }

    @Override
    public String name() {
        return "Three hourly air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Duration onDuration = getOnDuration(THREE_HOURLY_FRESH_AIR);
        final double impact = M * onDuration.toMinutes() + B;
        return Confidence.createWeighted(impact, CONFIDENCE_WEIGHT);
    }

    public Duration getOnDuration(Duration onDuration) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final Duration duration = onDuration.plus(Duration.ofHours(1)); // Just a little bit more
        final List<SystemAction> actionsFromLastHour = dbAccessor.getActions(duration);
        final DurationCalculator durationCalculator = new DurationCalculator(actionsFromLastHour);
        return durationCalculator.getDuration(now.minus(duration), now);
    }
}
