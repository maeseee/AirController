package org.airController.sensor;

import org.airController.entities.AirVO;

import java.util.Optional;

public interface Dht22 {

    Optional<AirVO> refreshData();
}
