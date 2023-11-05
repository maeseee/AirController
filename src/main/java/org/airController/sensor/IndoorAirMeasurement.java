package org.airController.sensor;

import org.airController.sensorAdapter.IndoorAirMeasurementObserver;
import org.airController.sensorAdapter.IndoorAirValues;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    public static void main(String[] args) {
        final IndoorAirMeasurement indoorAirMeasurement = new IndoorAirMeasurement();
        indoorAirMeasurement.addObserver(System.out::println);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(indoorAirMeasurement, 0, 10, TimeUnit.SECONDS);
    }
}
