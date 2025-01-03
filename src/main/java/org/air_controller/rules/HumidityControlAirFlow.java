package org.air_controller.rules;

import org.air_controller.sensor.SensorException;
import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.Temperature;

import java.util.Optional;

public class HumidityControlAirFlow implements Rule {

    private static final Temperature REFERENCE_TEMPERATURE;
    private static final Humidity UPPER_HUMIDITY;
    private static final Humidity IDEAL_HUMIDITY;

    static {
        try {
            REFERENCE_TEMPERATURE = Temperature.createFromCelsius(22.0);
            UPPER_HUMIDITY = Humidity.createFromRelative(65.0, REFERENCE_TEMPERATURE);
            IDEAL_HUMIDITY = Humidity.createFromRelative(52.5, REFERENCE_TEMPERATURE);
        } catch (InvalidArgumentException e) {
            throw new SensorException("Invalid sensor values", e.getCause());
        }
    }

    private static final double HUMIDITY_FACTOR = UPPER_HUMIDITY.getAbsoluteHumidity() - IDEAL_HUMIDITY.getAbsoluteHumidity();

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
        final double indoorConfidence = Math.abs(indoorHumidity.getAbsoluteHumidity() - IDEAL_HUMIDITY.getAbsoluteHumidity());
        final double outdoorCorrectionFactor = calculateOutdoorCorrectionFactor(indoorHumidity, outdoorHumidity);
        return new Confidence(indoorConfidence * outdoorCorrectionFactor / HUMIDITY_FACTOR);
    }

    private double calculateOutdoorCorrectionFactor(Humidity indoorHumidity, Humidity outdoorHumidity) {
        final double sign = Math.signum(indoorHumidity.getAbsoluteHumidity() - IDEAL_HUMIDITY.getAbsoluteHumidity());
        return (indoorHumidity.getAbsoluteHumidity() - outdoorHumidity.getAbsoluteHumidity()) * sign;
    }
}
