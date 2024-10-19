package org.airController.rules;

import org.airController.sensorValues.CarbonDioxide;
import org.airController.sensorValues.CurrentSensorData;

import java.util.Optional;

public class CO2ControlAirFlow implements Rule {

    private static final double UPPER_LIMIT = 900;
    private static final double LOWER_LIMIT = 500;
    private static final double M = 2.0 / (UPPER_LIMIT - LOWER_LIMIT); // y = xm + b
    private static final double B = 1 - (UPPER_LIMIT * M); // y = xm + b

    private final CurrentSensorData currentIndoorSensorData;

    public CO2ControlAirFlow(CurrentSensorData currentIndoorSensorData) {
        this.currentIndoorSensorData = currentIndoorSensorData;
    }

    @Override
    public String name() {
        return "CO2 air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        Optional<CarbonDioxide> indoorCo2 = currentIndoorSensorData.getCo2();
        if (indoorCo2.isEmpty()) {
            return new Confidence(0.0);
        }
        double impact = M * indoorCo2.get().getPpm() + B;
        return new Confidence(impact);
    }
}
