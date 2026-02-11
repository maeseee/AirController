package org.air_controller.sensor_data_persistence;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.persistence.DatabaseConnection;
import org.air_controller.persistence.EntryAdder;
import org.air_controller.persistence.PreparedStatementSetter;
import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ClimateDataPointsDbAccessor implements ClimateDataPointPersistence {
    private final String dataPointTableName;
    private final DatabaseConnection database;

    public ClimateDataPointsDbAccessor(DatabaseConnection database, String dataPointTableName) {
        this.database = database;
        this.dataPointTableName = dataPointTableName;
        createTableIfNotExists();
    }

    @Override
    public void persist(ClimateDataPoint dataPoint) {
        final Double temperature = dataPoint.temperature().celsius();
        final Double humidity = dataPoint.humidity().absoluteHumidity();
        final Double co2 = dataPoint.co2().map(CarbonDioxide::ppm).orElse(null);
        insertDataPoint(temperature, humidity, co2, dataPoint.timestamp());
    }

    @Override
    public List<ClimateDataPoint> read() {
        final String sql = "SELECT * FROM " + dataPointTableName + ";";
        final EntryAdder<ClimateDataPoint> adder = this::addResultIfAvailable;
        return database.executeQuery(sql, adder);
    }

    @Override
    public Optional<ClimateDataPoint> getMostCurrentClimateDataPoint(ZonedDateTime lastValidTimestamp) {
        final String sql =
                "SELECT * FROM " + dataPointTableName + " i " +
                        "WHERE i.EVENT_TIME > ? " +
                        "ORDER BY i.EVENT_TIME DESC " +
                        "LIMIT 1;";
        final PreparedStatementSetter setter =
                preparedStatement -> preparedStatement.setTimestamp(1, Timestamp.valueOf(lastValidTimestamp.toLocalDateTime()));
        final EntryAdder<ClimateDataPoint> adder = this::addResultIfAvailable;
        final List<ClimateDataPoint> dataPoints = database.executeQuery(sql, adder, setter);
        return dataPoints.stream().findFirst();
    }

    @Override
    // TODO unittest
    public List<ClimateDataPoint> getDataPointsFromLast24Hours() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime yesterday = now.minusHours(24);
        final String sql =
                "SELECT * FROM " + dataPointTableName + " i " +
                        "WHERE i.EVENT_TIME > ? " +
                        "ORDER BY i.EVENT_TIME ASC";
        final PreparedStatementSetter setter =
                preparedStatement -> preparedStatement.setTimestamp(1, Timestamp.valueOf(yesterday.toLocalDateTime()));
        final EntryAdder<ClimateDataPoint> adder = this::addResultIfAvailable;
        return database.executeQuery(sql, adder, setter);
    }

    private ClimateDataPoint createDataPoint(ResultSet resultSet) throws SQLException, InvalidArgumentException {
        // Read the object as it can handle null
        return new ClimateDataPointBuilder()
                .setTemperatureCelsius(resultSet.getObject("temperature", Double.class))
                .setHumidityAbsolute(resultSet.getObject("humidity", Double.class))
                .setCo2(resultSet.getObject("co2", Double.class))
                .setTime(ZonedDateTime.of(resultSet.getObject("event_time", LocalDateTime.class), ZoneOffset.UTC))
                .build();
    }

    private void createTableIfNotExists() {
        final String sql =
                "CREATE TABLE IF NOT EXISTS " + dataPointTableName + " (\n" +
                        "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                        "temperature DOUBLE,\n" +
                        "humidity DOUBLE,\n" +
                        "co2 DOUBLE,\n" +
                        "event_time TIMESTAMP);";
        database.executeUpdate(sql);
    }

    private void insertDataPoint(Double temperature, Double humidity, Double co2, ZonedDateTime timestamp) {
        final String sql = "INSERT INTO " + dataPointTableName + " (temperature, humidity, co2, event_time) VALUES (?, ?, ?, ?)";
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
            entries.add(createDataPoint(resultSet));
        } catch (InvalidArgumentException | SQLException e) {
            log.error("Next entry could not be loaded! {}", e.getMessage());
        }
    }
}
