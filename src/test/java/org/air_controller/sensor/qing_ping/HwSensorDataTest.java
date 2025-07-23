package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.Temperature;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HwSensorDataTest {

    @Test
    void shouldBeEquals_whenAllElementsAreEqual() throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now();
        final HwSensorData testee1 = createHwSensorData(23.0, 50.0, 500, timestamp);
        final HwSensorData testee2 = createHwSensorData(23.0, 50.0, 500, timestamp);

        final boolean equals = testee1.equals(testee2);

        assertTrue(equals);
    }

    @Test
    void shouldBeDifferent_whenTemperatureDifferent() throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now();
        final HwSensorData testee1 = createHwSensorData(23.0, 50.0, 500, timestamp);
        final HwSensorData testee2 = createHwSensorData(24.0, 50.0, 500, timestamp);

        final boolean equals = testee1.equals(testee2);

        assertFalse(equals);
    }

    @Test
    void shouldBeDifferent_whenHumidityDifferent() throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now();
        final HwSensorData testee1 = createHwSensorData(23.0, 50.0, 500, timestamp);
        final HwSensorData testee2 = createHwSensorData(23.0, 60.0, 500, timestamp);

        final boolean equals = testee1.equals(testee2);

        assertFalse(equals);
    }

    @Test
    void shouldBeDifferent_whenCo2Different() throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now();
        final HwSensorData testee1 = createHwSensorData(23.0, 50.0, 500, timestamp);
        final HwSensorData testee2 = createHwSensorData(23.0, 50.0, 600, timestamp);

        final boolean equals = testee1.equals(testee2);

        assertFalse(equals);
    }

    private HwSensorData createHwSensorData(double temperatureValue, double humidityValue, int co2Value, ZonedDateTime timestamp)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(temperatureValue);
        final Humidity humidity = Humidity.createFromRelative(humidityValue, temperature);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(co2Value);
        return new HwSensorData(temperature, humidity, co2, timestamp);
    }

}