package org.air_controller.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LocalInMemoryDatabase implements DatabaseConnection {
    private static final Logger logger = LogManager.getLogger(LocalInMemoryDatabase.class);

    @Override
    public Connection createConnection() throws SQLException {
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource.getConnection();
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
