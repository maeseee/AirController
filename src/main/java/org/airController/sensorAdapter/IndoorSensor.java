package org.airController.sensorAdapter;

public interface IndoorSensor extends Runnable {
    void addObserver(IndoorSensorObserver observer);
}
