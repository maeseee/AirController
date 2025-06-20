package org.air_controller.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseConnection {
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    public void executeUpdate(String sql) {
        final PreparedStatementSetter setter = preparedStatement -> {
        };
        executeUpdate(sql, setter);
    }

    public void executeUpdate(String sql, PreparedStatementSetter setter) {
        try (Connection connection = createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setter.setParameters(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception on executeUpdate! {}", e.getMessage());
        }
    }

    public <R> List<R> executeQuery(String sql, EntryAdder<R> adder) {
        final PreparedStatementSetter setter = preparedStatement -> {
        };
        return executeQuery(sql, adder, setter);
    }

    public <R> List<R> executeQuery(String sql, EntryAdder<R> adder, PreparedStatementSetter setter) {
        final List<R> entries = new ArrayList<>();
        try (Connection connection = createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setter.setParameters(preparedStatement);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    adder.addResultIfAvailable(entries, resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception on executeQuery! {}", e.getMessage());
        }
        return entries;
    }

    protected abstract Connection createConnection() throws SQLException;
}
