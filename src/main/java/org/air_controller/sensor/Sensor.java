package org.air_controller.sensor;

import org.air_controller.sensor_data_persistence.SensorDataPersistence;

public interface Sensor extends Runnable {
    SensorDataPersistence getPersistence();
}
