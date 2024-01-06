package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OneWireSensor implements IndoorSensor {
    private static final Logger logger = LogManager.getLogger(OneWireSensor.class);

    private final List<IndoorSensorObserver> observers = new ArrayList<>();
    private final Dht22 dht22;

    public OneWireSensor() throws IOException {
        this.dht22 = new Dht22Impl();
    }

    public OneWireSensor(Dht22 dht22) {
        this.dht22 = dht22;
    }

    @Override
    public void run() {
        final Optional<AirValue> indoorAirValue = dht22.refreshData();
        indoorAirValue.ifPresentOrElse(
                this::notifyObservers,
                () -> logger.error("Indoor sensor out of order"));
    }

    @Override
    public void addObserver(IndoorSensorObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(AirValue indoorAirValue) {
        logger.info("New indoor sensor value: " + indoorAirValue);
        observers.forEach(observer -> observer.updateIndoorAirValue(indoorAirValue));
    }

    public static void main(String[] args) throws IOException {
        final Dht22Impl dht22 = new Dht22Impl();
        final OneWireSensor indoorSensor = new OneWireSensor(dht22);
        indoorSensor.addObserver(System.out::println);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(indoorSensor, 0, 10, TimeUnit.SECONDS);
    }
}
