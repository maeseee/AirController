package org.air_controller.sensor_data_persistence;

import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.DataPointBuilder;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class DbToCsvTest {
    private final String dataPointTableName = "DbToCsvTable";

    @Test
    void shouldWriteAllDataFromDbToCsvFile() throws InvalidArgumentException {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        prepareDB(now);
        final String sensorCsvPath = "log/dataPointFromDbTest.csv";
        final DbToCsv testee = new DbToCsv(new LocalInMemoryDatabase());

        testee.persistToCsvFromDb(dataPointTableName, sensorCsvPath);

        final ClimateDataPointsCsv dataPointsCsv = new ClimateDataPointsCsv(sensorCsvPath);
        final List<ClimateDataPoint> dataPoints = dataPointsCsv.read();
        final ClimateDataPoint lastDataPoint = dataPoints.getLast();
        assertThat(lastDataPoint.temperature().celsius()).isCloseTo(21.0, within(0.001));
        assertThat(lastDataPoint.humidity().absoluteHumidity()).isCloseTo(10.0, within(0.001));
        assertThat(lastDataPoint.co2()).isPresent().hasValueSatisfying(
                co2 -> assertThat(co2.ppm()).isCloseTo(500.0, within(0.001)));
        assertThat(lastDataPoint.timestamp()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    private void prepareDB(ZonedDateTime now) throws InvalidArgumentException {
        final ClimateDataPointsDb dataPointsDb = new ClimateDataPointsDb(new LocalInMemoryDatabase(), dataPointTableName);
        final ClimateDataPoint dataPoint = new DataPointBuilder()
                .setTemperatureCelsius(21.0)
                .setHumidityAbsolute(10.0)
                .setCo2(500.0)
                .setTime(now)
                .build();
        dataPointsDb.persist(dataPoint);
    }
}