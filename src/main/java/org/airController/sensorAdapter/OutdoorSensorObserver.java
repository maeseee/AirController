package org.airController.sensorAdapter;

import org.airController.entities.AirValue;

import java.time.LocalDateTime;

public interface OutdoorSensorObserver {
    void updateOutdoorAirValue(AirValue outdoorAirValue);

    void runOneLoop();
}
