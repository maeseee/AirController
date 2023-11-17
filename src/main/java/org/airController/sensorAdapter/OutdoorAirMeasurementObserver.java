package org.airController.sensorAdapter;

public interface OutdoorAirMeasurementObserver {
    void updateOutdoorSensorValue(SensorValue outdoorSensorValue);
}
