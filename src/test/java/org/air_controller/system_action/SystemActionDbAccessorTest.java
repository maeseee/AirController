package org.air_controller.system_action;

import org.air_controller.system.OutputState;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
        testee.insertAction(OutputState.ON, now.minusHours(1));
        testee.insertAction(OutputState.OFF, now);
        testee.insertAction(OutputState.ON, now.minusHours(2));

        final Optional<SystemAction> result = testee.getMostCurrentState();

        assertThat(result).isPresent();
        assertThat(result.get().systemPart()).isEqualTo(SystemPart.AIR_FLOW);
        assertThat(result.get().outputState()).isEqualTo(OutputState.OFF);
        assertThat(result.get().actionTime()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    @Test
    void shouldReturnActionsInTimeRange() throws Exception {
        final SystemActionDbAccessor testee = new SystemActionDbAccessor(connection, SYSTEM_PART);
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        testee.insertAction(OutputState.ON, now.minusHours(1));
        testee.insertAction(OutputState.OFF, now.minusMinutes(1));
        testee.insertAction(OutputState.ON, now.minusHours(4));
        testee.insertAction(OutputState.OFF, now.minusHours(2));

        final List<SystemAction> actionsFromTimeToNow = testee.getActionsFromTimeToNow(now.minusHours(3));

        assertThat(actionsFromTimeToNow).hasSize(3);
    }
}