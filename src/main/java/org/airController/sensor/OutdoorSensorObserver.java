package org.airController.sensor;

import org.airController.sensorValues.SensorData;

public interface OutdoorSensorObserver {
    void updateOutdoorSensorData(SensorData sensorData);
}
