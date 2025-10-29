package org.air_controller.sensor_values;

import lombok.RequiredArgsConstructor;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class CurrentSensorData {
    private static final Duration SENSOR_INVALIDATION = Duration.ofHours(4);

    private final SensorDataPersistence persistence;

    public Optional<SensorData> getCurrentSensorData() {
        return persistence.getMostCurrentSensorData(getLastValidTimestamp());
    }

    private ZonedDateTime getLastValidTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).minus(SENSOR_INVALIDATION);
    }
}


