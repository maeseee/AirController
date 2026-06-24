package org.air_controller.sensor.moon_phases;

import org.air_controller.web_access.card_view.CardItem;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MoonPhaseCalculator {

    private static final ZonedDateTime FULL_MOON_REFERENCE = ZonedDateTime.parse("2024-01-25T17:54:00Z");
    private static final long lunarMonthDurationMs = getLunarMonthDuration().toMillis();

    public CardItem nextFullMoonCardItem() {
        final ZonedDateTime fullMoon = nextFullMoon(ZonedDateTime.now());
        return new CardItem("Next full moon", toDateString(fullMoon), "");
    }

    public ZonedDateTime nextFullMoon(ZonedDateTime today) {
        final ZonedDateTime fromDate = today.minusDays(1); // Make sure that todays full moon is included
        final double diffInMs = Duration.between(FULL_MOON_REFERENCE, fromDate).toMillis();
        final double cyclesPassed = diffInMs / lunarMonthDurationMs;
        final long nextCycle = (long) Math.ceil(cyclesPassed);
        final long msToNextFullMoon = nextCycle * lunarMonthDurationMs;
        return FULL_MOON_REFERENCE.plus(Duration.ofMillis(msToNextFullMoon));
    }

    String toDateString(ZonedDateTime date) {
        final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd.MM.yy")
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
