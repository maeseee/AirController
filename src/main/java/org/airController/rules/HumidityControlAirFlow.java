package org.airController.rules;

import org.airController.controllers.Rule;
import org.airController.controllers.SensorValues;
import org.airController.entities.Humidity;

class HumidityControlAirFlow implements Rule {

    private static final double UPPER_LIMIT = 60.0;
    private static final double LOWER_LIMIT = 44.0;
    private static final double M = 2.0 / (UPPER_LIMIT - LOWER_LIMIT); // y = xm + b
    private static final double B = 1 - (UPPER_LIMIT * M); // y = xm + b

    private final SensorValues sensorValues;

    public HumidityControlAirFlow(SensorValues sensorValues) {
        this.sensorValues = sensorValues;
    }

    @Override
    public String name() {
        return "Humidity air flow control";
    }

    @Override
    public Percentage turnOn() {
        Humidity indoorHumidity = sensorValues.getIndoorHumidity();
        double impact = M * indoorHumidity.getRelativeHumidity() + B;
        double sign = sensorValues.isIndoorHumidityAboveOutdoorHumidity() ? 1 : -1;
        return new Percentage(impact * sign);
    }
}
