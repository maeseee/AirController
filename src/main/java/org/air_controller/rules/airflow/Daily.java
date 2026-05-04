package org.air_controller.rules.airflow;

import org.air_controller.rules.Confidence;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
class Daily implements AirFlowRule {

    public static final double CONFIDENCE_WEIGHT = 0.8;

    private static final LocalTime HEAT_PEAK_TIME_UTC = LocalTime.of(2, 0, 0);

    private final QuarterYear quarterYear = new QuarterYear();

    @Override
    public String name() {
        return "Daily air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final double seasonFactor = quarterYear.getSeasonFactor(MonthDay.from(now));
        final double confidence = getCosinus(now.toLocalTime());
        return Confidence.createWeighted(confidence * seasonFactor, CONFIDENCE_WEIGHT);
    }

    private double getCosinus(LocalTime timeNow) {
        final Duration diffToPeak = Duration.between(HEAT_PEAK_TIME_UTC, timeNow);
        final double hoursToPeak = diffToPeak.getSeconds() / 60.0 / 60.0;
        final Duration cosPeriodDuration = Duration.ofDays(1);
        final double b = 2 * Math.PI / cosPeriodDuration.toHours();
        return Math.cos(b * hoursToPeak);
    }
}
