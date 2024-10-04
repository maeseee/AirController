package org.airController.persistence;

import org.airController.sensorValues.SensorData;

public interface SensorDataPersistence {

    void persist(SensorData sensorData);
}
