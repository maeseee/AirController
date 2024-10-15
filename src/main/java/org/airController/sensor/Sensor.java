package org.airController.sensor;

import org.airController.persistence.SensorDataPersistence;

public interface Sensor extends Runnable {
    SensorDataPersistence getPersistence();
}
