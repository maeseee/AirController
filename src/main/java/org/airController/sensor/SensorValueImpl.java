package org.airController.sensor;

import org.airController.entities.AirVO;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.SensorValue;

import java.util.Optional;

public class SensorValueImpl implements SensorValue {

    private final AirVO airValues;

    public SensorValueImpl(Temperature temperature, Humidity humidity) {
        this(new AirVO(temperature, humidity));
    }

    public SensorValueImpl(AirVO airValues) {
        this.airValues = airValues;
    }

    @Override
    public Optional<AirVO> getValue() {
        return Optional.ofNullable(airValues);
    }

    @Override
    public String toString() {
        return airValues.toString();
    }
}
