package org.airController.sensors;

import org.airController.sensorAdapter.IndoorAirMeasurementObserver;
import org.airController.sensorAdapter.IndoorAirValues;

import java.util.ArrayList;
import java.util.List;

public class IndoorAirMeasurement {

    private final List<IndoorAirMeasurementObserver> observers = new ArrayList<>();

    public void addObserver(IndoorAirMeasurementObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(IndoorAirValues indoorAirValues) {
        observers.forEach(observer -> observer.updateAirMeasurement(indoorAirValues));
    }
}
