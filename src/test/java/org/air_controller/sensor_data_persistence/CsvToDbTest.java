package org.air_controller.sensor_data_persistence;

import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.SensorData;
import org.air_controller.sensor_values.SensorDataImpl;
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
        assertThat(lastSensorData.getTemperature()).isPresent().hasValueSatisfying(
                temperature -> assertThat(temperature.getCelsius()).isCloseTo(21.0, within(0.001)));
        assertThat(lastSensorData.getHumidity()).isPresent().hasValueSatisfying(
                humidity -> assertThat(humidity.getAbsoluteHumidity()).isCloseTo(10.0, within(0.001)));
        assertThat(lastSensorData.getCo2()).isPresent().hasValueSatisfying(
                co2 -> assertThat(co2.getPpm()).isCloseTo(500.0, within(0.001)));
        assertThat(lastSensorData.getTimeStamp()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    private void persistDataToCsv(ZonedDateTime now) throws InvalidArgumentException {
        final SensorData sensorData = new SensorDataImpl(21.0, 10.0, 500.0, now);
        final SensorDataCsv sensorDataCsv = new SensorDataCsv(sensorDataCsvPath);
        sensorDataCsv.persist(sensorData);
    }
}