package org.air_controller.sensor;

import org.air_controller.sensorDataPersistence.SensorDataPersistence;

public interface Sensor extends Runnable {
    SensorDataPersistence getPersistence();
}
