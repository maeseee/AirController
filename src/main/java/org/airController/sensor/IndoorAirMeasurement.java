package org.airController.sensor;

import org.airController.entities.AirVO;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.sensorAdapter.IndoorAirMeasurementObserver;
import org.airController.sensorAdapter.IndoorAirValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IndoorAirMeasurement implements Runnable {

    private final List<IndoorAirMeasurementObserver> observers = new ArrayList<>();
    private final Dht22 dht22;

    public IndoorAirMeasurement(Dht22 dht22) {
        this.dht22 = dht22;
    }

    @Override
    public void run() {
        final Optional<AirVO> indoorAirValues = dht22.refreshData();
        indoorAirValues.ifPresent(airVO -> notifyObservers(new IndoorAirValues(airVO)));
    }

    public void addObserver(IndoorAirMeasurementObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(IndoorAirValues indoorAirValues) {
        observers.forEach(observer -> observer.updateAirMeasurement(indoorAirValues));
    }

    public static void main(String[] args) throws IOException {
        final Dht22 dht22 = new Dht22(GpioFunction.DHT22_SENSOR);
        final IndoorAirMeasurement indoorAirMeasurement = new IndoorAirMeasurement(dht22);
        indoorAirMeasurement.addObserver(System.out::println);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(indoorAirMeasurement, 0, 10, TimeUnit.SECONDS);
    }
}
