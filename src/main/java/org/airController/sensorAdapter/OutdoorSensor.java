package org.airController.sensorAdapter;

public interface OutdoorSensor extends Runnable {
    void addObserver(OutdoorSensorObserver observer);
}
