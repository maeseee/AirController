package org.airController.persistence;

import org.airController.sensorValues.SensorData;

import java.util.List;

public class PersistenceConverter {
    public static void convert(SensorDataPersistence origin, SensorDataPersistence target) {
        final List<SensorData> sensorData = origin.read();
        sensorData.forEach(target::persist);
    }
}
