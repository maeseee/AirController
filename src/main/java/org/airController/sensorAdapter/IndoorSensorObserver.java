package org.airController.sensorAdapter;

import org.airController.entities.AirValue;

public interface IndoorSensorObserver {
    void updateIndoorSensorValue(AirValue indoorAirValue);
}
