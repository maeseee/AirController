package org.air_controller.system_action;

import org.air_controller.persistence.Persistence;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            logger.error(e);
            throw new ActionException("SQL Exception on creating connection! " + e.getMessage(), e.getCause());
        }
    }

    @Override
    public void setAirFlowOn(OutputState state) {
        final Optional<SystemAction> currentAction = getMostCurrentAirFlowState();
        if (currentAction.isPresent() && currentAction.get().outputState() == state) {
            return;
        }
        try {
            insertAction(AIR_FLOW_ACTION_TABLE_NAME, SystemPart.AIR_FLOW, state, ZonedDateTime.now(ZoneOffset.UTC));
        } catch (SQLException e) {
            logger.error("SQL Exception! {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unknown error! {}", e.getMessage());
        }
    }

    @Override
    public void setHumidityExchangerOn(OutputState state) {
        try {
            insertAction(HUMIDITY_ACTION_TABLE_NAME, SystemPart.HUMIDITY, state, ZonedDateTime.now(ZoneOffset.UTC));
        } catch (SQLException e) {
            logger.error("SQL Exception! {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unknown error! {}", e.getMessage());
        }
    }

    public List<SystemAction> getActionsFromTimeToNow(ZonedDateTime startDateTime, SystemPart part) {
        final List<SystemAction> entries = new ArrayList<>();
        final String sql =
                "SELECT * FROM " + AIR_FLOW_ACTION_TABLE_NAME + " i " +
                        "WHERE i.action_time > ? " +
                        "AND i.system_part = ? " +
                        "ORDER BY i.action_time;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDateTime.toLocalDateTime()));
            preparedStatement.setString(2, part.name());
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                addResultIfAvailable(entries, resultSet);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception on read ! {}", e.getMessage());
        }
        return entries;
    }

    private Optional<SystemAction> getMostCurrentAirFlowState() {
        final String sql =
                "SELECT * FROM " + AIR_FLOW_ACTION_TABLE_NAME + " i " +
                        "ORDER BY i.action_time DESC " +
                        "LIMIT 1;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createSystemAction(resultSet));
            }
        } catch (SQLException e) {
            logger.error("SQL Exception on read ! {}", e.getMessage());
        }
        return Optional.empty();
    }

    private void createTableIfNotExists(String tableName) throws SQLException {
        final String sql =
                "CREATE TABLE IF NOT EXISTS " + tableName + " (\n" +
                        "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                        "system_part VARCHAR(20),\n" +
                        "status VARCHAR(20),\n" +
                        "action_time TIMESTAMP);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private void insertAction(String tableName, SystemPart systemPart, OutputState state, ZonedDateTime timestamp) throws SQLException {
        final String sql = "INSERT INTO " + tableName + " (system_part, status, action_time) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, systemPart.name());
            preparedStatement.setObject(2, state.name());
            preparedStatement.setObject(3, timestamp.toLocalDateTime());
            preparedStatement.executeUpdate();
        }
    }

    private SystemAction createSystemAction(ResultSet resultSet) throws SQLException {
        final String systemPartString = resultSet.getString("system_part");
        final SystemPart systemPart = SystemPart.valueOf(systemPartString);
        final String statusString = resultSet.getString("status");
        final OutputState outputState = OutputState.valueOf(statusString);
        final ZonedDateTime actionTime = ZonedDateTime.of(resultSet.getObject("action_time", LocalDateTime.class), ZoneOffset.UTC);
        return new SystemAction(actionTime, systemPart, outputState);
    }

    private void addResultIfAvailable(List<SystemAction> entries, ResultSet resultSet) {
        try {
            entries.add(createSystemAction(resultSet));
        } catch (SQLException e) {
            logger.error("Next entry could not be loaded! {}", e.getMessage());
        }
    }
}
