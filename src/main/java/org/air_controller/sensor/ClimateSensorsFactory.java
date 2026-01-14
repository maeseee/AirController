package org.air_controller.sensor;

import org.air_controller.ApplicationPersistence;
import org.air_controller.sensor_values.ClimateSensors;

public class ClimateSensorsFactory {

    public ClimateSensors build(ApplicationPersistence persistence) {
        final SensorFactory indoorSensorFactory = new IndoorSensorFactory();
        final SensorFactory outdoorSensorFactory = new OutdoorSensorFactory();
        return new ClimateSensors(
                indoorSensorFactory.build(persistence.getClimateSensorAccessors().indoor()),
                outdoorSensorFactory.build(persistence.getClimateSensorAccessors().outdoor()));
    }
}
