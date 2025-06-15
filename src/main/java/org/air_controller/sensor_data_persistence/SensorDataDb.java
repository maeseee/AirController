package org.air_controller.sensor_data_persistence;

import com.google.common.annotations.VisibleForTesting;
import org.air_controller.persistence.Persistence;
import org.air_controller.sensor_values.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SensorDataDb implements SensorDataPersistence {
    private static final Logger logger = LogManager.getLogger(SensorDataDb.class);

    private final String sensorDataTableName;
    private final Connection connection;

    public SensorDataDb(String sensorDataTableName) {
        this.sensorDataTableName = sensorDataTableName;
        try {
            connection = Persistence.createConnection();
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
        final List<SensorData> entries = new ArrayList<>();
        final String sql = "SELECT * FROM " + sensorDataTableName + ";";
        try (Statement statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                addResultIfAvailable(entries, resultSet);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception on read ! {}", e.getMessage());
        }
        return entries;
    }

    @Override
    public Optional<SensorData> getMostCurrentSensorData(ZonedDateTime lastValidTimestamp) {
        final String sql =
                "SELECT * FROM " + sensorDataTableName + " i " +
                        "WHERE i.EVENT_TIME > ? " +
                        "ORDER BY i.EVENT_TIME DESC " +
                        "LIMIT 1;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(lastValidTimestamp.toLocalDateTime()));
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createSensorData(resultSet));
            }
        } catch (InvalidArgumentException | SQLException e) {
            logger.error("SQL Exception on getMostCurrentSensorData ! {}", e.getMessage());
        }
        return Optional.empty();
    }

    @VisibleForTesting
    void resetDB() {
        final String sql = "DELETE FROM " + sensorDataTableName + ";";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            logger.error("SQL Exception on resetting DB! {}", e.getMessage());
        }
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
        try (Statement statement = connection.createStatement()) {
            final String sql =
                    "CREATE TABLE IF NOT EXISTS " + sensorDataTableName + " (\n" +
                            "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                            "temperature DOUBLE,\n" +
                            "humidity DOUBLE,\n" +
                            "co2 DOUBLE,\n" +
                            "event_time TIMESTAMP);";
            statement.execute(sql);
        }
    }

    private void insertSensorData(Double temperature, Double humidity, Double co2, ZonedDateTime timestamp) throws SQLException {
        final String sql = "INSERT INTO " + sensorDataTableName + " (temperature, humidity, co2, event_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, temperature);
            preparedStatement.setObject(2, humidity);
            preparedStatement.setObject(3, co2);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(timestamp.toLocalDateTime()));
            preparedStatement.executeUpdate();
        }
    }

    private void addResultIfAvailable(List<SensorData> entries, ResultSet resultSet) throws SQLException {
        try {
            entries.add(createSensorData(resultSet));
        } catch (InvalidArgumentException e) {
            logger.error("Next entry could not be loaded! {}", e.getMessage());
        }
    }
}
