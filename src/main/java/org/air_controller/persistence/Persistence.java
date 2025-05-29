package org.air_controller.persistence;

import lombok.NoArgsConstructor;
import org.air_controller.secrets.EnvironmentVariable;
import org.air_controller.secrets.Secret;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Persistence {
    private static final String SCHEMA_NAME = "AirControllerSystem";
    // DS224P.local for all hosts
    // host.docker.internal for docker, if DB is outside this container
    private static final String DB_HOST_NAME_VARIABLE = "dbHostNameVariable";
    private static final String DB_HOST_NAME = "DS224P.local";
    private static final String USER = "mariaDb";
    private static final String ENVIRONMENT_VARIABLE_DB = "mariaDdPassword";
    private static final String ENCRYPTED_DB_SECRET = "cfKfHZKIpDP3vLkTZcwxcMYUFW5DFnXSrkjCrlqmM9U=";

    public static Connection createConnection() throws SQLException {
        final String hostName = EnvironmentVariable.readEnvironmentVariable(DB_HOST_NAME_VARIABLE).orElse(DB_HOST_NAME);
        final String jdbc_url = "jdbc:mariadb://" + hostName + ":3306/" + SCHEMA_NAME;
        final String password = Secret.getSecret(ENVIRONMENT_VARIABLE_DB, ENCRYPTED_DB_SECRET);
        return DriverManager.getConnection(jdbc_url, USER, password);
    }
}