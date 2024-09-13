package org.airController.sensorAdapter;

import org.airController.controllers.SensorValue;

public interface OutdoorSensorObserver {
    void updateOutdoorSensorValue(SensorValue outdoorSensorValue);
}
