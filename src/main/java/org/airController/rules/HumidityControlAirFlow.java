package org.airController.rules;

import org.airController.sensorValues.CurrentSensorValues;
import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.Temperature;

import java.util.Optional;

public class HumidityControlAirFlow implements Rule {

    private static final double UPPER_LIMIT = 65.0;
    private static final double LOWER_LIMIT = 40.0;
    private static final double M = 2.0 / (UPPER_LIMIT - LOWER_LIMIT); // y = xm + b
    private static final double B = 1 - (UPPER_LIMIT * M); // y = xm + b

    private final CurrentSensorValues sensorValues;

    public HumidityControlAirFlow(CurrentSensorValues sensorValues) {
        this.sensorValues = sensorValues;
    }

    @Override
    public String name() {
        return "Humidity air flow control";
    }

    @Override
    public Percentage turnOn() {
        Optional<Humidity> indoorHumidity = sensorValues.getIndoorHumidity();
        Optional<Temperature> indoorTemperature = sensorValues.getIndoorTemperature();
        if (indoorHumidity.isEmpty() || indoorTemperature.isEmpty()) {
            return new Percentage(0.0);
        }
        double impact = M * indoorHumidity.get().getRelativeHumidity(indoorTemperature.get()) + B;
        double sign = sensorValues.isIndoorHumidityAboveOutdoorHumidity() ? 1.0 : -1.0;
        return new Percentage(impact * sign);
    }
}
