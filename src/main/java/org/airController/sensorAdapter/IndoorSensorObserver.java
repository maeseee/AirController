package org.airController.sensorAdapter;

import org.airController.entities.AirValue;

public interface IndoorSensorObserver {
    void updateIndoorAirValue(AirValue indoorAirValue);

    void runOneLoop();
}
