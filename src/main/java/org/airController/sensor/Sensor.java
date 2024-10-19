package org.airController.sensor;

import org.airController.sensorDataPersistence.SensorDataPersistence;

public interface Sensor extends Runnable {
    SensorDataPersistence getPersistence();
}
