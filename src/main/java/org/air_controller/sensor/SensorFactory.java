package org.air_controller.sensor;

import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;

public abstract class SensorFactory {

    public ClimateSensor build(ClimateDataPointPersistence persistence) {
        return createSensor(persistence);
    }

    protected abstract ClimateSensor createSensor(ClimateDataPointPersistence persistence);
}
