package org.air_controller.system_action;

import org.air_controller.persistence.DatabaseConnection;
import org.air_controller.persistence.EntryAdder;
import org.air_controller.persistence.PreparedStatementSetter;
import org.air_controller.system.OutputState;
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

public class SystemActionDbAccessor {
    private static final Logger logger = LogManager.getLogger(SystemActionDbAccessor.class);

    private final DatabaseConnection database;
    private final SystemPart systemPart;

    public SystemActionDbAccessor(DatabaseConnection database, SystemPart systemPart) {
        this.database = database;
        this.systemPart = systemPart;
        createTableIfNotExists();
    }

    public List<SystemAction> getActionsFromTimeToNow(ZonedDateTime startDateTime) {
        final String sql = "SELECT * FROM " + systemPart.getTableName() + " i " +
                "WHERE i.action_time > ? " +
                "AND i.system_part = ? " +
                "ORDER BY i.action_time;";
        final PreparedStatementSetter setter = preparedStatement -> {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDateTime.toLocalDateTime()));
            preparedStatement.setString(2, systemPart.name());
        };
        final EntryAdder<SystemAction> adder = this::addResultIfAvailable;
        return database.executeQuery(sql, adder, setter);
    }

    public Optional<SystemAction> getMostCurrentState() {
        final String sql = "SELECT * FROM " + systemPart.getTableName() + " i " +
                "ORDER BY i.action_time DESC " +
                "LIMIT 1;";
        final EntryAdder<SystemAction> adder = this::addResultIfAvailable;
        final List<SystemAction> systemActions = database.executeQuery(sql, adder);
        return systemActions.stream().findFirst();
    }

    public void insertAction(OutputState state, ZonedDateTime timestamp) {
        final String sql = "INSERT INTO " + systemPart.getTableName() + " (system_part, status, action_time) " + "VALUES (?, ?, ?)";
        final PreparedStatementSetter preparedStatementSetter = preparedStatement -> {
            preparedStatement.setString(1, systemPart.name());
            preparedStatement.setObject(2, state.name());
            preparedStatement.setObject(3, timestamp.toLocalDateTime());
        };
        database.executeUpdate(sql, preparedStatementSetter);
    }

    private void createTableIfNotExists() {
        final String sql =
                "CREATE TABLE IF NOT EXISTS " + systemPart.getTableName() + " (\n" +
                        "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                        "system_part VARCHAR(20),\n" +
                        "status VARCHAR(20),\n" +
                        "action_time TIMESTAMP);";
        database.executeUpdate(sql);
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
