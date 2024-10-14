package org.airController.sensor;

import org.airController.sensorValues.SensorData;

public interface SensorObserver {
    void updateSensorData(SensorData sensorData);
}
