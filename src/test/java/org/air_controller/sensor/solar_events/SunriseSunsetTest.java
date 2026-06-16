package org.air_controller.sensor.solar_events;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class SunriseSunsetTest {

    @Test
    void shouldCalculateSolarEvents() {
        final SunriseSunset testee = new SunriseSunset();
        final ZonedDateTime date = ZonedDateTime.of(2026, 6,16, 12, 0, 0, 0, ZoneId.of("UTC"));

        final SolarEvent solarEvent = testee.solarEventsFrom(date);

        final ZonedDateTime expectedSunrise = ZonedDateTime.of(2026, 6, 16, 5, 30, 53, 0, ZoneId.of("Europe/Berlin"));
        assertThat(solarEvent.sunrise()).isCloseTo(expectedSunrise, within(1, ChronoUnit.MINUTES));
    }
}