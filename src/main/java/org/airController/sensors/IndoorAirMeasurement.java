package org.airController.sensors;

import org.airController.sensorAdapter.IndoorAirMeasurementObserver;
import org.airController.sensorAdapter.IndoorAirValues;

import java.util.ArrayList;
import java.util.List;

public class IndoorAirMeasurement implements Runnable {

    private final List<IndoorAirMeasurementObserver> observers = new ArrayList<>();

    @Override
    public void run() {
        // TODO
        notifyObservers(new IndoorAirValues(null));
    }

    public void addObserver(IndoorAirMeasurementObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(IndoorAirValues indoorAirValues) {
        observers.forEach(observer -> observer.updateAirMeasurement(indoorAirValues));
    }
}
