package org.air_controller.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseConnection {
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    public abstract Connection createConnection() throws SQLException;

    public void execute(String sql) {
        try (Connection connection = createConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            logger.error("SQL Exception on execute {}! {}", sql, e.getMessage());
        }
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

    public <R> List<R> executeQuery(String sql, PreparedStatementSetter setter, EntryAdder<R> adder) {
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
            logger.error("SQL Exception on executeUpdate! {}", e.getMessage());
        }
        return entries;
    }
}
