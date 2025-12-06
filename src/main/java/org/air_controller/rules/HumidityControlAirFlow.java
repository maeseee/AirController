package org.air_controller.rules;

import lombok.RequiredArgsConstructor;
import org.air_controller.sensor.SensorException;
import org.air_controller.sensor_values.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@RequiredArgsConstructor
class HumidityControlAirFlow implements Rule {
    private static final Logger logger = LogManager.getLogger(HumidityControlAirFlow.class);

    private static final double UPPER_RELATIV_HUMIDITY = 65.0;
    private static final double IDEAL_RELATIV_HUMIDITY = 52.0;

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
            return new Confidence(0.0);
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
        return new Confidence(indoorConfidence * outdoorCorrectionFactor);
    }

    private double absoluteHumidityFrom(double relativeHumidity, ClimateDataPoint indoorDataPoint) {
        try {
            final Temperature currentIndoorTemperature = indoorDataPoint.temperature();
            return Humidity.createFromRelative(relativeHumidity, currentIndoorTemperature).absoluteHumidity();
        } catch (InvalidArgumentException e) {
            logger.error("Ideal humidity could not be created", e);
            throw new SensorException("Invalid sensor values", e.getCause());
        }
    }
}
