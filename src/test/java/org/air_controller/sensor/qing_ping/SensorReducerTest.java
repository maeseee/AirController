package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.*;
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
        final SensorData sensorData1 = createSensorData(temperature1, humidity1, carbonDioxide1, minutesYounger, now);
        final SensorData sensorData2 = createSensorData(40.0, 15.0, Double.NaN, 0, now);
        final List<SensorData> sensorData = List.of(sensorData1, sensorData2);
        final SensorReducer testee = new SensorReducer();

        final SensorData result = testee.reduce(sensorData);

        final SensorData expectedSensorData = createSensorData(expectedTemperature, expectedHumidity, expectedCo2, 0, now);
        assertThat(result).isEqualTo(expectedSensorData);
    }

    private SensorData createSensorData(double temperature, double humidity, double co2, int minutesYounger, ZonedDateTime now)
            throws InvalidArgumentException {
        final Temperature temp = Temperature.createFromCelsius(temperature);
        final Humidity hum = Humidity.createFromAbsolute(humidity);
        final CarbonDioxide carbonDioxide = Double.isNaN(co2) ? null : CarbonDioxide.createFromPpm(co2);
        final ZonedDateTime timestamp = now.minusMinutes(minutesYounger);
        return new SensorData(temp, hum, Optional.ofNullable(carbonDioxide), timestamp);
    }
}