package org.airController.sensor;

import org.airController.sensorValues.SensorData;

public interface IndoorSensorObserver {
    void updateIndoorSensorData(SensorData indoorSensorData);
}
