package org.airController.persistence;

import org.airController.controllers.SensorData;

public interface SensorValuePersistence {

    void persist(SensorData sensorData);
}
