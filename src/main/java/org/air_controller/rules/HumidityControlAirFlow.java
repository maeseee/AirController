package org.air_controller.rules;

import org.air_controller.sensor.SensorException;
import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.Temperature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

class HumidityControlAirFlow implements Rule {
    private static final Logger logger = LogManager.getLogger(HumidityControlAirFlow.class);

    private static final double UPPER_RELATIV_HUMIDITY = 65.0;
    private static final double IDEAL_RELATIV_HUMIDITY = 52.5;

    private final CurrentSensorData currentIndoorSensorData;
    private final CurrentSensorData currentOutdoorSensorData;

    public HumidityControlAirFlow(CurrentSensorData currentIndoorSensorData, CurrentSensorData currentOutdoorSensorData) {
        this.currentIndoorSensorData = currentIndoorSensorData;
        this.currentOutdoorSensorData = currentOutdoorSensorData;
    }

    @Override
    public String name() {
        return "Humidity air flow control";
    }

    @Override
    public Confidence turnOnConfidence() {
        final Optional<Humidity> indoorHumidity = currentIndoorSensorData.getHumidity();
        final Optional<Humidity> outdoorHumidity = currentOutdoorSensorData.getHumidity();
        if (indoorHumidity.isEmpty() || outdoorHumidity.isEmpty()) {
            return new Confidence(0.0);
        }
        return calculateConfidence(indoorHumidity.get(), outdoorHumidity.get());
    }

    private Confidence calculateConfidence(Humidity indoorHumidity, Humidity outdoorHumidity) {
        final double indoorToIdealDiff = indoorHumidity.getAbsoluteHumidity() - createHumidity(IDEAL_RELATIV_HUMIDITY).getAbsoluteHumidity();
        final double confidenceBase =
                createHumidity(UPPER_RELATIV_HUMIDITY).getAbsoluteHumidity() - createHumidity(IDEAL_RELATIV_HUMIDITY).getAbsoluteHumidity();
        final double indoorConfidence = indoorToIdealDiff / confidenceBase;
        final double indoorToOutdoorDiff = indoorHumidity.getAbsoluteHumidity() - outdoorHumidity.getAbsoluteHumidity();
        final double outdoorCorrectionFactor = indoorToOutdoorDiff / confidenceBase;
        return new Confidence(indoorConfidence * outdoorCorrectionFactor);
    }

    private Humidity createHumidity(double relativeHumidity) {
        try {
            final Temperature currentIndoorTemperature = currentIndoorSensorData.getTemperature().orElse(Temperature.createFromCelsius(22.5));
            return Humidity.createFromRelative(relativeHumidity, currentIndoorTemperature);
        } catch (InvalidArgumentException e) {
            logger.error("Ideal humidity could not be created", e);
            throw new SensorException("Invalid sensor values", e.getCause());
        }
    }
}
