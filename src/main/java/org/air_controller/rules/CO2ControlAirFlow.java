package org.air_controller.rules;

import lombok.RequiredArgsConstructor;
import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.CurrentSensorData;

import java.util.Optional;

@RequiredArgsConstructor
class CO2ControlAirFlow implements Rule {

    private static final double UPPER_LIMIT = 1000;
    private static final double LOWER_LIMIT = 400;
    private static final double M = 2.0 / (UPPER_LIMIT - LOWER_LIMIT); // y = xm + b
    private static final double B = -1 - (LOWER_LIMIT * M); // y = xm + b

    private final CurrentSensorData currentIndoorSensorData;

    @Override
    public String name() {
        return "CO2 air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Optional<CarbonDioxide> indoorCo2 = currentIndoorSensorData.getCo2();
        if (indoorCo2.isEmpty()) {
            return new Confidence(0.0);
        }
        final double impact = M * indoorCo2.get().ppm() + B;
        return new Confidence(impact);
    }
}
