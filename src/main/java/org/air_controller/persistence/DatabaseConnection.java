package org.air_controller.persistence;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class DatabaseConnection {
    public void executeUpdate(String sql) {
        final PreparedStatementSetter setter = _ -> {
        };
        executeUpdate(sql, setter);
    }

    public void executeUpdate(String sql, PreparedStatementSetter setter) {
        //noinspection SqlSourceToSinkFlow
        try (Connection connection = createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setter.setParameters(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQL Exception on executeUpdate! {}", e.getMessage());
        }
    }

    public <R> List<R> executeQuery(String sql, EntryAdder<R> adder) {
        final PreparedStatementSetter setter = _ -> {
        };
        return executeQuery(sql, adder, setter);
    }

    public <R> List<R> executeQuery(String sql, EntryAdder<R> adder, PreparedStatementSetter setter) {
        final List<R> entries = new ArrayList<>();
        //noinspection SqlSourceToSinkFlow
        try (Connection connection = createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setter.setParameters(preparedStatement);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    adder.addResultIfAvailable(entries, resultSet);
                }
            }
        } catch (SQLException e) {
            log.error("SQL Exception on executeQuery! {}", e.getMessage());
        }
        return entries;
    }

    protected abstract Connection createConnection() throws SQLException;
}
