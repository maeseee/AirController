package org.sensors;

import org.entities.AirVO;

import java.util.Optional;

public interface SensorValues {
    Optional<AirVO> getAirValues();
}
