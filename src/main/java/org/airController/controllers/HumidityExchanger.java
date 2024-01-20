package org.airController.controllers;

import org.airController.entities.Humidity;

import java.io.IOException;

class HumidityExchanger {

    private static final Humidity LOWER_TARGET_HUMIDITY;
    private static final Humidity UPPER_TARGET_HUMIDITY;

    static {
        try {
            LOWER_TARGET_HUMIDITY = Humidity.createFromRelative(49.0);
            UPPER_TARGET_HUMIDITY = Humidity.createFromRelative(55.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean turnFreshAirOn(SensorValues sensorValues) {
        if (sensorValues.isIndoorHumidityAboveUpperTarget(UPPER_TARGET_HUMIDITY) &&
                sensorValues.isOutdoorHumidityBelowLowerTarget(LOWER_TARGET_HUMIDITY)) {
            return true;
        }

        return sensorValues.isIndoorHumidityBelowLowerTarget(LOWER_TARGET_HUMIDITY) &&
                sensorValues.isOutdoorHumidityAboveUpperTarget(UPPER_TARGET_HUMIDITY);
    }

    public boolean turnHumidityExchangerOn(SensorValues sensorValues) {
        if (sensorValues.isIndoorHumidityAboveUpperTarget(UPPER_TARGET_HUMIDITY) && !sensorValues.isIndoorHumidityAboveOutdoorHumidity()) {
            return true;
        }

        return sensorValues.isIndoorHumidityBelowLowerTarget(LOWER_TARGET_HUMIDITY) && sensorValues.isIndoorHumidityAboveOutdoorHumidity();
    }
}
