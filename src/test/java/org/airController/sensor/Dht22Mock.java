package org.airController.sensor;

import org.airController.entities.AirVO;
import org.airController.sensorAdapter.SensorValue;

public class Dht22Mock implements Dht22 {

    private AirVO data;

    @Override
    public SensorValue refreshData() {
        return new SensorValueImpl(data);
    }

    public void setData(AirVO data) {
        this.data = data;
    }
}
