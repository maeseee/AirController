package org.air_controller.rules;

import lombok.RequiredArgsConstructor;
import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.ClimateDataPoint;

import java.util.Optional;

@RequiredArgsConstructor
class CO2ControlAirFlow implements Rule {

    private static final double UPPER_LIMIT = 1000;
    private static final double LOWER_LIMIT = 400;
    private static final double M = 2.0 / (UPPER_LIMIT - LOWER_LIMIT); // y = xm + b
    private static final double B = -1 - (LOWER_LIMIT * M); // y = xm + b
    private static final double CONFIDENCE_WEIGHT = 1.0;

    private final ClimateSensor indoorSensor;

    @Override
    public String name() {
        return "CO2 air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Optional<CarbonDioxide> indoorCo2 = indoorSensor.getCurrentDataPoint().flatMap(ClimateDataPoint::co2);
        if (indoorCo2.isEmpty()) {
            return new Confidence(0.0, CONFIDENCE_WEIGHT);
        }
        final double impact = M * indoorCo2.get().ppm() + B;
        return new Confidence(impact, CONFIDENCE_WEIGHT);
    }
}
