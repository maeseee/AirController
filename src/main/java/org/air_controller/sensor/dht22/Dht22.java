package org.air_controller.sensor.dht22;

import org.air_controller.sensorValues.SensorData;

import java.util.Optional;

public interface Dht22 {
    Optional<SensorData> refreshData();
}
