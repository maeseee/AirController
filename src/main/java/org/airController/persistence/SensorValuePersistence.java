package org.airController.persistence;

import org.airController.sensorValues.SensorData;

public interface SensorValuePersistence {

    void persist(SensorData sensorData);
}
