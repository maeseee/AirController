package org.air_controller.persistence;

import lombok.NoArgsConstructor;
import org.air_controller.secrets.Secret;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Persistence {
    private static final String SCHEMA_NAME = "AirControllerSystem";
    private static final String JDBC_URL = "jdbc:mariadb://192.168.50.12:3306/" + SCHEMA_NAME;
    ;
    private static final String USER = "mariaDb";
    private static final String ENVIRONMENT_VARIABLE_DB = "mariaDdPassword";
    private static final String ENCRYPTED_DB_SECRET = "cfKfHZKIpDP3vLkTZcwxcMYUFW5DFnXSrkjCrlqmM9U=";

    public static Connection createConnection() throws SQLException {
        final String password = Secret.getSecret(ENVIRONMENT_VARIABLE_DB, ENCRYPTED_DB_SECRET);
        return DriverManager.getConnection(JDBC_URL, USER, password);
    }
}