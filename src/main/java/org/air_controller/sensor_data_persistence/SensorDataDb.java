package org.air_controller.sensor_data_persistence;

import com.google.common.annotations.VisibleForTesting;
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
        try {
            createTableIfNotExists();
        } catch (SQLException e) {
            logger.error(e);
            throw new ParseException("SQL Exception on creating connection! " + e.getMessage(), e.getCause());
        }
    }

    @Override
    public void persist(SensorData sensorData) {
        final Double temperature = sensorData.getTemperature().map(Temperature::getCelsius).orElse(null);
        final Double humidity = sensorData.getHumidity().map(Humidity::getAbsoluteHumidity).orElse(null);
        final Double co2 = sensorData.getCo2().map(CarbonDioxide::getPpm).orElse(null);
        try {
            insertSensorData(temperature, humidity, co2, sensorData.getTimeStamp());
        } catch (SQLException e) {
            logger.error("SQL Exception! {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unknown error! {}", e.getMessage());
        }
    }

    @Override
    public List<SensorData> read() {
        final String sql = "SELECT * FROM " + sensorDataTableName + ";";
        final PreparedStatementSetter setter = preparedStatement -> {
        };
        final EntryAdder<SensorData> adder = this::addResultIfAvailable;
        return database.executeQuery(sql, setter, adder);
    }

    @Override
    public Optional<SensorData> getMostCurrentSensorData(ZonedDateTime lastValidTimestamp) {
        final String sql =
                "SELECT * FROM " + sensorDataTableName + " i " +
                        "WHERE i.EVENT_TIME > ? " +
                        "ORDER BY i.EVENT_TIME DESC " +
                        "LIMIT 1;";
        final PreparedStatementSetter setter = preparedStatement -> {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(lastValidTimestamp.toLocalDateTime()));
        };
        final EntryAdder<SensorData> adder = this::addResultIfAvailable;
        final List<SensorData> sensorData = database.executeQuery(sql, setter, adder);
        return sensorData.stream().findFirst();
    }

    @VisibleForTesting
    void resetDB() {
        final String sql = "DELETE FROM " + sensorDataTableName + ";";
        database.execute(sql);
    }

    private SensorData createSensorData(ResultSet resultSet) throws SQLException, InvalidArgumentException {
        // Read the object as it can handle null
        final Double temp = resultSet.getObject("temperature", Double.class);
        final Double hum = resultSet.getObject("humidity", Double.class);
        final Double carbonDioxide = resultSet.getObject("co2", Double.class);
        final ZonedDateTime timestamp = ZonedDateTime.of(resultSet.getObject("event_time", LocalDateTime.class), ZoneOffset.UTC);
        return new SensorDataImpl(temp, hum, carbonDioxide, timestamp);
    }

    private void createTableIfNotExists() throws SQLException {
        final String sql =
                "CREATE TABLE IF NOT EXISTS " + sensorDataTableName + " (\n" +
                        "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                        "temperature DOUBLE,\n" +
                        "humidity DOUBLE,\n" +
                        "co2 DOUBLE,\n" +
                        "event_time TIMESTAMP);";
        database.execute(sql);
    }

    private void insertSensorData(Double temperature, Double humidity, Double co2, ZonedDateTime timestamp) throws SQLException {
        final String sql = "INSERT INTO " + sensorDataTableName + " (temperature, humidity, co2, event_time) VALUES (?, ?, ?, ?)";
        final PreparedStatementSetter setter = preparedStatement -> {
            preparedStatement.setObject(1, temperature);
            preparedStatement.setObject(2, humidity);
            preparedStatement.setObject(3, co2);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(timestamp.toLocalDateTime()));
        };
        database.executeUpdate(sql, setter);
    }

    private void addResultIfAvailable(List<SensorData> entries, ResultSet resultSet) {
        try {
            entries.add(createSensorData(resultSet));
        } catch (InvalidArgumentException | SQLException e) {
            logger.error("Next entry could not be loaded! {}", e.getMessage());
        }
    }
}
