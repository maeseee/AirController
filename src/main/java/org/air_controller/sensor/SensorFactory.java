package org.air_controller.sensor;

public abstract class SensorFactory {

    public Sensor build() {
        return createSensor();
    }

    protected abstract Sensor createSensor();
}
