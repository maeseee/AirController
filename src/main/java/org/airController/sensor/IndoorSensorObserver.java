package org.airController.sensor;

import org.airController.controllers.SensorData;

public interface IndoorSensorObserver {
    void updateIndoorSensorData(SensorData indoorSensorData);
}
