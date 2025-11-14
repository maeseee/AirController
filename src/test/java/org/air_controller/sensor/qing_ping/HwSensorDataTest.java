package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZoneOffset;
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
    void shouldBeDifferent(double comparedTemperature, double comparedHumidity, double comparedCo2, int timeOffset, boolean isEquals)
            throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        final ClimateDataPoint dataPoint1 = new SensorDataBuilder()
                .setTemperatureCelsius(23.0)
                .setHumidityRelative(50.0)
                .setCo2(500.0)
                .setTime(timestamp)
                .build();
        final ClimateDataPoint dataPoint2 = new SensorDataBuilder()
                .setTemperatureCelsius(comparedTemperature)
                .setHumidityRelative(comparedHumidity)
                .setCo2(comparedCo2)
                .setTime(timestamp.plusSeconds(timeOffset))
                .build();

        if (isEquals) {
            assertThat(dataPoint1).isEqualTo(dataPoint2);
        } else {
            assertThat(dataPoint1).isNotEqualTo(dataPoint2);
        }
    }
}