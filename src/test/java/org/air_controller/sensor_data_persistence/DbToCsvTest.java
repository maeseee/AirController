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

class DbToCsvTest {
    private final String sensorDataTableName = "DbToCsvTable";

    @Test
    void shouldWriteAllDataFromDbToCsvFile() throws InvalidArgumentException {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        prepareDB(now);
        final String sensorCsvPath = "log/sensorDataFromDbTest.csv";
        final DbToCsv testee = new DbToCsv(new LocalInMemoryDatabase());

        testee.persistToCsvFromDb(sensorDataTableName, sensorCsvPath);

        final SensorDataCsv sensorDataCsv = new SensorDataCsv(sensorCsvPath);
        final List<SensorData> sensorDataList = sensorDataCsv.read();
        final SensorData lastSensorData = sensorDataList.getLast();
        assertThat(lastSensorData.getTemperature()).isPresent().hasValueSatisfying(
                temperature -> assertThat(temperature.celsius()).isCloseTo(21.0, within(0.001)));
        assertThat(lastSensorData.getHumidity()).isPresent().hasValueSatisfying(
                humidity -> assertThat(humidity.absoluteHumidity()).isCloseTo(10.0, within(0.001)));
        assertThat(lastSensorData.getCo2()).isPresent().hasValueSatisfying(
                co2 -> assertThat(co2.ppm()).isCloseTo(500.0, within(0.001)));
        assertThat(lastSensorData.getTimeStamp()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    private void prepareDB(ZonedDateTime now) throws InvalidArgumentException {
        final SensorDataDb sensorDataDb = new SensorDataDb(new LocalInMemoryDatabase(), sensorDataTableName);
        sensorDataDb.resetDB();
        final SensorData sensorData = new SensorDataImpl(21.0, 10.0, 500.0, now);
        sensorDataDb.persist(sensorData);
    }
}