package org.airController.sensorAdapter;

public interface OutdoorAirMeasurementObserver {
    void updateAirMeasurement(OutdoorAirValues outdoorAirValues);
}
