package org.air_controller.sensor_data_persistence;

import lombok.RequiredArgsConstructor;
import org.air_controller.persistence.DatabaseConnection;
import org.air_controller.sensor_values.ClimateDataPoint;

import java.util.List;

@RequiredArgsConstructor
public class CsvToDb {

    private final DatabaseConnection database;

    public void persistToDbFromCsv(String csvFilePath, String tableName) {
        final ClimateDataPointPersistence dataPointsCsv = new ClimateDataPointsCsv(csvFilePath);
        final ClimateDataPointPersistence dataPointsDb = new ClimateDataPointsDbAccessor(database, tableName);
        final List<ClimateDataPoint> dataPoints = dataPointsCsv.read();
        dataPoints.forEach(dataPointsDb::persist);
    }
}
