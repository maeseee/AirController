package org.air_controller.sensor_data_persistence;

import org.air_controller.sensor_values.SensorData;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class SensorDataCollection implements SensorDataPersistence {
    private final List<SensorDataPersistence> persistences;

    public SensorDataCollection(List<SensorDataPersistence> persistences) {
        this.persistences = persistences;
    }

    @Override
    public void persist(SensorData sensorData) {
        persistences.forEach(persistence -> persistence.persist(sensorData));
    }

    @Override
    public List<SensorData> read() {
        return persistences.get(0).read();
    }

    @Override
    public Optional<SensorData> getMostCurrentSensorData(ZonedDateTime lastValidTimestamp) {
        return persistences.get(0).getMostCurrentSensorData(lastValidTimestamp);
    }
}
