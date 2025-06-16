package org.air_controller.persistence;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {

    Connection createConnection() throws SQLException;
}
