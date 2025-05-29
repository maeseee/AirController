package org.air_controller.sensor_data_persistence;

import org.air_controller.sensor_values.SensorData;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class SensorDataCollection implements SensorDataPersistence {
    private final List<SensorDataPersistence> persistenceList;

    public SensorDataCollection(List<SensorDataPersistence> persistenceList) {
        this.persistenceList = persistenceList;
    }

    @Override
    public void persist(SensorData sensorData) {
        persistenceList.forEach(persistence -> persistence.persist(sensorData));
    }

    @Override
    public List<SensorData> read() {
        return persistenceList.getFirst().read();
    }

    @Override
    public Optional<SensorData> getMostCurrentSensorData(ZonedDateTime lastValidTimestamp) {
        return persistenceList.getFirst().getMostCurrentSensorData(lastValidTimestamp);
    }
}
