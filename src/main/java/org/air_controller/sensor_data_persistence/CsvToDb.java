package org.air_controller.sensor_data_persistence;

import lombok.RequiredArgsConstructor;
import org.air_controller.persistence.DatabaseConnection;
import org.air_controller.sensor_values.ClimateDataPoint;

import java.util.List;

@RequiredArgsConstructor
public class CsvToDb {

    private final DatabaseConnection database;

    public void persistToDbFromCsv(String csvFilePath, String tableName) {
        final ClimateDataPointPersistence sensorDataCsv = new ClimateDataPointsCsv(csvFilePath);
        final ClimateDataPointPersistence sensorDataDb = new ClimateDataPointsDb(database, tableName);
        final List<ClimateDataPoint> dataPoints = sensorDataCsv.read();
        dataPoints.forEach(sensorDataDb::persist);
    }
}
