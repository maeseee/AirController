package org.sensors;

import org.entities.AirValues;
import org.entities.Humidity;
import org.entities.Temperature;

import java.util.Optional;

public class OutdoorAirValues implements SensorValues {

    private final AirValues airValues;

    public OutdoorAirValues(Temperature temperature, Humidity humidity) {
        this(new AirValues(temperature, humidity));
    }

    public OutdoorAirValues(AirValues airValues) {
        this.airValues = airValues;
    }

    @Override
    public Optional<AirValues> getAirValues() {
        return Optional.ofNullable(airValues);
    }

    @Override
    public String toString() {
        return airValues.toString();
    }
}
