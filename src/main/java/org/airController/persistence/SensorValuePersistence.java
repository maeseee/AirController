package org.airController.persistence;

import org.airController.controllers.SensorValue;

public interface SensorValuePersistence {

    void persist(SensorValue value);
}
