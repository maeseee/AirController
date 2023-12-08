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
        final double lowerTargetAbsoluteHumidity = LOWER_TARGET_AIR_VALUE.getAbsoluteHumidity();
        final double upperTargetAbsoluteHumidity = UPPER_TARGET_AIR_VALUE.getAbsoluteHumidity();

        if (indoorAbsoluteHumidity > upperTargetAbsoluteHumidity && outdoorAbsoluteHumidity < lowerTargetAbsoluteHumidity) {
            return true;
        }

        return indoorAbsoluteHumidity < lowerTargetAbsoluteHumidity && outdoorAbsoluteHumidity > upperTargetAbsoluteHumidity;
    }

    public boolean turnHumidityExchangerOn(AirValue indoorAirValue, AirValue outdoorAirValue) {
        final double indoorAbsoluteHumidity = indoorAirValue.getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValue.getAbsoluteHumidity();
        final double lowerTargetAbsoluteHumidity = LOWER_TARGET_AIR_VALUE.getAbsoluteHumidity();
        final double upperTargetAbsoluteHumidity = UPPER_TARGET_AIR_VALUE.getAbsoluteHumidity();

        if (indoorAbsoluteHumidity > upperTargetAbsoluteHumidity && indoorAbsoluteHumidity < outdoorAbsoluteHumidity) {
            return true;
        }

        return indoorAbsoluteHumidity < lowerTargetAbsoluteHumidity && indoorAbsoluteHumidity > outdoorAbsoluteHumidity;
    }
}
