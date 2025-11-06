package org.air_controller.sensor;

public class SensorsFactory {

    public Sensors build() {
        final SensorFactory indoorSensorFactory = new IndoorSensorFactory();
        final SensorFactory outdoorSensorFactory = new OutdoorSensorFactory();
        return new Sensors(indoorSensorFactory.build(), outdoorSensorFactory.build());
    }
}
