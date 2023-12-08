package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;

import java.io.IOException;

class HumidityExchanger {

    private static final AirValue LOWER_TARGET_AIR_VALUE;
    private static final AirValue UPPER_TARGET_AIR_VALUE;

    static {
        try {
            LOWER_TARGET_AIR_VALUE = new AirValue(Temperature.createFromCelsius(22.0), Humidity.createFromRelative(48.0));
            UPPER_TARGET_AIR_VALUE = new AirValue(Temperature.createFromCelsius(23.0), Humidity.createFromRelative(52.0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean turnFreshAirOn(AirValue indoorAirValue, AirValue outdoorAirValue) {
        final double indoorAbsoluteHumidity = indoorAirValue.getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValue.getAbsoluteHumidity();

        if (isIndoorHumidityAboveUpperTarget(indoorAbsoluteHumidity) && isOutdoorHumidityBelowLowerTarget(outdoorAbsoluteHumidity)) {
            return true;
        }

        return isIndoorHumidityBelowLowerTarget(indoorAbsoluteHumidity) && isOutdoorHumidityAboveUpperTarget(outdoorAbsoluteHumidity);
    }

    public boolean turnHumidityExchangerOn(AirValue indoorAirValue, AirValue outdoorAirValue) {
        final double indoorAbsoluteHumidity = indoorAirValue.getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValue.getAbsoluteHumidity();

        if (isIndoorHumidityAboveUpperTarget(indoorAbsoluteHumidity) && indoorAbsoluteHumidity < outdoorAbsoluteHumidity) {
            return true;
        }

        return isIndoorHumidityBelowLowerTarget(indoorAbsoluteHumidity) && indoorAbsoluteHumidity > outdoorAbsoluteHumidity;
    }

    private boolean isIndoorHumidityAboveUpperTarget(double indoorAbsoluteHumidity) {
        final double upperTargetAbsoluteHumidity = UPPER_TARGET_AIR_VALUE.getAbsoluteHumidity();
        return indoorAbsoluteHumidity > upperTargetAbsoluteHumidity;
    }

    private boolean isIndoorHumidityBelowLowerTarget(double indoorAbsoluteHumidity) {
        final double lowerTargetAbsoluteHumidity = LOWER_TARGET_AIR_VALUE.getAbsoluteHumidity();
        return indoorAbsoluteHumidity < lowerTargetAbsoluteHumidity;
    }

    private boolean isOutdoorHumidityBelowLowerTarget(double outdoorAbsoluteHumidity) {
        final double lowerTargetAbsoluteHumidity = LOWER_TARGET_AIR_VALUE.getAbsoluteHumidity();
        return outdoorAbsoluteHumidity < lowerTargetAbsoluteHumidity;
    }

    private boolean isOutdoorHumidityAboveUpperTarget(double outdoorAbsoluteHumidity) {
        final double upperTargetAbsoluteHumidity = UPPER_TARGET_AIR_VALUE.getAbsoluteHumidity();
        return outdoorAbsoluteHumidity > upperTargetAbsoluteHumidity;
    }
}
