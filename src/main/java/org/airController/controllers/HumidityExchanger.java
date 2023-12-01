package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;

import java.io.IOException;

class HumidityExchanger {

    private static final Humidity TARGET_HUMIDITY;

    static {
        try {
            TARGET_HUMIDITY = Humidity.createFromRelative(50.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean turnHumidityExchangerOn(AirValue indoorAirValue, AirValue outdoorAirValue) {
        final double indoorAbsoluteHumidity = indoorAirValue.getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValue.getAbsoluteHumidity();
        final double targetAbsoluteHumidity = TARGET_HUMIDITY.getAbsoluteHumidity(indoorAirValue.getTemperature());

        final double diffIndoorToGoal = indoorAbsoluteHumidity - targetAbsoluteHumidity;
        final double diffOutdoorToGoal = outdoorAbsoluteHumidity - targetAbsoluteHumidity;

        if (numbersWithDifferentSigns(diffIndoorToGoal, diffOutdoorToGoal)) {
            return false;
        }

        return isIndoorCloserToGoal(diffOutdoorToGoal, diffIndoorToGoal);
    }

    private boolean numbersWithDifferentSigns(double diffIndoorToGoal, double diffOutdoorToGoal) {
        return Math.signum(diffIndoorToGoal) * Math.signum(diffOutdoorToGoal) < 0;
    }

    private boolean isIndoorCloserToGoal(double diffOutdoorToGoal, double diffIndoorToGoal) {
        return Math.abs(diffOutdoorToGoal) > Math.abs(diffIndoorToGoal);
    }
}
