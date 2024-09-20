package org.airController.sensorAdapter;

import org.airController.controllers.SensorData;

public interface OutdoorSensorObserver {
    void updateOutdoorSensorValue(SensorData outdoorSensorValue);
}
