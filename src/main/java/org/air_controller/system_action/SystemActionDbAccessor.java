package org.air_controller.system_action;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.persistence.DatabaseConnection;
import org.air_controller.persistence.EntryAdder;
import org.air_controller.persistence.PreparedStatementSetter;
import org.air_controller.system.OutputState;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SystemActionDbAccessor {
    private final DatabaseConnection database;
    private final SystemPart systemPart;

    public SystemActionDbAccessor(DatabaseConnection database, SystemPart systemPart) {
        this.database = database;
        this.systemPart = systemPart;
        createTableIfNotExists();
    }

    public List<SystemAction> getActionsFromTimeToNow(ZonedDateTime startDateTime) {
        final String sql = "SELECT status, action_time FROM " + systemPart.getTableName() + " i " +
                "WHERE i.action_time > ? " +
                "ORDER BY i.action_time;";
        final PreparedStatementSetter setter = preparedStatement -> {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDateTime.toLocalDateTime()));
        };
        final EntryAdder<SystemAction> adder = this::addSystemActionResultIfAvailable;
        return database.executeQuery(sql, adder, setter);
    }

    public Optional<SystemAction> getMostCurrentSystemAction() {
        final String sql = "SELECT status, action_time FROM " + systemPart.getTableName() + " i " +
                "ORDER BY i.action_time DESC " +
                "LIMIT 1;";
        final EntryAdder<SystemAction> adder = this::addSystemActionResultIfAvailable;
        final List<SystemAction> systemActions = database.executeQuery(sql, adder);
        return systemActions.stream().findFirst();
    }

    public Optional<VentilationSystemPersistenceData> getMostCurrentPersistenceData() {
        final String sql = "SELECT * FROM " + systemPart.getTableName() + " i " +
                "ORDER BY i.action_time DESC " +
                "LIMIT 1;";
        final EntryAdder<VentilationSystemPersistenceData> adder = this::addPersistenceDataResultIfAvailable;
        final List<VentilationSystemPersistenceData> systemActions = database.executeQuery(sql, adder);
        return systemActions.stream().findFirst();
    }

    public void insertAction(VentilationSystemPersistenceData data) {
        final String sql =
                "INSERT INTO " + systemPart.getTableName() + " (status, action_time, total_confidence, confidences) " + "VALUES (?, ?, ?, ?, ?)";
        final PreparedStatementSetter preparedStatementSetter = preparedStatement -> {
            preparedStatement.setObject(1, data.action().name());
            preparedStatement.setObject(2, data.timestamp().toLocalDateTime());
            preparedStatement.setDouble(3, data.totalConfidence());
            preparedStatement.setString(4, data.getConfidencesText());
        };
        database.executeUpdate(sql, preparedStatementSetter);
    }

    private void createTableIfNotExists() {
        final String sql =
                "CREATE TABLE IF NOT EXISTS " + systemPart.getTableName() + " (\n" +
                        "id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                        "system_part VARCHAR(20),\n" +
                        "status VARCHAR(20),\n" +
                        "action_time TIMESTAMP,\n" +
                        "total_confidence DOUBLE,\n" +
                        "confidences VARCHAR(200));";
        database.executeUpdate(sql);
    }

    private void addSystemActionResultIfAvailable(List<SystemAction> entries, ResultSet resultSet) {
        try {
            entries.add(createSystemAction(resultSet));
        } catch (SQLException e) {
            log.error("System action entry could not be loaded! {}", e.getMessage());
        }
    }

    private SystemAction createSystemAction(ResultSet resultSet) throws SQLException {
        final String statusString = resultSet.getString("status");
        final OutputState outputState = OutputState.valueOf(statusString);
        final ZonedDateTime actionTime = ZonedDateTime.of(resultSet.getObject("action_time", LocalDateTime.class), ZoneOffset.UTC);
        return new SystemAction(actionTime, outputState);
    }

    private void addPersistenceDataResultIfAvailable(List<VentilationSystemPersistenceData> entries, ResultSet resultSet) {
        try {
            entries.add(createSystemPersistenceData(resultSet));
        } catch (SQLException e) {
            log.error("Persistence data entry could not be loaded! {}", e.getMessage());
        }
    }

    private VentilationSystemPersistenceData createSystemPersistenceData(ResultSet resultSet) throws SQLException {
        final String statusString = resultSet.getString("status");
        final OutputState outputState = OutputState.valueOf(statusString);
        final ZonedDateTime actionTime = ZonedDateTime.of(resultSet.getObject("action_time", LocalDateTime.class), ZoneOffset.UTC);
        final double totalConfidence = resultSet.getDouble("total_confidence");
        final String confidencesString = resultSet.getString("confidences");
        return VentilationSystemPersistenceData.create(outputState, totalConfidence, confidencesString, actionTime);
    }
}
