package org.air_controller.system_action;

import org.air_controller.system.OutputState;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class SystemActionDbAccessorTest {
    private static final SystemPart SYSTEM_PART = SystemPart.AIR_FLOW;

    private Connection connection;

    @BeforeEach
    void setupDatabase() throws Exception {
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        connection = dataSource.getConnection();
    }

    @AfterEach
    void cleanup() throws Exception {
        connection.close();
    }

    @Test
    void shouldReturnTheMostCurrentState() throws Exception {
        final SystemActionDbAccessor testee = new SystemActionDbAccessor(connection, SYSTEM_PART);
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        insertStateIntoTable(now.minusHours(1), OutputState.ON);
        insertStateIntoTable(now, OutputState.OFF);
        insertStateIntoTable(now.minusHours(2), OutputState.ON);

        final Optional<SystemAction> result = testee.getMostCurrentState();

        assertThat(result).isPresent();
        assertThat(result.get().systemPart()).isEqualTo(SystemPart.AIR_FLOW);
        assertThat(result.get().outputState()).isEqualTo(OutputState.OFF);
        assertThat(result.get().actionTime()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    private void insertStateIntoTable(ZonedDateTime timestamp, OutputState state) throws SQLException {
        final String sql =
                "INSERT INTO " + SYSTEM_PART.getTableName() + " (system_part, status, action_time) " +
                        "VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, SYSTEM_PART.name());
            preparedStatement.setObject(2, state.name());
            preparedStatement.setObject(3, timestamp.toLocalDateTime());
            preparedStatement.executeUpdate();
        }
    }
}