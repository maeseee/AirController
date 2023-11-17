package org.airController.sensorAdapter;

import org.airController.entities.AirVO;

import java.util.Optional;

public interface SensorValue {
    Optional<AirVO> getValue();
}
