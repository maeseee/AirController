package org.airController.persistence;

import org.airController.sensorValues.InvalidArgumentException;
import org.airController.sensorValues.SensorData;
import org.airController.sensorValues.SensorDataImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SensorDataDbTest {

    @Test
    void shouldAddTable() throws InvalidArgumentException {
        final SensorData sensorData = new SensorDataImpl(21.0, 10.0, 500.0, LocalDateTime.now());
        final String sensorDataTableName = "TestSensorTable";
        final SensorDataDb testee = new SensorDataDb(sensorDataTableName);
        final int numberOfEntries = testee.read().size();

        testee.persist(sensorData);

        final List<SensorData> entries = testee.read();
        assertThat(entries.size()).isEqualTo(numberOfEntries + 1);
    }
}