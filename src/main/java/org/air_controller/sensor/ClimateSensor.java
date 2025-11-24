package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class ClimateSensor implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClimateSensor.class);

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
            logger.error("Exception in Climate Sensor loop:", exception);
        }
    }

    public Optional<ClimateDataPoint> getCurrentDataPoint() {
        return persistence.getMostCurrentClimateDataPoint(getLastValidTimestamp());
    }

    protected abstract Optional<ClimateDataPoint> parseResponse(String response);

    protected abstract String sensorType();

    private void persistDataPoint(ClimateDataPoint dataPoint) {
        logger.info("Data point on {}: {}", sensorType(), dataPoint);
        persistence.persist(dataPoint);
    }

    private ZonedDateTime getLastValidTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).minus(SENSOR_INVALIDATION);
    }
}
