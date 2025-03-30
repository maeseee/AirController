package org.air_controller.sensor.dht22;

import org.air_controller.sensor_data_persistence.SensorDataPersistence;
import org.air_controller.sensor_data_persistence.SensorDataPrinter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Dht22Main {
    public static void main(String[] args) throws IOException {
        final Dht22Impl dht22 = new Dht22Impl();
        final SensorDataPersistence persistence = new SensorDataPrinter();
        final OneWireSensor indoorSensor = new OneWireSensor(persistence, dht22);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(indoorSensor, 0, 10, TimeUnit.SECONDS);
    }
}
