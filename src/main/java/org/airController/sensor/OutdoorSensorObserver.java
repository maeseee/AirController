package org.airController.sensor;

import org.airController.controllers.SensorData;

public interface OutdoorSensorObserver {
    void updateOutdoorSensorData(SensorData sensorData);
}
