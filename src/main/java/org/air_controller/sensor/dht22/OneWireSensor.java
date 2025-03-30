package org.air_controller.sensor.dht22;

import lombok.Getter;
import org.air_controller.sensor.Sensor;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;
import org.air_controller.sensor_values.SensorData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class OneWireSensor implements Sensor {
    private static final Logger logger = LogManager.getLogger(OneWireSensor.class);

    @Getter
    private final SensorDataPersistence persistence;
    private final Dht22 dht22;

    public OneWireSensor(SensorDataPersistence persistence) throws IOException {
        this(persistence, new Dht22Impl());
    }

    public OneWireSensor(SensorDataPersistence persistence, Dht22 dht22) {
        this.persistence = persistence;
        this.dht22 = dht22;
    }

    @Override
    public void run() {
        try {
            final Optional<SensorData> indoorSensorData = dht22.refreshData();
            indoorSensorData.ifPresentOrElse(
                    this::persistData,
                    () -> logger.error("Indoor sensor out of order"));
        } catch (final Exception e) {
            logger.error("OneWireSensor out of order", e);
        }
    }

    private void persistData(SensorData indoorSensorData) {
        logger.info("New indoor sensor data: {}", indoorSensorData);
        persistence.persist(indoorSensorData);
    }


}
