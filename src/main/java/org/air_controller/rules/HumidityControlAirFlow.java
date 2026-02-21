package org.air_controller.rules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateSensors;
import org.air_controller.sensor_values.Temperature;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
class HumidityControlAirFlow implements Rule {
    private static final double UPPER_RELATIV_HUMIDITY = 65.0;
    private static final double IDEAL_RELATIV_HUMIDITY = 52.0;
    private static final double CONFIDENCE_WEIGHT = 0.8;

    private final ClimateSensors sensors;

    @Override
    public String name() {
        return "Humidity air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Optional<ClimateDataPoint> indoorDataPoint = sensors.indoor().getCurrentDataPoint();
        final Optional<ClimateDataPoint> outdoorDataPoint = sensors.outdoor().getCurrentDataPoint();
        if (indoorDataPoint.isEmpty() || outdoorDataPoint.isEmpty()) {
            return Confidence.createEmpty();
        }
        return getConfidence(indoorDataPoint.get(), outdoorDataPoint.get());
    }

    private Confidence getConfidence(ClimateDataPoint indoorDataPoint, ClimateDataPoint outdoorClimateDataPoint) {
        final Temperature indoorTemperature = indoorDataPoint.temperature();
        final double indoorHumidity = indoorDataPoint.humidity().getRelativeHumidity(indoorTemperature);
        final double changePotential = toConfidence(indoorHumidity - IDEAL_RELATIV_HUMIDITY);
        final double outdoorHumidityOnIndoorTemperature = outdoorClimateDataPoint.humidity().getRelativeHumidity(indoorTemperature);
        final double changePower = toConfidence(indoorHumidity - outdoorHumidityOnIndoorTemperature);
        return Confidence.createWeighted(changePotential * changePower, CONFIDENCE_WEIGHT);
    }

    private double toConfidence(double differenceToIdeal) {
        return differenceToIdeal / (UPPER_RELATIV_HUMIDITY - IDEAL_RELATIV_HUMIDITY);
    }
}
