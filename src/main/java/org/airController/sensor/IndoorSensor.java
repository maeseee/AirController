package org.airController.sensor;

public interface IndoorSensor extends Runnable {
    void addObserver(IndoorSensorObserver observer);
}
