package org.airController.persistence;

import org.airController.sensorValues.*;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class SensorDataCsvTest {

    @Test
    void shouldWriteSensorDataIntoCsvFiles_whenPersist() throws InvalidArgumentException {
        final Random random = new Random();
        final double celsiusTemperature = random.nextDouble() * 100;
        final double relativeHumidity = random.nextDouble() * 100;
        final double co2Ppm = random.nextDouble() * 100000;
        final LocalDateTime time = LocalDateTime.of(2024, 9, 27, 20, 51, 12);
        final SensorData inputSensorData = createSensorData(celsiusTemperature, relativeHumidity, co2Ppm, time);
        final String csvFilePath = "log/sensorValueCsvWriterTest.csv";
        final SensorDataPersistence testee = new SensorDataCsv(csvFilePath);
        final int initialSize = testee.read().size();

        testee.persist(inputSensorData);
        final List<SensorData> sensorData = testee.read();

        assertThat(sensorData).size().isGreaterThan(initialSize);
    }

    @Test
    void shouldReadMostCurrentSensorData() throws InvalidArgumentException {
        final Random random = new Random();
        final double celsiusTemperature = random.nextDouble() * 100;
        final double relativeHumidity = random.nextDouble() * 100;
        final double co2Ppm = random.nextDouble() * 100000;
        final LocalDateTime time = LocalDateTime.of(2024, 9, 27, 20, 51, 12);
        final SensorData inputSensorData = createSensorData(celsiusTemperature, relativeHumidity, co2Ppm, time);
        final String csvFilePath = "log/sensorValueCsvWriterTest.csv";
        final SensorDataPersistence testee = new SensorDataCsv(csvFilePath);

        testee.persist(inputSensorData);
        final Optional<SensorData> lastSensorData = testee.getMostCurrentSensorData(time.minusMinutes(10));

        assertThat(lastSensorData).hasValueSatisfying(sensorData -> {
            assertThat(sensorData.getTemperature()).isPresent().hasValueSatisfying(
                    temperature -> assertThat(temperature.getCelsius()).isCloseTo(temperature.getCelsius(), Offset.offset(0.01)));
            assertThat(sensorData.getHumidity()).isPresent().hasValueSatisfying(
                    humidity -> assertThat(humidity.getRelativeHumidity(sensorData.getTemperature().get())).isCloseTo(relativeHumidity,
                            Offset.offset(0.01)));
            assertThat(sensorData.getCo2()).isPresent().hasValueSatisfying(
                    co2 -> assertThat(co2.getPpm()).isCloseTo(co2Ppm, Offset.offset(1.0)));
            assertThat(sensorData.getTimeStamp()).isCloseTo(time, within(1, ChronoUnit.SECONDS));
        });
    }

    private SensorData createSensorData(double celsiusTemperatur, double relativeHumidity, double co2Ppm, LocalDateTime time)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(celsiusTemperatur);
        final Humidity humidity = Humidity.createFromRelative(relativeHumidity, temperature);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(co2Ppm);
        return new SensorDataImpl(temperature, humidity, co2, time);
    }
}