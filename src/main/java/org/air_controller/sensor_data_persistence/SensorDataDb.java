package org.air_controller.sensor_data_persistence;

import org.air_controller.persistence.DatabaseConnection;
import org.air_controller.persistence.EntryAdder;
import org.air_controller.persistence.PreparedStatementSetter;
import org.air_controller.sensor_values.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class SensorDataDb implements SensorDataPersistence {
    private static final Logger logger = LogManager.getLogger(SensorDataDb.class);

    private final String sensorDataTableName;
    private final DatabaseConnection database;

    public SensorDataDb(DatabaseConnection database, String sensorDataTableName) {
        this.database = database;
        this.sensorDataTableName = sensorDataTableName;
        createTableIfNotExists();
    }

    @Override
    public void persist(ClimateDataPoint dataPoint) {
        final Double temperature = dataPoint.temperature().celsius();
        final Double humidity = dataPoint.humidity().absoluteHumidity();
        final Double co2 = dataPoint.co2().map(CarbonDioxide::ppm).orElse(null);
        insertSensorData(temperature, humidity, co2, dataPoint.timestamp());
    }

    @Override
    public List<ClimateDataPoint> read() {
        final String sql = "SELECT * FROM " + sensorDataTableName + ";";
        final EntryAdder<ClimateDataPoint> adder = this::addResultIfAvailable;
        return database.executeQuery(sql, adder);
    }

    @Override
    public Optional<ClimateDataPoint> getMostCurrentClimateDataPoint(ZonedDateTime lastValidTimestamp) {
        final String sql =
                "SELECT * FROM " + sensorDataTableName + " i " +
                        "WHERE i.EVENT_TIME > ? " +
                        "ORDER BY i.EVENT_TIME DESC " +
                        "LIMIT 1;";
        final PreparedStatementSetter setter =
                preparedStatement -> preparedStatement.setTimestamp(1, Timestamp.valueOf(lastValidTimestamp.toLocalDateTime()));
        final EntryAdder<ClimateDataPoint> adder = this::addResultIfAvailable;
        final List<ClimateDataPoint> dataPoints = database.executeQuery(sql, adder, setter);
        return dataPoints.stream().findFirst();
    }

    private ClimateDataPoint createSensorData(ResultSet resultSet) throws SQLException, InvalidArgumentException {
        // Read the object as it can handle null
        return new SensorDataBuilder()
                .setTemperatureCelsius(resultSet.getObject("temperature", Double.class))
                .setHumidityAbsolute(resultSet.getObject("humidity", Double.class))
                .setCo2(resultSet.getObject("co2", Double.class))
                .setTime(ZonedDateTime.of(resultSet.getObject("event_time", LocalDateTime.class), ZoneOffset.UTC))
                .build();
    }

    private void createTableIfNotExists() {
        final String sql =
                "CREATE TABLE IF NOT EXISTS " + sensorDataTableName + " (\n" +
                        "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                        "temperature DOUBLE,\n" +
                        "humidity DOUBLE,\n" +
                        "co2 DOUBLE,\n" +
                        "event_time TIMESTAMP);";
        database.executeUpdate(sql);
    }

    private void insertSensorData(Double temperature, Double humidity, Double co2, ZonedDateTime timestamp) {
        final String sql = "INSERT INTO " + sensorDataTableName + " (temperature, humidity, co2, event_time) VALUES (?, ?, ?, ?)";
        final PreparedStatementSetter setter = preparedStatement -> {
            preparedStatement.setObject(1, temperature);
            preparedStatement.setObject(2, humidity);
            preparedStatement.setObject(3, co2);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(timestamp.toLocalDateTime()));
        };
        database.executeUpdate(sql, setter);
    }

    private void addResultIfAvailable(List<ClimateDataPoint> entries, ResultSet resultSet) {
        try {
            entries.add(createSensorData(resultSet));
        } catch (InvalidArgumentException | SQLException e) {
            logger.error("Next entry could not be loaded! {}", e.getMessage());
        }
    }
}
