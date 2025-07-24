package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.Temperature;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HwSensorDataTest {

    @ParameterizedTest
    @CsvSource({
            "23, 50, 500, 0, true",
            "24, 50, 500, 0, false",
            "23, 60, 500, 0, false",
            "23, 50, 600, 0, false",
            "23, 50, 500, 1, false",
    })
    void shouldBeDifferent(double comparedTemperature, double comparedHumidity, int comparedCo2, int timeOffset, boolean isEquals)
            throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now();
        final HwSensorData testee = createHwSensorData(23.0, 50.0, 500, timestamp);
        final HwSensorData compareObject = createHwSensorData(comparedTemperature, comparedHumidity, comparedCo2, timestamp.plusSeconds(timeOffset));

        final boolean equals = testee.equals(compareObject);

        assertThat(equals).isEqualTo(isEquals);
    }

    private HwSensorData createHwSensorData(double temperatureValue, double humidityValue, int co2Value, ZonedDateTime timestamp)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(temperatureValue);
        final Humidity humidity = Humidity.createFromRelative(humidityValue, temperature);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(co2Value);
        return new HwSensorData(temperature, humidity, co2, timestamp);
    }

}