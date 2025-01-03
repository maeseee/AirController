package org.air_controller.sensor_data_persistence;

import org.air_controller.sensor_values.SensorData;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SensorDataPrinter implements SensorDataPersistence {

    @Override
    public void persist(SensorData sensorData) {
        System.out.println("Persist: " + sensorData);
    }

    @Override
    public List<SensorData> read() {
        return Collections.emptyList();
    }

    @Override
    public Optional<SensorData> getMostCurrentSensorData(ZonedDateTime lastValidTimestamp) {
        return Optional.empty();
    }
}
