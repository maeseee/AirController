package org.airController.sensor;

import org.airController.sensorAdapter.SensorValue;

public interface Dht22 {
    SensorValue refreshData();
}
