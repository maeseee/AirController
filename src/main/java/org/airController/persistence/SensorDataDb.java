package org.airController.persistence;

import org.airController.secrets.Secret;
import org.airController.sensorValues.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SensorDataDb implements SensorDataPersistence {
    private static final Logger logger = LogManager.getLogger(SensorDataDb.class);
    private static final String DB_NAME = "SensorDataH2Db";
    private static final String JDBC_URL = "jdbc:h2:./" + DB_NAME;
    private static final String USER = "SensorData";
    private static final String ENVIRONMENT_VARIBLE_DB = "sensorDataDbPassword";
    private static final String ENCRYPTED_DB_SECRET = "mMwIpBLqf8oVg+ahrUTiKRRjx/hdEffKEw6klDCNY3c=";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private final String sensorDataTableName;
    private final String password;

    public SensorDataDb(String sensorDataTableName) {
        this.sensorDataTableName = sensorDataTableName;
        this.password = Secret.getSecret(ENVIRONMENT_VARIBLE_DB, ENCRYPTED_DB_SECRET);
    }

    @Override
    public void persist(SensorData sensorData) {
        try (final Connection connection = DriverManager.getConnection(JDBC_URL, USER, password);
             final Statement statement = connection.createStatement()) {
            final String createTableSql = getCreateTableSql();
            statement.execute(createTableSql);
            final String insertDataSql = getInsertDataSql(sensorData);
            statement.execute(insertDataSql);
        } catch (SQLException e) {
            logger.error("SQL Exception! {}", e.getMessage());
        } catch (InvalidArgumentException e) {
            logger.error("Invalid sensor data! {}", e.getMessage());
        }
    }

    @Override
    public List<SensorData> read() {
        final List<SensorData> entries = new ArrayList<>();
        try (final Connection connection = DriverManager.getConnection(JDBC_URL, USER, password);
             final Statement statement = connection.createStatement()) {

            final String querySQL = getEntriesSql();
            final ResultSet resultSet = statement.executeQuery(querySQL);

            while (resultSet.next()) {
                try {
                    entries.add(createSensorData(resultSet));
                } catch (InvalidArgumentException e) {
                    logger.error("Invalid sensor data! {}", e.getMessage());
                }
            }
            return entries;
        } catch (SQLException e) {
            logger.error("SQL Exception! {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    private static SensorData createSensorData(ResultSet resultSet) throws SQLException, InvalidArgumentException {
        // Read the object as it can handle null
        final Double temp = resultSet.getObject("temperature", Double.class);
        final Double hum = resultSet.getObject("humidity", Double.class);
        final Double carbonDioxide = resultSet.getObject("co2", Double.class);
        final String timestampStr = resultSet.getTimestamp("event_time").toString();
        final LocalDateTime timestamp = LocalDateTime.from(formatter.parse(timestampStr));
        return new SensorDataImpl(temp, hum, carbonDioxide, timestamp);
    }

    private String getCreateTableSql() {
        final String unformattedSql = """
                CREATE TABLE IF NOT EXISTS public.%s (
                     id INT PRIMARY KEY AUTO_INCREMENT,
                    temperature DOUBLE PRECISION,
                    humidity DOUBLE PRECISION,
                    co2 DOUBLE PRECISION,
                    event_time TIMESTAMP);
                """;
        return String.format(unformattedSql, sensorDataTableName);
    }

    private String getInsertDataSql(SensorData sensorData) throws InvalidArgumentException {
        final Double temp = sensorData.getTemperature().map(Temperature::getCelsius).orElse(null);
        final Double hum = sensorData.getHumidity().map(Humidity::getAbsoluteHumidity).orElse(null);
        final Double carbonDioxide = sensorData.getCo2().map(CarbonDioxide::getPpm).orElse(null);
        final String timeStamp = sensorData.getTimeStamp().format(formatter);
        final String unformattedSql = "INSERT INTO %s (temperature, humidity, co2, event_time) VALUES (%f, %f, %f, '%s');";
        return String.format(unformattedSql, sensorDataTableName, temp, hum, carbonDioxide, timeStamp);
    }

    private String getEntriesSql() {
        final String unformattedSql = "SELECT * FROM %s;";
        return String.format(unformattedSql, sensorDataTableName);
    }
}
