package org.air_controller.sensor_data_persistence;

import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.SensorData;
import org.air_controller.sensor_values.SensorDataBuilder;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CsvToDbTest {
    private final String sensorDataCsvPath = "log/sensorDataFromDbTest.csv";

    @Test
    void shouldWriteAllDataFromDbToCsvFile() throws InvalidArgumentException {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        persistDataToCsv(now);
        final String sensorTableName = "DbToCsvTable";
        final CsvToDb testee = new CsvToDb(new LocalInMemoryDatabase());

        testee.persistToDbFromCsv(sensorDataCsvPath, sensorTableName);

        final SensorDataDb sensorDataDb = new SensorDataDb(new LocalInMemoryDatabase(), sensorTableName);
        final List<SensorData> sensorDataList = sensorDataDb.read();
        final SensorData lastSensorData = sensorDataList.getLast();
        assertThat(lastSensorData.temperature().celsius()).isCloseTo(21.0, within(0.001));
        assertThat(lastSensorData.humidity().absoluteHumidity()).isCloseTo(10.0, within(0.001));
        assertThat(lastSensorData.co2()).isPresent().hasValueSatisfying(
                co2 -> assertThat(co2.ppm()).isCloseTo(500.0, within(0.001)));
        assertThat(lastSensorData.timestamp()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    private void persistDataToCsv(ZonedDateTime now) throws InvalidArgumentException {
        final SensorData sensorData = new SensorDataBuilder()
                .setTemperature(21.0)
                .setAbsoluteHumidity(10.0)
                .setCo2(500.0)
                .setTime(now)
                .build();
        final SensorDataCsv sensorDataCsv = new SensorDataCsv(sensorDataCsvPath);
        sensorDataCsv.persist(sensorData);
    }
}