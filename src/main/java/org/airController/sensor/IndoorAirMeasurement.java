package org.airController.sensor;

import org.airController.gpioAdapter.GpioFunction;
import org.airController.sensorAdapter.IndoorAirMeasurementObserver;
import org.airController.sensorAdapter.SensorValue;
import org.airController.util.Logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IndoorAirMeasurement implements Runnable {
    private final List<IndoorAirMeasurementObserver> observers = new ArrayList<>();
    private final Dht22 dht22;

    public IndoorAirMeasurement() throws IOException {
        this.dht22 = new Dht22Impl(GpioFunction.DHT22_SENSOR);
    }

    public IndoorAirMeasurement(Dht22 dht22) {
        this.dht22 = dht22;
    }

    @Override
    public void run() {
        final SensorValue indoorSensorValue = dht22.refreshData();
        indoorSensorValue.getValue().ifPresentOrElse(
                airVO -> notifyObservers(indoorSensorValue),
                () -> Logging.getLogger().severe("Indoor sensor out of order"));
    }

    public void addObserver(IndoorAirMeasurementObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(SensorValue indoorSensorValue) {
        Logging.getLogger().info("New indoor sensor value: " + indoorSensorValue);
        observers.forEach(observer -> observer.updateIndoorSensorValue(indoorSensorValue));
    }

    public static void main(String[] args) throws IOException {
        final Dht22Impl dht22 = new Dht22Impl(GpioFunction.DHT22_SENSOR);
        final IndoorAirMeasurement indoorAirMeasurement = new IndoorAirMeasurement(dht22);
        indoorAirMeasurement.addObserver(System.out::println);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(indoorAirMeasurement, 0, 10, TimeUnit.SECONDS);
    }
}
