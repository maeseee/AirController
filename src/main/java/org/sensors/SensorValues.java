package org.sensors;

import org.entities.AirValues;

import java.util.Optional;

public interface SensorValues {
    Optional<AirValues> getAirValues();
}
