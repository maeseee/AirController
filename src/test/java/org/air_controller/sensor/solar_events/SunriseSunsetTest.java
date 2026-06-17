package org.air_controller.sensor.solar_events;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class SunriseSunsetTest {

    @Test
    void shouldCalculateSolarEvents() {
        final SunriseSunset testee = new SunriseSunset();
        final LocalDate date = LocalDate.of(2026, 6, 16);

        final SolarEvent solarEvent = testee.solarEventsFrom(date);

        final ZonedDateTime expectedSunrise = ZonedDateTime.of(2026, 6, 16, 5, 31, 0, 0, ZoneId.of("Europe/Berlin"));
        final ZonedDateTime expectedSunset = ZonedDateTime.of(2026, 6, 16, 21, 25, 0, 0, ZoneId.of("Europe/Berlin"));
        assertThat(solarEvent.sunrise()).isCloseTo(expectedSunrise, within(1, ChronoUnit.MINUTES));
        assertThat(solarEvent.sunset()).isCloseTo(expectedSunset, within(1, ChronoUnit.MINUTES));
    }
}