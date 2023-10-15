package org.sensors;

import org.entities.AirValues;
import org.entities.Humidity;
import org.entities.Temperature;

public class IndoorAirValues implements SensorValues {

    private final AirValues airValues;

    public IndoorAirValues(AirValues airValues) {
        this.airValues = airValues;
    }

    public IndoorAirValues(Humidity humidity, Temperature temperature) {
        this.airValues = new AirValues(humidity, temperature);
    }

    @Override
    public AirValues getAirValues() {
        return airValues;
    }

    @Override
    public String toString() {
        return airValues.toString();
    }
}
