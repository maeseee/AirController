package org.airController.sensorDataPersistence;

import org.airController.sensorValues.SensorData;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class SensorDataCollection implements SensorDataPersistence {
    private final List<SensorDataPersistence> sensorDataCollection;

    public SensorDataCollection(List<SensorDataPersistence> sensorDataCollection) {
        this.sensorDataCollection = sensorDataCollection;
    }

    @Override
    public void persist(SensorData sensorData) {
        sensorDataCollection.forEach(persistence -> persistence.persist(sensorData));
    }

    @Override
    public List<SensorData> read() {
        return sensorDataCollection.get(0).read();
    }

    @Override
    public Optional<SensorData> getMostCurrentSensorData(ZonedDateTime lastValidTimestamp) {
        return sensorDataCollection.get(0).getMostCurrentSensorData(lastValidTimestamp);
    }
}
