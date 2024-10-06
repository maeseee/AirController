package org.airController.persistence;

import org.airController.sensorValues.SensorData;

import java.util.List;

public interface SensorDataPersistence {

    void persist(SensorData sensorData);

    List<SensorData> read();
}
