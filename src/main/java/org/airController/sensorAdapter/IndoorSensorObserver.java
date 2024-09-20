package org.airController.sensorAdapter;

import org.airController.controllers.SensorData;

public interface IndoorSensorObserver {
    void updateIndoorSensorData(SensorData indoorSensorData);
}
