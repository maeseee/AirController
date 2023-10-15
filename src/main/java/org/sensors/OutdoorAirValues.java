package org.sensors;

import org.entities.AirValues;
import org.entities.Humidity;
import org.entities.Temperature;

public class OutdoorAirValues implements SensorValues {

    private final AirValues airValues;

    public OutdoorAirValues(AirValues airValues) {
        this.airValues = airValues;
    }

    public OutdoorAirValues(Humidity humidity, Temperature temperature) {
        this.airValues = new AirValues(temperature, humidity);
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
