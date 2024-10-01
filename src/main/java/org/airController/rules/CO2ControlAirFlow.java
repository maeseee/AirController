package org.airController.rules;

import org.airController.sensorValues.CarbonDioxide;
import org.airController.sensorValues.CurrentSensorValues;

import java.util.Optional;

public class CO2ControlAirFlow implements Rule {

    private static final double UPPER_LIMIT = 1100;
    private static final double LOWER_LIMIT = 500;
    private static final double M = 2.0 / (UPPER_LIMIT - LOWER_LIMIT); // y = xm + b
    private static final double B = 1 - (UPPER_LIMIT * M); // y = xm + b

    private final CurrentSensorValues sensorValues;

    public CO2ControlAirFlow(CurrentSensorValues sensorValues) {
        this.sensorValues = sensorValues;
    }

    @Override
    public String name() {
        return "CO2 air flow control";
    }

    @Override
    public Confident turnOnConfident() {
        Optional<CarbonDioxide> indoorCo2 = sensorValues.getIndoorCo2();
        if (indoorCo2.isEmpty()) {
            return new Confident(0.0);
        }
        double impact = M * indoorCo2.get().getPpm() + B;
        return new Confident(impact);
    }
}
