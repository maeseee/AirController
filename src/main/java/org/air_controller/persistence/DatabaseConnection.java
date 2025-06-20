package org.air_controller.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DatabaseConnection {
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    public abstract Connection createConnection() throws SQLException;

    public abstract void execute(String sql);

    public void executeUpdate(String sql, PreparedStatementSetter setter) {
        try (Connection connection = createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setter.setParameters(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception on executeUpdate! {}", e.getMessage());
        }
    }
}
