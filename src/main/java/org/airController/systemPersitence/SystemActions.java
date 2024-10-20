package org.airController.systemPersitence;

import org.airController.persistence.Persistence;
import org.airController.system.OutputState;
import org.airController.system.VentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SystemActions implements VentilationSystem {
    private static final Logger logger = LogManager.getLogger(SystemActions.class);
    private static final String AIR_FLOW_ACTION_TABLE_NAME = "airFlowActions";
    private static final String HUMIDITY_ACTION_TABLE_NAME = "humidityActions";

    private final Connection connection;

    public SystemActions() {
        try {
            connection = Persistence.createConnection();
            createTableIfNotExists(AIR_FLOW_ACTION_TABLE_NAME);
            createTableIfNotExists(HUMIDITY_ACTION_TABLE_NAME);
        } catch (SQLException e) {
            logger.error("SQL Exception on creating connection! {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setAirFlowOn(OutputState state) {
        final SystemAction currentAction = getMostCurrentAirFlowState();
        if (currentAction.outputState() == state) {
            return;
        }
        try {
            insertAction(AIR_FLOW_ACTION_TABLE_NAME, SystemPart.AIR_FLOW, state, LocalDateTime.now());
        } catch (SQLException e) {
            logger.error("SQL Exception! {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unknown error! {}", e.getMessage());
        }
    }

    @Override
    public void setHumidityExchangerOn(OutputState state) {
        try {
            insertAction(HUMIDITY_ACTION_TABLE_NAME, SystemPart.HUMIDITY, state, LocalDateTime.now());
        } catch (SQLException e) {
            logger.error("SQL Exception! {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unknown error! {}", e.getMessage());
        }
    }

    public List<SystemAction> getActionsFromTimeToNow(LocalDateTime startDateTime, SystemPart part) {
        final List<SystemAction> entries = new ArrayList<>();
        final String sql =
                "SELECT * FROM " + AIR_FLOW_ACTION_TABLE_NAME + " i " +
                        "WHERE i.action_time > ? " +
                        "AND i.system_part = ? " +
                        "ORDER BY i.action_time ASC;";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDateTime));
            preparedStatement.setString(2, part.name());
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                try {
                    entries.add(createSystemAction(resultSet));
                } catch (SQLException e) {
                    logger.error("Next entry could not be loaded! {}", e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception on read ! {}", e.getMessage());
        }
        return entries;
    }

    public SystemAction getMostCurrentAirFlowState() {
        final String sql =
                "SELECT * FROM " + AIR_FLOW_ACTION_TABLE_NAME + " i " +
                        "ORDER BY i.action_time DESC " +
                        "LIMIT 1;";
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createSystemAction(resultSet);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception on read ! {}", e.getMessage());
        }
        return null;
    }

    private void createTableIfNotExists(String tableName) throws SQLException {
        final String sql =
                "CREATE TABLE IF NOT EXISTS public." + tableName + " (\n" +
                        "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                        "system_part VARCHAR(20),\n" +
                        "status VARCHAR(20),\n" +
                        "action_time TIMESTAMP);";
        final Statement statement = connection.createStatement();
        statement.execute(sql);
    }

    private void insertAction(String tableName, SystemPart systemPart, OutputState state, LocalDateTime timestamp) throws SQLException {
        final String sql = "INSERT INTO " + tableName + " (system_part, status, action_time) VALUES (?, ?, ?)";
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, systemPart.name());
        preparedStatement.setObject(2, state.name());
        preparedStatement.setObject(3, timestamp);
        preparedStatement.executeUpdate();
    }

    private SystemAction createSystemAction(ResultSet resultSet) throws SQLException {
        final String systemPartString = resultSet.getString("system_part");
        final SystemPart systemPart = SystemPart.valueOf(systemPartString);
        final String statusString = resultSet.getString("status");
        final OutputState outputState = OutputState.valueOf(statusString);
        final LocalDateTime actionTime = resultSet.getObject("action_time", LocalDateTime.class);
        return new SystemAction(actionTime, systemPart, outputState);
    }
}
