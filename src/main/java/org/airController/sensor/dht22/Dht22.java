package org.airController.sensor.dht22;

import org.airController.controllers.SensorData;

import java.util.Optional;

public interface Dht22 {
    Optional<SensorData> refreshData();
}
