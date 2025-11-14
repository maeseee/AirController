package org.air_controller.sensor.ping_ping_adapter;

import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SensorReducerTest {

    @ParameterizedTest(
            name = "{index} => temperature1 ={0}, humidity1={1}, carbonDioxide1={2}, minutesYounger={3}, expectedTemperature={4}, " +
                    "expectedHumidity={5}, " +
                    "expectedCo2={6}")
    @CsvSource({
            "20, 10, NaN, 0, 30, 12.5, NaN",
            "20, 10, 500, 0, 30, 12.5, 500",
            "40, 15, 500.0, 0, 40, 15.0, 500",
            "20, 10, NaN, 30, 30, 12.5, NaN",
            "20, 10, NaN, 59, 30, 12.5, NaN",
            "20, 10, NaN, 60, 40, 15.0, NaN", // Invalid after SENSOR_INVALIDATION_TIME
    })
    void shouldTakeAverageOfSensorValues_whenMultipleSensors(double temperature1, double humidity1, double carbonDioxide1, int minutesYounger,
            double expectedTemperature, double expectedHumidity, double expectedCo2) throws InvalidArgumentException, CalculationException {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ClimateDataPoint dataPoint1 = new ClimateDataPointBuilder()
                .setTemperatureCelsius(temperature1)
                .setHumidityAbsolute(humidity1)
                .setCo2(Double.isNaN(carbonDioxide1) ?  null : carbonDioxide1)
                .setTime(now.minusMinutes(minutesYounger))
                .build();
        final ClimateDataPoint dataPoint2 = new ClimateDataPointBuilder()
                .setTemperatureCelsius(40.0)
                .setHumidityAbsolute(15.0)
                .setTime(now)
                .build();
        final List<ClimateDataPoint> dataPoints = List.of(dataPoint1, dataPoint2);
        final SensorReducer testee = new SensorReducer();

        final Optional<ClimateDataPoint> result = testee.reduce(dataPoints);

        final ClimateDataPoint expectedClimateDataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(expectedTemperature)
                .setHumidityAbsolute(expectedHumidity)
                .setCo2(Double.isNaN(expectedCo2) ?  null : expectedCo2)
                .setTime(now)
                .build();
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedClimateDataPoint);
    }
}