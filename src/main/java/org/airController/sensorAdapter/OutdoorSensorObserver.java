package org.airController.sensorAdapter;

import org.airController.entities.AirValue;

public interface OutdoorSensorObserver {
    void updateOutdoorAirValue(AirValue outdoorAirValue);

    void runOneLoop();
}
