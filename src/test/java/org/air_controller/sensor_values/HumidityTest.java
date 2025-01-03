package org.air_controller.sensor_values;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HumidityTest {

    @ParameterizedTest
    @CsvSource({
            "50.0",
            "10.0",
            "90.0",
            "100.0",
            "-0.0"})
    void testRelativHumidity(double relativeHumidity) throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(25.0);
        final Humidity testee = Humidity.createFromRelative(relativeHumidity, temperature);

        final double result = testee.getRelativeHumidity(temperature);

        assertThat(result).isCloseTo(relativeHumidity, Offset.offset(0.001));
    }

    @ParameterizedTest
    @CsvSource({
            "100.1, false",
            "-0.1"})
    void testRelativeEdgeCases(double relativeHumidity) throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(25.0);
        assertThrows(InvalidArgumentException.class, () -> Humidity.createFromRelative(relativeHumidity, temperature));
    }

    @Test
    void testAbsoluteEdgeCases() {
        assertThrows(InvalidArgumentException.class, () -> Humidity.createFromAbsolute(-1.0));
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
    void testCalculationOfRelativeHumidity100Percent(double temperatureCelsius, double weightFor100Percent) throws InvalidArgumentException {
        final double relativeHumidity = 100.0;
        final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
        final Humidity testee = Humidity.createFromRelative(relativeHumidity, temperature);

        final double absoluteHumidity = testee.getAbsoluteHumidity();

        final double expectedMaxDeltaInPercent = 1.0;
        final double expectedMaxDelta = expectedMaxDeltaInPercent * weightFor100Percent / 100.0;
        assertThat(absoluteHumidity).isCloseTo(weightFor100Percent, Offset.offset(expectedMaxDelta));
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
    void testCalculationOfAbsoluteHumidity50Percent(double temperatureCelsius, double weightFor100Percent) throws InvalidArgumentException {
        final double relativeHumidity = 50.0;
        final Temperature temperature = Temperature.createFromCelsius(temperatureCelsius);
        final Humidity testee = Humidity.createFromRelative(relativeHumidity, temperature);

        final double absoluteHumidity = testee.getAbsoluteHumidity();

        final double expectedMaxDeltaInPercent = 1.0;
        final double expectedMaxDelta = expectedMaxDeltaInPercent * weightFor100Percent / 100.0;
        assertThat(absoluteHumidity).isCloseTo(weightFor100Percent / 2.0, Offset.offset(expectedMaxDelta));
    }

    @Test
    void calculationAbsoluteHumidity() throws InvalidArgumentException {
        final double relativeHumidity = 63.0;
        final Temperature temperature = Temperature.createFromCelsius(9.5);
        final Humidity testee = Humidity.createFromRelative(relativeHumidity, temperature);

        System.out.println(testee);
    }
}