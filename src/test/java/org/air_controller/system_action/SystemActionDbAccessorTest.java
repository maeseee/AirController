package org.air_controller.system_action;

import org.air_controller.persistence.DatabaseConnection;
import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.system.OutputState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class SystemActionDbAccessorTest {
    private static final SystemPart SYSTEM_PART = SystemPart.AIR_FLOW;

    private DatabaseConnection database;

    @BeforeEach
    void setupDatabase() {
        database = new LocalInMemoryDatabase();
    }

    @Test
    void shouldReturnTheMostCurrentState() {
        final SystemActionDbAccessor testee = new SystemActionDbAccessor(database, SYSTEM_PART);
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        testee.insertAction(new VentilationSystemPersistenceData(OutputState.ON, 0.0, Collections.emptyMap(), now.minusHours(1)));
        testee.insertAction(new VentilationSystemPersistenceData(OutputState.OFF, 0.0, Collections.emptyMap(), now));
        testee.insertAction(new VentilationSystemPersistenceData(OutputState.ON, 0.0, Collections.emptyMap(), now.minusHours(2)));

        final Optional<SystemAction> result = testee.getMostCurrentSystemAction();

        assertThat(result).isPresent();
        assertThat(result.get().outputState()).isEqualTo(OutputState.OFF);
        assertThat(result.get().actionTime()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    @Test
    void shouldReturnActionsInTimeRange() {
        final SystemActionDbAccessor testee = new SystemActionDbAccessor(database, SYSTEM_PART);
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        testee.insertAction(new VentilationSystemPersistenceData(OutputState.ON, 0.0, Collections.emptyMap(), now.minusHours(1)));
        testee.insertAction(new VentilationSystemPersistenceData(OutputState.OFF, 0.0, Collections.emptyMap(), now.minusMinutes(1)));
        testee.insertAction(new VentilationSystemPersistenceData(OutputState.ON, 0.0, Collections.emptyMap(), now.minusHours(4)));
        testee.insertAction(new VentilationSystemPersistenceData(OutputState.OFF, 0.0, Collections.emptyMap(), now.minusHours(2)));

        final List<SystemAction> actionsFromTimeToNow = testee.getActionsFromTimeToNow(now.minusHours(3));

        assertThat(actionsFromTimeToNow).hasSize(3);
    }
}