package org.airController.sensor;

public interface OutdoorSensor extends Runnable {
    void addObserver(OutdoorSensorObserver observer);
}
