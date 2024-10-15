package org.airController.persistence;

import org.airController.secrets.Secret;
import org.airController.sensorValues.SensorData;
import org.airController.system.VentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VentilationSystemActions implements VentilationSystem {
    private static final Logger logger = LogManager.getLogger(VentilationSystemActions.class);
    private static final String DB_NAME = "SensorDataH2Db";
    private static final String JDBC_URL = "jdbc:h2:./" + DB_NAME;
    private static final String USER = "SensorData";
    private static final String ENVIRONMENT_VARIBLE_DB = "dataDbPassword";
    private static final String ENCRYPTED_DB_SECRET = "mMwIpBLqf8oVg+ahrUTiKRRjx/hdEffKEw6klDCNY3c=";

    private final String airFlowTableName;
    private final Connection connection;

    public VentilationSystemActions(String sensorDataTableName) {
        this.airFlowTableName = sensorDataTableName;
        final String password = Secret.getSecret(ENVIRONMENT_VARIBLE_DB, ENCRYPTED_DB_SECRET);
        try {
            connection = DriverManager.getConnection(JDBC_URL, USER, password);
            createTableIfNotExists();
        } catch (SQLException e) {
            logger.error("SQL Exception on creating connection! {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override public void setAirFlowOn(boolean on) {
        try {
            insertAirflowState(on, LocalDateTime.now());
        } catch (SQLException e) {
            logger.error("SQL Exception! {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unknown error! {}", e.getMessage());
        }
    }

    @Override public void setHumidityExchangerOn(boolean on) {
//        try {
//            insertAirflowState(on, LocalDateTime.now());
//        } catch (SQLException e) {
//            logger.error("SQL Exception! {}", e.getMessage());
//        } catch (Exception e) {
//            logger.error("Unknown error! {}", e.getMessage());
//        }
    }

    public List<SensorData> read() {
        final List<SensorData> entries = new ArrayList<>();
        try {
            final Statement statement = connection.createStatement();
            final String sql = "SELECT * FROM " + airFlowTableName + ";";
            final ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
//                try {
//                    entries.add(createSensorData(resultSet));
//                } catch (InvalidArgumentException e) {
//                    logger.error("Next entry could not be loaded! {}", e.getMessage());
//                }
            }
            return entries;
        } catch (SQLException e) {
            logger.error("SQL Exception on read ! {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    private void createTableIfNotExists() throws SQLException {
        final Statement statement = connection.createStatement();
        final String sql = "CREATE TABLE IF NOT EXISTS public." + airFlowTableName + " (\n" +
                "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "on_state BOOLEAN,\n" +
                "event_time TIMESTAMP);";
        statement.execute(sql);
    }

    private void insertAirflowState(boolean onState, LocalDateTime timestamp) throws SQLException {
        final String sql = "INSERT INTO " + airFlowTableName + " (on_state, event_time) VALUES (?, ?)";
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, onState);
        preparedStatement.setObject(2, timestamp);
        preparedStatement.executeUpdate();
    }
}
