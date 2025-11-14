package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class ClimateSensor implements Runnable {
    private static final Duration SENSOR_INVALIDATION = Duration.ofHours(4);

    protected final ClimateDataPointPersistence persistence;

    public Optional<ClimateDataPoint> getCurrentDataPoint() {
        return persistence.getMostCurrentClimateDataPoint(getLastValidTimestamp());
    }

    protected abstract Optional<ClimateDataPoint> parseResponse(String response);

//    public abstract Optional<ClimateDataPoint> readDataPoint();

    protected ZonedDateTime getLastValidTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).minus(SENSOR_INVALIDATION);
    }
}
