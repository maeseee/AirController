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
    private static final double IDEAL_RELATIV_HUMIDITY = 52.5;

    private final CurrentSensorData currentIndoorSensorData;
    private final CurrentSensorData currentOutdoorSensorData;

    @Override
    public String name() {
        return "Humidity air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Optional<SensorData> indoorSensorData = currentIndoorSensorData.getCurrentSensorData();
        final Optional<SensorData> outdoorSensorData = currentOutdoorSensorData.getCurrentSensorData();
        if (indoorSensorData.isEmpty() || outdoorSensorData.isEmpty()) {
            return new Confidence(0.0);
        }
        return getConfidence(indoorSensorData.get(), outdoorSensorData.get());
    }

    private Confidence getConfidence(SensorData indoorSensorData, SensorData outdoorSensorData) {
        final double indoorToIdealDiff = indoorSensorData.humidity().absoluteHumidity() - absoluteHumidityFrom(IDEAL_RELATIV_HUMIDITY,
                indoorSensorData);
        final double indoorToOutdoorDiff = indoorSensorData.humidity().absoluteHumidity() - outdoorSensorData.humidity().absoluteHumidity();
        return calculateConfidence(indoorToIdealDiff, indoorToOutdoorDiff, indoorSensorData);
    }

    private Confidence calculateConfidence(double indoorToIdealDiff, double indoorToOutdoorDiff, SensorData indoorSensorData) {
        final double confidenceBase =
                absoluteHumidityFrom(UPPER_RELATIV_HUMIDITY, indoorSensorData) - absoluteHumidityFrom(IDEAL_RELATIV_HUMIDITY, indoorSensorData);
        final double indoorConfidence = indoorToIdealDiff / confidenceBase;
        final double outdoorCorrectionFactor = indoorToOutdoorDiff / confidenceBase;
        return new Confidence(indoorConfidence * outdoorCorrectionFactor);
    }

    private double absoluteHumidityFrom(double relativeHumidity, SensorData indoorSensorData) {
        try {
            final Temperature currentIndoorTemperature = indoorSensorData.temperature();
            return Humidity.createFromRelative(relativeHumidity, currentIndoorTemperature).absoluteHumidity();
        } catch (InvalidArgumentException e) {
            logger.error("Ideal humidity could not be created", e);
            throw new SensorException("Invalid sensor values", e.getCause());
        }
    }
}
