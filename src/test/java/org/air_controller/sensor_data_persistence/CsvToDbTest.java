package org.air_controller.sensor_data_persistence;

import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CsvToDbTest {
    private final String dataPointCsvPath = "log/dataPointFromDbTest.csv";

    @Test
    void shouldWriteAllDataFromDbToCsvFile() throws InvalidArgumentException {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        persistDataToCsv(now);
        final String sensorTableName = "DbToCsvTable";
        final CsvToDb testee = new CsvToDb(new LocalInMemoryDatabase());

        testee.persistToDbFromCsv(dataPointCsvPath, sensorTableName);

        final ClimateDataPointsDbAccessor dbAccessor = new ClimateDataPointsDbAccessor(new LocalInMemoryDatabase(), sensorTableName);
        final List<ClimateDataPoint> dataPoints = dbAccessor.read();
        final ClimateDataPoint lastClimateDataPoint = dataPoints.getLast();
        assertThat(lastClimateDataPoint.temperature().celsius()).isCloseTo(21.0, within(0.001));
        assertThat(lastClimateDataPoint.humidity().absoluteHumidity()).isCloseTo(10.0, within(0.001));
        assertThat(lastClimateDataPoint.co2()).isPresent().hasValueSatisfying(
                co2 -> assertThat(co2.ppm()).isCloseTo(500.0, within(0.001)));
        assertThat(lastClimateDataPoint.timestamp()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    private void persistDataToCsv(ZonedDateTime now) throws InvalidArgumentException {
        final ClimateDataPoint dataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(21.0)
                .setHumidityAbsolute(10.0)
                .setCo2(500.0)
                .setTime(now)
                .build();
        final ClimateDataPointsCsv dataPointsCsv = new ClimateDataPointsCsv(dataPointCsvPath);
        dataPointsCsv.persist(dataPoint);
    }
}