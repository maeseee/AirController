package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public abstract class ClimateSensor implements Runnable {
    private static final Duration SENSOR_INVALIDATION = Duration.ofHours(4);

    protected final ClimateDataPointPersistence persistence;
    protected final SensorReader sensor;

    @Override
    public void run() {
        try {
            final String response = sensor.readData();
            Optional<ClimateDataPoint> dataPoint = parseResponse(response);
            dataPoint.ifPresent(this::persistDataPoint);
        } catch (Exception exception) {
            log.error("Exception in Climate Sensor loop:", exception);
        }
    }

    public Optional<ClimateDataPoint> getCurrentDataPoint() {
        return persistence.getMostCurrentClimateDataPoint(getLastValidTimestamp());
    }

    protected abstract Optional<ClimateDataPoint> parseResponse(String response);

    private void persistDataPoint(ClimateDataPoint dataPoint) {
        persistence.persist(dataPoint);
    }

    private ZonedDateTime getLastValidTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).minus(SENSOR_INVALIDATION);
    }
}
