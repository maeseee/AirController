package org.air_controller.persistence;

import lombok.NoArgsConstructor;
import org.air_controller.secrets.Secret;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor
public class MariaDatabase extends DatabaseConnection {

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
}