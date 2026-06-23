package org.air_controller.sensor.moon_phases;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class MoonPhaseCalculatorTest {

    @Test
    public void testCalculateMoonPhase() {
        final ZonedDateTime fromDate = ZonedDateTime.of(2026, 6, 23, 20, 9, 0, 0, ZoneOffset.UTC);
        final MoonPhaseCalculator testee = new MoonPhaseCalculator();

        final ZonedDateTime fullMoon = testee.nextFullMoon(fromDate);

        final ZonedDateTime expextedFullMoon = ZonedDateTime.of(2026, 6, 29, 23, 56, 0, 0, ZoneOffset.UTC);;
        assertThat(fullMoon).isCloseTo(expextedFullMoon, within(Duration.ofHours(10)));
    }
}
