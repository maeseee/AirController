package org.airController.sensor.dht22;

import org.airController.sensorValues.SensorData;

import java.util.Optional;

public interface Dht22 {
    Optional<SensorData> refreshData();
}
