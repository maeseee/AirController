package org.air_controller.rules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor.SensorException;
import org.air_controller.sensor_values.*;

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
            return new Confidence(0.0, CONFIDENCE_WEIGHT);
        }
        return getConfidence(indoorDataPoint.get(), outdoorDataPoint.get());
    }

    private Confidence getConfidence(ClimateDataPoint indoorDataPoint, ClimateDataPoint outdoorClimateDataPoint) {
        final double indoorToIdealDiff =
                indoorDataPoint.humidity().absoluteHumidity() - absoluteHumidityFrom(IDEAL_RELATIV_HUMIDITY, indoorDataPoint);
        final double indoorToOutdoorDiff =
                indoorDataPoint.humidity().absoluteHumidity() - outdoorClimateDataPoint.humidity().absoluteHumidity();
        return calculateConfidence(indoorToIdealDiff, indoorToOutdoorDiff, indoorDataPoint);
    }

    private Confidence calculateConfidence(double indoorToIdealDiff, double indoorToOutdoorDiff, ClimateDataPoint indoorDataPoint) {
        final double confidenceBase = absoluteHumidityFrom(UPPER_RELATIV_HUMIDITY, indoorDataPoint) -
                absoluteHumidityFrom(IDEAL_RELATIV_HUMIDITY, indoorDataPoint);
        final double indoorConfidence = indoorToIdealDiff / confidenceBase;
        final double outdoorCorrectionFactor = indoorToOutdoorDiff / confidenceBase;
        return new Confidence(indoorConfidence * outdoorCorrectionFactor, CONFIDENCE_WEIGHT);
    }

    private double absoluteHumidityFrom(double relativeHumidity, ClimateDataPoint indoorDataPoint) {
        try {
            final Temperature currentIndoorTemperature = indoorDataPoint.temperature();
            return Humidity.createFromRelative(relativeHumidity, currentIndoorTemperature).absoluteHumidity();
        } catch (InvalidArgumentException e) {
            log.error("Ideal humidity could not be created", e);
            throw new SensorException("Invalid sensor values", e.getCause());
        }
    }
}
