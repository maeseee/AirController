package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;

import java.io.IOException;

class HumidityExchanger {

    private static final Humidity LOWER_TARGET_HUMIDITY;
    private static final Humidity UPPER_TARGET_HUMIDITY;

    static {
        try {
            LOWER_TARGET_HUMIDITY = Humidity.createFromRelative(49.0);
            UPPER_TARGET_HUMIDITY = Humidity.createFromRelative(52.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean turnFreshAirOn(AirValue indoorAirValue, AirValue outdoorAirValue) {
        final double indoorAbsoluteHumidity = indoorAirValue.getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValue.getAbsoluteHumidity();
        final double lowerTargetAbsoluteHumidity = LOWER_TARGET_HUMIDITY.getAbsoluteHumidity(indoorAirValue.getTemperature());
        final double upperTargetAbsoluteHumidity = UPPER_TARGET_HUMIDITY.getAbsoluteHumidity(indoorAirValue.getTemperature());

        if (isIndoorHumidityAboveUpperTarget(indoorAbsoluteHumidity, upperTargetAbsoluteHumidity) &&
                isOutdoorHumidityBelowLowerTarget(outdoorAbsoluteHumidity, lowerTargetAbsoluteHumidity)) {
            return true;
        }

        return isIndoorHumidityBelowLowerTarget(indoorAbsoluteHumidity, lowerTargetAbsoluteHumidity) &&
                isOutdoorHumidityAboveUpperTarget(outdoorAbsoluteHumidity, upperTargetAbsoluteHumidity);
    }

    public boolean turnHumidityExchangerOn(AirValue indoorAirValue, AirValue outdoorAirValue) {
        final double indoorAbsoluteHumidity = indoorAirValue.getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValue.getAbsoluteHumidity();
        final double lowerTargetAbsoluteHumidity = LOWER_TARGET_HUMIDITY.getAbsoluteHumidity(indoorAirValue.getTemperature());
        final double upperTargetAbsoluteHumidity = UPPER_TARGET_HUMIDITY.getAbsoluteHumidity(indoorAirValue.getTemperature());

        if (isIndoorHumidityAboveUpperTarget(indoorAbsoluteHumidity, upperTargetAbsoluteHumidity) &&
                indoorAbsoluteHumidity < outdoorAbsoluteHumidity) {
            return true;
        }

        return isIndoorHumidityBelowLowerTarget(indoorAbsoluteHumidity, lowerTargetAbsoluteHumidity) &&
                indoorAbsoluteHumidity > outdoorAbsoluteHumidity;
    }

    private boolean isIndoorHumidityAboveUpperTarget(double indoorAbsoluteHumidity, double upperTargetAbsoluteHumidity) {
        return indoorAbsoluteHumidity > upperTargetAbsoluteHumidity;
    }

    private boolean isIndoorHumidityBelowLowerTarget(double indoorAbsoluteHumidity, double lowerTargetAbsoluteHumidity) {
        return indoorAbsoluteHumidity < lowerTargetAbsoluteHumidity;
    }

    private boolean isOutdoorHumidityBelowLowerTarget(double outdoorAbsoluteHumidity, double lowerTargetAbsoluteHumidity) {
        return outdoorAbsoluteHumidity < lowerTargetAbsoluteHumidity;
    }

    private boolean isOutdoorHumidityAboveUpperTarget(double outdoorAbsoluteHumidity, double upperTargetAbsoluteHumidity) {
        return outdoorAbsoluteHumidity > upperTargetAbsoluteHumidity;
    }
}
