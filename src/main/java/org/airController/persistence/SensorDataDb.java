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
    private final Connection connection;

    public SensorDataDb(String sensorDataTableName) {
        this.sensorDataTableName = sensorDataTableName;
        String password = Secret.getSecret(ENVIRONMENT_VARIBLE_DB, ENCRYPTED_DB_SECRET);
        try {
            connection = DriverManager.getConnection(JDBC_URL, USER, password);
        } catch (SQLException e) {
            logger.error("SQL Exception on creating connection! {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void persist(SensorData sensorData) {
        try {
            createTable();
            insertSensorData(sensorData);
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

    private SensorData createSensorData(ResultSet resultSet) throws SQLException, InvalidArgumentException {
        // Read the object as it can handle null
        final Double temp = resultSet.getObject("temperature", Double.class);
        final Double hum = resultSet.getObject("humidity", Double.class);
        final Double carbonDioxide = resultSet.getObject("co2", Double.class);
        final String timestampStr = resultSet.getTimestamp("event_time").toString();
        final LocalDateTime timestamp = LocalDateTime.from(formatter.parse(timestampStr));
        return new SensorDataImpl(temp, hum, carbonDioxide, timestamp);
    }

    private void createTable() throws SQLException {
        final Statement statement = connection.createStatement();
        final String sql = "CREATE TABLE IF NOT EXISTS public." + sensorDataTableName + " (\n" +
                "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "temperature DOUBLE,\n" +
                "humidity DOUBLE,\n" +
                "co2 DOUBLE,\n" +
                "event_time TIMESTAMP);";
        statement.execute(sql);
    }

    private void insertSensorData(SensorData sensorData) throws SQLException {
        final String sql = "INSERT INTO " + sensorDataTableName + " (temperature, humidity, co2, event_time) VALUES (?, ?, ?, ?)";
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);

        final Double temp = sensorData.getTemperature().map(Temperature::getCelsius).orElse(null);
        preparedStatement.setObject(1, temp);

        final Double hum = sensorData.getHumidity().map(Humidity::getAbsoluteHumidity).orElse(null);
        preparedStatement.setObject(2, hum);

        final Double carbonDioxide = sensorData.getCo2().map(CarbonDioxide::getPpm).orElse(null);
        preparedStatement.setObject(3, carbonDioxide);

        final String timeStamp = sensorData.getTimeStamp().format(formatter);
        preparedStatement.setObject(4, timeStamp);

        preparedStatement.executeUpdate();
    }
}
