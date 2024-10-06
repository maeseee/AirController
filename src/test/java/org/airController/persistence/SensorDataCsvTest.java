package org.airController.persistence;

import org.airController.sensorValues.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class SensorDataCsvTest {

    private final String FILE_PATH = "log/sensorValueCsvWriterTest.csv";

    @Test
    void shouldWriteSensorDataIntoCsvFiles_whenPersist() throws InvalidArgumentException, IOException {
        final Random random = new Random();
        final double celsiusTemperature = random.nextDouble() * 100;
        final double relativeHumidity = random.nextDouble() * 100;
        final double co2Ppm = random.nextDouble() * 100000;
        final LocalDateTime time = LocalDateTime.of(2024, 9, 27, 20, 51, 12);
        final SensorData inputSensorData = createSensorData(celsiusTemperature, relativeHumidity, co2Ppm, time);
        final SensorDataPersistence testee = new SensorDataCsv(FILE_PATH);

        testee.persist(inputSensorData);
        final List<SensorData> sensorDataList = testee.read();

        final SensorData lastSensorData = sensorDataList.get(sensorDataList.size() - 1);
        assertThat(lastSensorData.getTemperature()).isPresent()
                .hasValueSatisfying(temperature -> assertThat(temperature.getCelsius()).isEqualTo(celsiusTemperature));
        assertThat(lastSensorData.getHumidity()).isPresent()
                .hasValueSatisfying(
                        humidity -> assertThat(humidity.getRelativeHumidity(lastSensorData.getTemperature().get())).isEqualTo(relativeHumidity));
        assertThat(lastSensorData.getCo2()).isPresent()
                .hasValueSatisfying(co2 -> assertThat(co2.getPpm()).isEqualTo(co2Ppm));
        assertThat(lastSensorData.getTimeStamp()).isEqualTo(time);
    }

    private SensorData createSensorData(double celsiusTemperatur, double relativeHumidity, double co2Ppm, LocalDateTime time)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(celsiusTemperatur);
        final Humidity humidity = Humidity.createFromRelative(relativeHumidity, temperature);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(co2Ppm);
        return new SensorDataImpl(temperature, humidity, co2, time);
    }
}