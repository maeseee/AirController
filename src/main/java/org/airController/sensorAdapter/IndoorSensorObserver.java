package org.airController.sensorAdapter;

import org.airController.controllers.SensorValue;

public interface IndoorSensorObserver {
    void updateIndoorSensorValue(SensorValue indoorSensorValue);
}
