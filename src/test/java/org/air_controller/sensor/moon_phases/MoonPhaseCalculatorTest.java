package org.air_controller.sensor.moon_phases;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class MoonPhaseCalculatorTest {

    @ParameterizedTest(name = "{index} => fromDateString={0}, expectedNextFullMoonString={1}")
    @CsvSource({
            "2026-06-23, 2026-06-29T23:56:00Z",
            "2026-12-10, 2026-12-24T01:28:00Z",
            "2024-01-23, 2024-01-25T17:54:00Z"
    })
    public void testCalculateMoonPhase(String fromDateString, String expectedNextFullMoonString) {
        final ZonedDateTime fromDate = ZonedDateTime.parse(fromDateString + "T00:00:00Z");
        final MoonPhaseCalculator testee = new MoonPhaseCalculator();

        final ZonedDateTime fullMoon = testee.nextFullMoon(fromDate);

        final ZonedDateTime expectedFullMoon = ZonedDateTime.parse(expectedNextFullMoonString);
        assertThat(fullMoon).isCloseTo(expectedFullMoon, within(Duration.ofHours(16))); // Toleranz for current calculation
    }
}
