package org.air_controller.sensor;

import org.air_controller.sensor_values.ClimateSensors;

public class ClimateSensorsFactory {

    public ClimateSensors build() {
        final SensorFactory indoorSensorFactory = new IndoorSensorFactory();
        final SensorFactory outdoorSensorFactory = new OutdoorSensorFactory();
        return new ClimateSensors(indoorSensorFactory.build(), outdoorSensorFactory.build());
    }
}
