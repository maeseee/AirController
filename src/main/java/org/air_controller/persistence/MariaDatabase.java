package org.air_controller.persistence;

import lombok.NoArgsConstructor;
import org.air_controller.secrets.Secret;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@NoArgsConstructor
public class MariaDatabase implements DatabaseConnection {
    private static final Logger logger = LogManager.getLogger(MariaDatabase.class);

    @Override
    public Connection createConnection() throws SQLException {
        final String SCHEMA_NAME = "AirControllerSystem";
        final String JDBC_URL = "jdbc:mariadb://192.168.50.12:3306/" + SCHEMA_NAME;
        final String USER = "mariaDb";
        final String ENVIRONMENT_VARIABLE_DB = "mariaDdPassword";
        final String ENCRYPTED_DB_SECRET = "cfKfHZKIpDP3vLkTZcwxcMYUFW5DFnXSrkjCrlqmM9U=";
        final String password = Secret.getSecret(ENVIRONMENT_VARIABLE_DB, ENCRYPTED_DB_SECRET);
        return DriverManager.getConnection(JDBC_URL, USER, password);
    }

    @Override
    public void execute(String sql) {
        try (Connection connection = createConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            logger.error("SQL Exception on execute {}! {}", sql, e.getMessage());
        }
    }
}