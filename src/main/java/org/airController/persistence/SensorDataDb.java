package org.airController.persistence;

import org.airController.secrets.Secret;
import org.airController.sensorValues.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SensorDataDb implements SensorDataPersistence {
    private static final Logger logger = LogManager.getLogger(SensorDataDb.class);
    private static final String SCHEMA_NAME = "AirControllerSystem";
    private static final String JDBC_URL = "jdbc:h2:./" + SCHEMA_NAME;
    private static final String USER = "SensorData";
    private static final String ENVIRONMENT_VARIBLE_DB = "dbPassword";
    private static final String ENCRYPTED_DB_SECRET = "mMwIpBLqf8oVg+ahrUTiKRRjx/hdEffKEw6klDCNY3c=";

    private final String sensorDataTableName;
    private final Connection connection;

    public SensorDataDb(String sensorDataTableName) {
        this.sensorDataTableName = sensorDataTableName;
        final String password = Secret.getSecret(ENVIRONMENT_VARIBLE_DB, ENCRYPTED_DB_SECRET);
        try {
            connection = DriverManager.getConnection(JDBC_URL, USER, password);
            createTableIfNotExists();
        } catch (SQLException e) {
            logger.error("SQL Exception on creating connection! {}", e.getMessage());
            throw new RuntimeException(e);
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
        try {
            final Statement statement = connection.createStatement();
            final String sql = "SELECT * FROM " + sensorDataTableName + ";";
            final ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                try {
                    entries.add(createSensorData(resultSet));
                } catch (InvalidArgumentException e) {
                    logger.error("Next entry could not be loaded! {}", e.getMessage());
                }
            }
            return entries;
        } catch (SQLException e) {
            logger.error("SQL Exception on read ! {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<SensorData> getMostCurrentSensorData(LocalDateTime lastValidTimestamp) {
        try {
            final String sql = "SELECT * FROM " + sensorDataTableName + " i " +
                    "WHERE i.EVENT_TIME > ? " +
                    "ORDER BY i.EVENT_TIME DESC " +
                    "LIMIT 1;";
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(lastValidTimestamp));
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                try {
                    return Optional.of(createSensorData(resultSet));
                } catch (InvalidArgumentException e) {
                    logger.error("Next entry could not be loaded! {}", e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception on read ! {}", e.getMessage());
        }
        return Optional.empty();
    }

    private SensorData createSensorData(ResultSet resultSet) throws SQLException, InvalidArgumentException {
        // Read the object as it can handle null
        final Double temp = resultSet.getObject("temperature", Double.class);
        final Double hum = resultSet.getObject("humidity", Double.class);
        final Double carbonDioxide = resultSet.getObject("co2", Double.class);
        final LocalDateTime timestamp = resultSet.getObject("event_time", LocalDateTime.class);
        return new SensorDataImpl(temp, hum, carbonDioxide, timestamp);
    }

    private void createTableIfNotExists() throws SQLException {
        final Statement statement = connection.createStatement();
        final String sql = "CREATE TABLE IF NOT EXISTS public." + sensorDataTableName + " (\n" +
                "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "temperature DOUBLE,\n" +
                "humidity DOUBLE,\n" +
                "co2 DOUBLE,\n" +
                "event_time TIMESTAMP);";
        statement.execute(sql);
    }

    private void insertSensorData(Double temperature, Double humidity, Double co2, LocalDateTime timestamp) throws SQLException {
        final String sql = "INSERT INTO " + sensorDataTableName + " (temperature, humidity, co2, event_time) VALUES (?, ?, ?, ?)";
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, temperature);
        preparedStatement.setObject(2, humidity);
        preparedStatement.setObject(3, co2);
        preparedStatement.setTimestamp(4, Timestamp.valueOf(timestamp));
        preparedStatement.executeUpdate();
    }
}
