package org.air_controller.sensor.moon_phases;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MoonPhaseCalculator {

    private static final ZonedDateTime FULL_MOON_REFERENCE = ZonedDateTime.parse("2024-01-25T17:54:00Z");
    private static final long lunarMonthDurationMs = getLunarMonthDuration().toMillis();

    public ZonedDateTime nextFullMoon(ZonedDateTime fromDate) {
        final double diffInMs = Duration.between(FULL_MOON_REFERENCE, fromDate).toMillis();
        final double cyclesPassed = diffInMs / lunarMonthDurationMs;
        final long nextCycle = (long) Math.ceil(cyclesPassed);
        final long msToNextFullMoon = nextCycle * lunarMonthDurationMs;
        return FULL_MOON_REFERENCE.plus(Duration.ofMillis(msToNextFullMoon));
    }

    public String toDateString(ZonedDateTime date) {
        final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd.MM.yyyy")
                .withZone(ZoneId.of("Europe/Berlin"));
        return date.format(formatter);
    }

    private static Duration getLunarMonthDuration() {
        return Duration.ofDays(29)
                .plusHours(12)
                .plusMinutes(44)
                .plusSeconds(2)
                .plusMillis(801)
                .plusNanos(600);
    }
}
