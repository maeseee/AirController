package org.airController.persistence;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SensorValueCsvWriterTest {

    private final String FILE_PATH = "log/sensorValueCsvWriterTest.csv";

    @Test
    void test() throws IOException {

        final LocalDateTime now = LocalDateTime.now();
        final Random random = new Random();
        final double temperature = random.nextDouble() * 100;
        final double humidity = random.nextDouble() * 100;
        final AirValue airValue = new AirValue(Temperature.createFromCelsius(temperature), Humidity.createFromRelative(humidity));
        final SensorValuePersistence testee = new SensorValueCsvWriter(FILE_PATH);

        testee.persist(now, airValue);

        final LocalDateTime time =
                LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        assertCsvFile(time, temperature, humidity);
    }

    private void assertCsvFile(LocalDateTime time, double temperature, double humidity) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        String lastLine = null;
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            lastLine = currentLine;
        }

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        assertNotNull(lastLine);
        final String[] csv = lastLine.split(",");
        assertEquals(3, csv.length);
        final LocalDateTime csvTime = LocalDateTime.parse(csv[0], formatter);
        assertEquals(time, csvTime);
        assertEquals(temperature, Double.parseDouble(csv[1]), 0.02);
        assertEquals(humidity, Double.parseDouble(csv[2]), 0.02);
    }

}