package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.SensorValue;

import java.util.Optional;

public class SensorValueImpl implements SensorValue {

    private final AirValue airValues;

    public SensorValueImpl(Temperature temperature, Humidity humidity) {
        this(new AirValue(temperature, humidity));
    }

    public SensorValueImpl(AirValue airValues) {
        this.airValues = airValues;
    }

    @Override
    public Optional<AirValue> getValue() {
        return Optional.ofNullable(airValues);
    }

    @Override
    public String toString() {
        return airValues.toString();
    }
}
