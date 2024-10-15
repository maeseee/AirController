package org.airController.persistence;

import org.airController.sensorValues.SensorData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorDataPersistence {

    void persist(SensorData sensorData);

    List<SensorData> read();

    Optional<SensorData> getMostCurrentSensorData(LocalDateTime lastValidTimestamp);
}
