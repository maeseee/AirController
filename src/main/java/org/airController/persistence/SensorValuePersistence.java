package org.airController.persistence;

import org.airController.entities.AirValue;

import java.time.LocalDateTime;

public interface SensorValuePersistence {

    void persist(LocalDateTime time, AirValue value);
}
