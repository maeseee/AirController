package org.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HumidityTest {

    @Test
    void testRelativHumidity() {
        final double relativeHumidity = 50.0;
        final Humidity testee = Humidity.createFromRelative(relativeHumidity);

        final double result = testee.getRelativeHumidity();

        assertThat(result, is(relativeHumidity));
    }

    @ParameterizedTest
    @CsvSource({
            "-20.0, 0.9", "-15.0, 1.4", "-10.0, 2.1", "-5.0, 3.3", "0.0, 4.8", "5.0, 6.8", "10.0, 9.4",
            "15.0, 12.8", "20.0, 17.3", "25.0, 23.0", "30.0, 30.3", "35.0, 39.6", "40.0, 51.1"})
    void testCalculationOfAbsoluteHumidity(double temperatureCelsius, double weightFor100Procent) {
        final double relativeHumidity = 100.0;
        final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
        final Humidity testee = Humidity.createFromRelative(relativeHumidity);

        final double result = testee.getAbsoluteHumidity(temperature);

        final double expectedMaxDeltaInProcent = 25.0;
        final double expectedMaxDelta = expectedMaxDeltaInProcent * weightFor100Procent / 100.0;
        assertEquals(weightFor100Procent, result, expectedMaxDelta);
    }
}