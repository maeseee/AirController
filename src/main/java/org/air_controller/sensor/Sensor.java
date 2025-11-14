package org.air_controller.sensor;

import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;

public interface Sensor extends Runnable {
    ClimateDataPointPersistence getPersistence();
}
