package org.airController.sensor.dht22;

import org.airController.entities.AirValue;

import java.util.Optional;

public interface Dht22 {
    Optional<AirValue> refreshData();
}