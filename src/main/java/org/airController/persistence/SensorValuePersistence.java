package org.airController.persistence;

import org.airController.entities.AirValue;

public interface SensorValuePersistence {

    void persist(AirValue value);
}
