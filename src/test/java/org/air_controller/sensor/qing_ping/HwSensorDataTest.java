package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

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
    void shouldBeDifferent(double comparedTemperature, double comparedHumidity, double comparedCo2, int timeOffset, boolean isEquals)
            throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        final SensorData sensorData1 = SensorData.createFromPrimitivesWithRelativHumidity(23.0, 50.0, 500.0, timestamp);
        final SensorData sensorData2 = createSensorData(comparedTemperature, comparedHumidity, comparedCo2, timestamp.plusSeconds(timeOffset));

        if (isEquals) {
            assertThat(sensorData1).isEqualTo(sensorData2);
        } else {
            assertThat(sensorData1).isNotEqualTo(sensorData2);
        }
    }

    private SensorData createSensorData(double temperatureValue, double humidityValue, double co2Value, ZonedDateTime timestamp)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(temperatureValue);
        final Humidity humidity = Humidity.createFromRelative(humidityValue, temperature);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(co2Value);
        return new SensorData(temperature, humidity, Optional.of(co2), timestamp);
    }

}