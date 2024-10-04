package org.airController.persistence;

import org.airController.sensorValues.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SensorDataCsvWriterTest {

    private final String FILE_PATH = "log/sensorValueCsvWriterTest.csv";

    @Test
    void shouldWriteSensorDataIntoCsvFiles_whenPersist() throws InvalidArgumentException, IOException {
        final Random random = new Random();
        final double temperatureValue = random.nextDouble() * 100;
        final double humidityValue = random.nextDouble() * 100;
        final double co2Value = random.nextDouble() * 100000;
        final LocalDateTime time = LocalDateTime.of(2024, 9, 27, 20, 51, 12);
        final SensorData sensorData = createSensorData(temperatureValue, humidityValue, co2Value, time);
        final SensorDataPersistence testee = new SensorDataCsvWriter(FILE_PATH);

        testee.persist(sensorData);

        assertCsvFile(time, temperatureValue, humidityValue, co2Value);
    }

    private SensorData createSensorData(double temperatureValue, double humidityValue, double co2Value, LocalDateTime time)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(temperatureValue);
        final Humidity humidity = Humidity.createFromRelative(humidityValue, temperature);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(co2Value);
        return new SensorData() {
            @Override public Optional<Temperature> getTemperature() {
                return Optional.of(temperature);
            }

            @Override public Optional<Humidity> getHumidity() {
                return Optional.of(humidity);
            }

            @Override public Optional<CarbonDioxide> getCo2() {
                return Optional.of(co2);
            }

            @Override public LocalDateTime getTimeStamp() {
                return time;
            }
        };
    }

    private void assertCsvFile(LocalDateTime time, double temperature, double humidity, double co2) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        String lastLine = null;
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            lastLine = currentLine;
        }

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        assertNotNull(lastLine);
        final String[] csv = lastLine.split(",");
        assertEquals(4, csv.length);
        final LocalDateTime csvTime = LocalDateTime.parse(csv[0], formatter);
        assertEquals(time, csvTime);
        assertEquals(temperature, Double.parseDouble(csv[1]), 0.02);
        assertEquals(humidity, Double.parseDouble(csv[2]), 0.02);
        assertEquals(co2, Double.parseDouble(csv[3]), 0.02);
    }

}