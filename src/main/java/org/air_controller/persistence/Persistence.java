package org.air_controller.persistence;

import org.air_controller.secrets.Secret;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Persistence {
    private static final String SCHEMA_NAME = "AirControllerSystem";
    private static final String JDBC_URL = "jdbc:h2:./" + SCHEMA_NAME;
    private static final String USER = "SensorData"; // TODO update username
    private static final String ENVIRONMENT_VARIBLE_DB = "dbPassword";
    private static final String ENCRYPTED_DB_SECRET = "mMwIpBLqf8oVg+ahrUTiKRRjx/hdEffKEw6klDCNY3c=";

    public static Connection createConnection() throws SQLException {
        final String password = Secret.getSecret(ENVIRONMENT_VARIBLE_DB, ENCRYPTED_DB_SECRET);
        return DriverManager.getConnection(JDBC_URL, USER, password);
    }
}
