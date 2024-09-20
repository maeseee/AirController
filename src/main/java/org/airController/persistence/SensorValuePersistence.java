package org.airController.persistence;

import org.airController.controllers.SensorData;
import org.airController.controllers.SensorValue;

public interface SensorValuePersistence {

    void persist(SensorValue value); // TODO delete after refactoring

    void persist(SensorData sensorData);
}
