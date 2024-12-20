package org.airController.sensorDataPersistence;

import org.airController.sensorValues.InvalidArgumentException;
import org.airController.sensorValues.SensorData;
import org.airController.sensorValues.SensorDataImpl;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Disabled;
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
    @Disabled
    void shouldWriteAllDataFromDbToCsvFile() throws InvalidArgumentException {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        persistDataToDb(now);
        final String sensorCsvPath = "log/sensorDataFromDbTest.csv";
        final DbToCsv testee = new DbToCsv();

        testee.persistToCsvFromDb(sensorDataTableName, sensorCsvPath);

        final SensorDataCsv sensorDataCsv = new SensorDataCsv(sensorCsvPath);
        final List<SensorData> sensorDataList = sensorDataCsv.read();
        final SensorData lastSensorData = sensorDataList.get(sensorDataList.size() - 1);
        assertThat(lastSensorData.getTemperature()).isPresent().hasValueSatisfying(
                temperature -> assertThat(temperature.getCelsius()).isCloseTo(21.0, Offset.offset(0.001)));
        assertThat(lastSensorData.getHumidity()).isPresent().hasValueSatisfying(
                humidity -> assertThat(humidity.getAbsoluteHumidity()).isCloseTo(10.0, Offset.offset(0.001)));
        assertThat(lastSensorData.getCo2()).isPresent().hasValueSatisfying(
                co2 -> assertThat(co2.getPpm()).isCloseTo(500.0, Offset.offset(0.001)));
        assertThat(lastSensorData.getTimeStamp()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    private void persistDataToDb(ZonedDateTime now) throws InvalidArgumentException {
        final SensorData sensorData = new SensorDataImpl(21.0, 10.0, 500.0, now);
        final SensorDataDb sensorDataDb = new SensorDataDb(sensorDataTableName);
        sensorDataDb.persist(sensorData);
    }
}