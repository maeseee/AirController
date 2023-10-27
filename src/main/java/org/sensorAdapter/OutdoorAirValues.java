package org.sensorAdapter;

import org.entities.AirVO;
import org.entities.Humidity;
import org.entities.Temperature;

import java.util.Optional;

public class OutdoorAirValues implements SensorValues {

    private final AirVO airValues;

    public OutdoorAirValues(Temperature temperature, Humidity humidity) {
        this(new AirVO(temperature, humidity));
    }

    public OutdoorAirValues(AirVO airValues) {
        this.airValues = airValues;
    }

    @Override
    public Optional<AirVO> getAirValues() {
        return Optional.ofNullable(airValues);
    }

    @Override
    public String toString() {
        return airValues.toString();
    }
}
