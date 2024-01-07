package org.airController.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HumidityTest {

    @ParameterizedTest
    @CsvSource({
            "50.0",
            "10.0",
            "90.0",
            "100.0",
            "-0.0"})
    void testRelativHumidity(double relativeHumidity) throws IOException {
        final Humidity testee = Humidity.createFromRelative(relativeHumidity);

        final double result = testee.getRelativeHumidity();

        assertThat(result, is(relativeHumidity));
    }

    @ParameterizedTest
    @CsvSource({
            "100.1, false",
            "-0.1"})
    void testEdgeCases(double relativeHumidity) {
        assertThrows(IOException.class, () -> Humidity.createFromRelative(relativeHumidity));
    }

    @ParameterizedTest
    @CsvSource({
            "-20.0, 1.075",
            "-15.0, 1.6066",
            "-10.0, 2.3593",
            "-5.0, 3.4085",
            "0.0, 4.8485",
            "5.0, 6.797",
            "10.0, 9.398",
            "15.0, 12.826",
            "20.0, 17.29",
            "25.0, 23.037",
            "30.0, 30.355",
            "35.0, 39.58",
            "40.0, 51.1",
            "45.0, 65.35",})
    void testCalculationOfAbsoluteHumidity100Percent(double temperatureCelsius, double weightFor100Percent) throws IOException {
        final double relativeHumidity = 100.0;
        final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
        final Humidity testee = Humidity.createFromRelative(relativeHumidity);

        final double result = testee.getAbsoluteHumidity(temperature);

        final double expectedMaxDeltaInPercent = 1.0;
        final double expectedMaxDelta = expectedMaxDeltaInPercent * weightFor100Percent / 100.0;
        assertEquals(weightFor100Percent, result, expectedMaxDelta);
    }

    @ParameterizedTest
    @CsvSource({
            "-20.0, 1.075",
            "-15.0, 1.6066",
            "-10.0, 2.3593",
            "-5.0, 3.4085",
            "0.0, 4.8485",
            "5.0, 6.797",
            "10.0, 9.398",
            "15.0, 12.826",
            "20.0, 17.29",
            "25.0, 23.037",
            "30.0, 30.355",
            "35.0, 39.58",
            "40.0, 51.1",
            "45.0, 65.35",})
    void testCalculationOfAbsoluteHumidity50Percent(double temperatureCelsius, double weightFor100Percent) throws IOException {
        final double relativeHumidity = 50.0;
        final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
        final Humidity testee = Humidity.createFromRelative(relativeHumidity);

        final double result = testee.getAbsoluteHumidity(temperature);

        final double expectedMaxDeltaInPercent = 1.0;
        final double expectedMaxDelta = expectedMaxDeltaInPercent * weightFor100Percent / 100.0;
        assertEquals(weightFor100Percent / 2.0, result, expectedMaxDelta);
    }

    @Test
    void calculationAbsoluteHumidity() throws IOException {
        final double relativeHumidity = 63.0;
        final Temperature temperature = Temperature.createFromCelsius(9.5);
        final Humidity testee = Humidity.createFromRelative(relativeHumidity);

        final double result = testee.getAbsoluteHumidity(temperature);

        System.out.println(result);
    }
}