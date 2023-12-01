package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.sensorAdapter.IndoorSensorObserver;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class IndoorSensorTest {

    public static void main(String[] args) throws IOException {
        final Dht22Impl dht22 = new Dht22Impl();
        final IndoorSensorImpl indoorSensor = new IndoorSensorImpl(dht22);
        indoorSensor.addObserver(new IndoorSensorObserver() {
            @Override
            public void updateIndoorAirValue(AirValue indoorAirValue) {
                System.out.println(indoorAirValue);
            }

            @Override
            public void runOneLoop() {
            }
        });
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(indoorSensor, 0, 10, TimeUnit.SECONDS);
    }
}