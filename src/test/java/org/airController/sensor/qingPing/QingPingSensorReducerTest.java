package org.airController.sensor.qingPing;

import org.airController.sensorValues.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QingPingSensorReducerTest {

    @ParameterizedTest(
            name = "{index} => temperature1 ={0}, humidity1={1}, co2_1={2}, minutesYounger={3}, expectedTemperature={4}, expectedHumidity={5}, " +
                    "expectedCo2={6}")
    @CsvSource({
            "20, 10, NaN, 0, 30, 12.5, NaN",
            "20, 10, 500, 0, 30, 12.5, 500",
            "40, 15, 500.0, 0, 40, 15.0, 500",
            "20, 10, NaN, 30, 30, 12.5, NaN",
            "20, 10, NaN, 59, 30, 12.5, NaN",
            "20, 10, NaN, 60, 40, 15.0, NaN", // Invalid after SENSOR_INVALIDATION_TIME
    })
    void shouldTakeAverageOfSensorValues_whenMultipleSensors(double temperature1, double humidity1, double co2_1, int minutesYounger,
            double expectedTemperature, double expectedHumidity, double expectedCo2) throws InvalidArgumentException, CalculationException {
        final LocalDateTime now = LocalDateTime.now();
        final QingPingSensorData sensorData1 = createSensorData(temperature1, humidity1, co2_1, minutesYounger, now);
        final QingPingSensorData sensorData2 = createSensorData(40.0, 15.0, Double.NaN, 0, now);
        final List<QingPingSensorData> sensorData = List.of(sensorData1, sensorData2);
        final QingPingSensorReducer testee = new QingPingSensorReducer();

        final SensorData result = testee.reduce(sensorData);

        final SensorData expectedSensorData = createSensorData(expectedTemperature, expectedHumidity, expectedCo2, 0, now);
        assertThat(result.getTemperature()).isEqualTo(expectedSensorData.getTemperature());
        assertThat(result.getHumidity()).isEqualTo(expectedSensorData.getHumidity());
        assertThat(result.getCo2()).isEqualTo(expectedSensorData.getCo2());
        assertThat(result.getTimeStamp()).isEqualTo(expectedSensorData.getTimeStamp());
    }

    private static QingPingSensorData createSensorData(double temperature, double humidity, double co2, int minutesYounger, LocalDateTime now)
            throws InvalidArgumentException {
        final Temperature temp = Temperature.createFromCelsius(temperature);
        final Humidity hum = Humidity.createFromAbsolute(humidity);
        final CarbonDioxide carbonDioxide = Double.isNaN(co2) ? null : CarbonDioxide.createFromPpm(co2);
        final LocalDateTime timestamp = now.minusMinutes(minutesYounger);
        return new QingPingSensorData(temp, hum, carbonDioxide, timestamp);
    }
}