package org.airController.sensorAdapter;

import org.airController.entities.AirValue;

import java.util.Optional;

public interface SensorValue {
    Optional<AirValue> getValue();
}
