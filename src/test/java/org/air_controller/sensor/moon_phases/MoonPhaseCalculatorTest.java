package org.air_controller.sensor.moon_phases;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class MoonPhaseCalculatorTest {

    @ParameterizedTest(name = "{index} => fromDateString={0}, expectedNextFullMoonString={1}")
    @CsvSource({
            "2026-06-23, 2026-06-29 23:56 Z",
            "2026-12-10, 2026-12-24 01:28 Z",
            "2024-01-23, 2024-01-25 17:54 Z"
    })
    public void testCalculateMoonPhase(String fromDateString, String expectedNextFullMoonString) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm X");
        final ZonedDateTime fromDate = ZonedDateTime.parse(fromDateString + " 00:00 Z", formatter);
        final MoonPhaseCalculator testee = new MoonPhaseCalculator();

        final ZonedDateTime fullMoon = testee.nextFullMoon(fromDate);

        final ZonedDateTime expectedFullMoon = ZonedDateTime.parse(expectedNextFullMoonString, formatter);
        assertThat(fullMoon).isCloseTo(expectedFullMoon, within(Duration.ofHours(16))); // Toleranz for current calculation
    }
}
