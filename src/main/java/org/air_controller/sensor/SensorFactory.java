package org.air_controller.sensor;

public abstract class SensorFactory {

    public ClimateSensor build() {
        return createSensor();
    }

    protected abstract ClimateSensor createSensor();
}
